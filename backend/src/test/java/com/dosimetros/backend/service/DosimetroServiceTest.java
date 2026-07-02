package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dosimetro.DosimetroResponse;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.TareaRepository;
import com.dosimetros.backend.repository.TipoDosimetroRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DosimetroServiceTest {

    private final DosimetroRepository dosimetroRepository = mock(DosimetroRepository.class);
    private final DosimetroService service = new DosimetroService(
            dosimetroRepository,
            mock(TipoDosimetroRepository.class),
            mock(TipoPortaRepository.class),
            mock(TareaRepository.class),
            mock(AsignacionRepository.class));

    private Dosimetro dosimetro(String estado) {
        TipoDosimetro tipo = new TipoDosimetro();
        tipo.setId(2);
        tipo.setNombre("TLD");
        Dosimetro d = new Dosimetro();
        d.setId(1);
        d.setNumero(1234);
        d.setTipoDosimetro(tipo);
        d.setEstado(estado);
        return d;
    }

    @Test
    void marcarDanadoDejaElDosimetroDanado() {
        Dosimetro d = dosimetro("disponible");
        when(dosimetroRepository.findById(1)).thenReturn(Optional.of(d));
        when(dosimetroRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DosimetroResponse resp = service.marcarDanado(1);

        assertEquals("dañado", resp.getEstado());
    }

    @Test
    void noSePuedeDanarUnDosimetroAsignado() {
        Dosimetro d = dosimetro("asignado");
        when(dosimetroRepository.findById(1)).thenReturn(Optional.of(d));

        assertThrows(IllegalArgumentException.class, () -> service.marcarDanado(1));
        verify(dosimetroRepository, never()).save(any());
    }

    @Test
    void marcarBuenoVuelveADisponible() {
        Dosimetro d = dosimetro("dañado");
        when(dosimetroRepository.findById(1)).thenReturn(Optional.of(d));
        when(dosimetroRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DosimetroResponse resp = service.marcarBueno(1);

        assertEquals("disponible", resp.getEstado());
    }

    @Test
    void marcarBuenoFallaSiNoEstabaDanado() {
        Dosimetro d = dosimetro("disponible");
        when(dosimetroRepository.findById(1)).thenReturn(Optional.of(d));

        assertThrows(IllegalArgumentException.class, () -> service.marcarBueno(1));
        verify(dosimetroRepository, never()).save(any());
    }
}
