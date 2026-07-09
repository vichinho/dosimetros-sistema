package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Asignacion;
import com.dosimetros.backend.entity.Cliente;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Ejecutivo;
import com.dosimetros.backend.entity.Empresa;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.entity.TipoPorta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifica el filtro de clientes (#16): ejecutivo, empresa (vía asignaciones) y texto. */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ClienteRepositoryTest {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EjecutivoRepository ejecutivoRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private TipoDosimetroRepository tipoDosimetroRepository;
    @Autowired private TipoPortaRepository tipoPortaRepository;
    @Autowired private DosimetroRepository dosimetroRepository;
    @Autowired private AsignacionRepository asignacionRepository;

    private Ejecutivo e1;
    private Empresa emp1;
    private Cliente acme;

    @BeforeEach
    void setUp() {
        e1 = ejecutivo("Juan");
        Ejecutivo e2 = ejecutivo("Pedro");
        emp1 = empresa("Photomat");

        acme = cliente("ACME Salud", e1);
        cliente("Beta Minería", e2);

        // ACME tiene una asignación con la empresa Photomat.
        TipoDosimetro tld = new TipoDosimetro();
        tld.setNombre("TLD");
        tld = tipoDosimetroRepository.save(tld);
        TipoPorta porta = new TipoPorta();
        porta.setNombre("Porta gringo");
        porta.setTipoDosimetro(tld);
        porta = tipoPortaRepository.save(porta);
        Dosimetro d = new Dosimetro();
        d.setNumero(1);
        d.setTipoDosimetro(tld);
        d.setEstado("asignado");
        d.setFechaCreacion(LocalDate.now());
        d = dosimetroRepository.save(d);

        Asignacion a = new Asignacion();
        a.setDosimetro(d);
        a.setCliente(acme);
        a.setEjecutivo(e1);
        a.setEmpresa(emp1);
        a.setTipoPorta(porta);
        a.setTrimestre("2T2025");
        a.setFechaAsignacion(LocalDate.now());
        asignacionRepository.save(a);
    }

    private Ejecutivo ejecutivo(String nombre) {
        Ejecutivo e = new Ejecutivo();
        e.setNombre(nombre);
        e.setActivo(true);
        return ejecutivoRepository.save(e);
    }

    private Empresa empresa(String nombre) {
        Empresa e = new Empresa();
        e.setNombre(nombre);
        e.setActiva(true);
        return empresaRepository.save(e);
    }

    private Cliente cliente(String razon, Ejecutivo ej) {
        Cliente c = new Cliente();
        c.setRazonSocial(razon);
        c.setEjecutivo(ej);
        c.setActivo(true);
        return clienteRepository.save(c);
    }

    @Test
    void filtraPorTextoEnRazonSocial() {
        List<Cliente> r = clienteRepository.filtrar(null, null, "acme");
        assertEquals(1, r.size());
        assertEquals("ACME Salud", r.get(0).getRazonSocial());
    }

    @Test
    void filtraPorEjecutivo() {
        List<Cliente> r = clienteRepository.filtrar(e1.getId(), null, null);
        assertEquals(1, r.size());
        assertEquals(e1.getId(), r.get(0).getEjecutivo().getId());
    }

    @Test
    void filtraPorEmpresaViaAsignaciones() {
        List<Cliente> r = clienteRepository.filtrar(null, emp1.getId(), null);
        assertEquals(1, r.size());
        assertEquals(acme.getId(), r.get(0).getId());
    }

    @Test
    void sinFiltrosDevuelveTodosLosActivos() {
        List<Cliente> r = clienteRepository.filtrar(null, null, null);
        assertTrue(r.size() >= 2);
    }
}
