package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.asignacion.AsignacionRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.entity.Asignacion;
import com.dosimetros.backend.entity.Cliente;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Ejecutivo;
import com.dosimetros.backend.entity.Empresa;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.ClienteRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.EjecutivoRepository;
import com.dosimetros.backend.repository.EmpresaRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final DosimetroRepository dosimetroRepository;
    private final ClienteRepository clienteRepository;
    private final EjecutivoRepository ejecutivoRepository;
    private final EmpresaRepository empresaRepository;
    private final TipoPortaRepository tipoPortaRepository;

    public AsignacionService(AsignacionRepository asignacionRepository,
                             DosimetroRepository dosimetroRepository,
                             ClienteRepository clienteRepository,
                             EjecutivoRepository ejecutivoRepository,
                             EmpresaRepository empresaRepository,
                             TipoPortaRepository tipoPortaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.dosimetroRepository = dosimetroRepository;
        this.clienteRepository = clienteRepository;
        this.ejecutivoRepository = ejecutivoRepository;
        this.empresaRepository = empresaRepository;
        this.tipoPortaRepository = tipoPortaRepository;
    }

    public List<AsignacionResponse> listarPorDosimetro(Integer dosimetroId) {
        return asignacionRepository.findByDosimetroIdOrderByFechaAsignacionDesc(dosimetroId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AsignacionResponse crear(AsignacionRequest request) {
        Dosimetro dosimetro = dosimetroRepository.findById(request.getDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dosímetro no encontrado con id: " + request.getDosimetroId()
                ));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + request.getClienteId()
                ));

        Ejecutivo ejecutivo = ejecutivoRepository.findById(request.getEjecutivoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ejecutivo no encontrado con id: " + request.getEjecutivoId()
                ));

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa no encontrada con id: " + request.getEmpresaId()
                ));

        TipoPorta tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de porta no encontrado con id: " + request.getTipoPortaId()
                ));

        if (!"disponible".equalsIgnoreCase(dosimetro.getEstado())) {
            throw new IllegalArgumentException("El dosímetro no está disponible para asignación");
        }

        if (!tipoPorta.getTipoDosimetro().getId().equals(dosimetro.getTipoDosimetro().getId())) {
            throw new IllegalArgumentException("El tipo de porta no es compatible con el tipo de dosímetro");
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setDosimetro(dosimetro);
        asignacion.setCliente(cliente);
        asignacion.setEjecutivo(ejecutivo);
        asignacion.setEmpresa(empresa);
        asignacion.setTipoPorta(tipoPorta);
        asignacion.setTarea(dosimetro.getTarea());
        asignacion.setNumeroBandeja(dosimetro.getNumeroBandeja());
        asignacion.setSlotBandeja(dosimetro.getSlotBandeja());
        asignacion.setTrimestre(request.getTrimestre());
        asignacion.setFechaAsignacion(
                request.getFechaAsignacion() != null ? request.getFechaAsignacion() : LocalDate.now()
        );
        asignacion.setLinkTrello(request.getLinkTrello());

        Asignacion guardada = asignacionRepository.save(asignacion);

        dosimetro.setEstado("asignado");
        dosimetroRepository.save(dosimetro);

        return toResponse(guardada);
    }

    private AsignacionResponse toResponse(Asignacion a) {
        return new AsignacionResponse(
                a.getId(),
                a.getDosimetro().getId(),
                a.getDosimetro().getNumero(),
                a.getCliente().getId(),
                a.getCliente().getRazonSocial(),
                a.getEjecutivo().getId(),
                a.getEjecutivo().getNombre(),
                a.getEmpresa().getId(),
                a.getEmpresa().getNombre(),
                a.getTipoPorta().getId(),
                a.getTipoPorta().getNombre(),
                a.getTarea() != null ? a.getTarea().getId() : null,
                a.getTarea() != null ? a.getTarea().getNumeroTarea() : null,
                a.getNumeroBandeja(),
                a.getSlotBandeja(),
                a.getTrimestre(),
                a.getFechaAsignacion(),
                a.getLinkTrello()
        );
    }
}