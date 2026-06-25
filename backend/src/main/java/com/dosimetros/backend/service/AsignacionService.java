package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.asignacion.AsignacionRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.entity.*;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.*;
import org.springframework.stereotype.Service;

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

    public AsignacionResponse crear(AsignacionRequest request) {
        Dosimetro dosimetro = dosimetroRepository.findById(request.getDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + request.getDosimetroId()));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));

        Ejecutivo ejecutivo = ejecutivoRepository.findById(request.getEjecutivoId())
                .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + request.getEjecutivoId()));

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + request.getEmpresaId()));

        TipoPorta tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de porta no encontrado con id: " + request.getTipoPortaId()));

        if (!tipoPorta.getTipoDosimetro().getId().equals(dosimetro.getTipoDosimetro().getId())) {
            throw new IllegalArgumentException("El tipo de porta no es compatible con el tipo de dosímetro");
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setDosimetro(dosimetro);
        asignacion.setCliente(cliente);
        asignacion.setEjecutivo(ejecutivo);
        asignacion.setEmpresa(empresa);
        asignacion.setTipoPorta(tipoPorta);
        asignacion.setTrimestre(request.getTrimestre());
        asignacion.setFechaAsignacion(request.getFechaAsignacion());
        asignacion.setLinkTrello(request.getLinkTrello());

        dosimetro.setEstado("asignado");
        dosimetroRepository.save(dosimetro);

        return toResponse(asignacionRepository.save(asignacion));
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
                a.getTrimestre(),
                a.getFechaAsignacion(),
                a.getLinkTrello()
        );
    }
}