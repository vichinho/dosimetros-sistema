package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroDetalleResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroRequest;
import com.dosimetros.backend.dto.dosimetro.DosimetroResponse;
import com.dosimetros.backend.entity.Asignacion;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Tarea;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.TareaRepository;
import com.dosimetros.backend.repository.TipoDosimetroRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DosimetroService {

    private final DosimetroRepository dosimetroRepository;
    private final TipoDosimetroRepository tipoDosimetroRepository;
    private final TipoPortaRepository tipoPortaRepository;
    private final TareaRepository tareaRepository;
    private final AsignacionRepository asignacionRepository;

    public DosimetroService(DosimetroRepository dosimetroRepository,
                            TipoDosimetroRepository tipoDosimetroRepository,
                            TipoPortaRepository tipoPortaRepository,
                            TareaRepository tareaRepository,
                            AsignacionRepository asignacionRepository) {
        this.dosimetroRepository = dosimetroRepository;
        this.tipoDosimetroRepository = tipoDosimetroRepository;
        this.tipoPortaRepository = tipoPortaRepository;
        this.tareaRepository = tareaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    public List<DosimetroResponse> listarDisponibles() {
        return dosimetroRepository.findByEstadoOrderByNumeroAsc("disponible")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DosimetroResponse> filtrarStock(Integer tipoDosimetroId, String estado) {
        String estadoFinal = (estado == null || estado.isBlank()) ? "disponible" : estado;

        return dosimetroRepository.filtrar(tipoDosimetroId, estadoFinal)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DosimetroResponse> buscarPorNumero(Integer numero) {
        return dosimetroRepository.findByNumeroOrderByIdAsc(numero)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DosimetroDetalleResponse> buscarDetallePorNumero(Integer numero) {
        List<Dosimetro> dosimetros = dosimetroRepository.findByNumeroOrderByIdAsc(numero);

        if (dosimetros.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron dosímetros con número: " + numero);
        }

        return dosimetros.stream()
                .map(this::toDetalleResponse)
                .toList();
    }

    public DosimetroResponse obtenerPorId(Integer id) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));
        return toResponse(dosimetro);
    }

    public DosimetroResponse crear(DosimetroRequest request) {
        TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findById(request.getTipoDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de dosímetro no encontrado con id: " + request.getTipoDosimetroId()
                ));

        TipoPorta tipoPorta = null;
        if (request.getTipoPortaId() != null) {
            tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de porta no encontrado con id: " + request.getTipoPortaId()
                    ));

            if (!tipoPorta.getTipoDosimetro().getId().equals(tipoDosimetro.getId())) {
                throw new IllegalArgumentException("El tipo de porta no es compatible con el tipo de dosímetro");
            }
        }

        Tarea tarea = null;
        if ("OSL".equalsIgnoreCase(tipoDosimetro.getNombre())) {
            request.setTareaId(null);
            request.setNumeroBandeja(null);
            request.setSlotBandeja(null);
        } else if (request.getTareaId() != null) {
            tarea = tareaRepository.findById(request.getTareaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarea no encontrada con id: " + request.getTareaId()
                    ));
        }

        Dosimetro dosimetro = new Dosimetro();
        dosimetro.setNumero(request.getNumero());
        dosimetro.setTipoDosimetro(tipoDosimetro);
        dosimetro.setTipoPorta(tipoPorta);
        dosimetro.setTarea(tarea);
        dosimetro.setNumeroBandeja(request.getNumeroBandeja());
        dosimetro.setSlotBandeja(request.getSlotBandeja());
        dosimetro.setEstado(request.getEstado() != null ? request.getEstado() : "disponible");
        dosimetro.setObservacion(request.getObservacion());

        return toResponse(dosimetroRepository.save(dosimetro));
    }

    public DosimetroResponse actualizar(Integer id, DosimetroRequest request) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));

        TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findById(request.getTipoDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de dosímetro no encontrado con id: " + request.getTipoDosimetroId()
                ));

        TipoPorta tipoPorta = null;
        if (request.getTipoPortaId() != null) {
            tipoPorta = tipoPortaRepository.findById(request.getTipoPortaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de porta no encontrado con id: " + request.getTipoPortaId()
                    ));

            if (!tipoPorta.getTipoDosimetro().getId().equals(tipoDosimetro.getId())) {
                throw new IllegalArgumentException("El tipo de porta no es compatible con el tipo de dosímetro");
            }
        }

        Tarea tarea = null;
        if ("OSL".equalsIgnoreCase(tipoDosimetro.getNombre())) {
            request.setTareaId(null);
            request.setNumeroBandeja(null);
            request.setSlotBandeja(null);
        } else if (request.getTareaId() != null) {
            tarea = tareaRepository.findById(request.getTareaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarea no encontrada con id: " + request.getTareaId()
                    ));
        }

        dosimetro.setNumero(request.getNumero());
        dosimetro.setTipoDosimetro(tipoDosimetro);
        dosimetro.setTipoPorta(tipoPorta);
        dosimetro.setTarea(tarea);
        dosimetro.setNumeroBandeja(request.getNumeroBandeja());
        dosimetro.setSlotBandeja(request.getSlotBandeja());
        dosimetro.setEstado(request.getEstado() != null ? request.getEstado() : dosimetro.getEstado());
        dosimetro.setObservacion(request.getObservacion());

        return toResponse(dosimetroRepository.save(dosimetro));
    }

    @Transactional
    public void liberar(Integer id) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));

        if ("baja".equalsIgnoreCase(dosimetro.getEstado())) {
            throw new IllegalArgumentException("No se puede liberar un dosímetro dado de baja");
        }

        dosimetro.setEstado("disponible");
        dosimetroRepository.save(dosimetro);
    }

    public void darDeBaja(Integer id, String observacion) {
        Dosimetro dosimetro = dosimetroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dosímetro no encontrado con id: " + id));

        dosimetro.setEstado("baja");
        dosimetro.setObservacion(observacion);
        dosimetroRepository.save(dosimetro);
    }

    private DosimetroResponse toResponse(Dosimetro d) {
        return new DosimetroResponse(
                d.getId(),
                d.getNumero(),
                d.getTipoDosimetro().getId(),
                d.getTipoDosimetro().getNombre(),
                d.getTipoPorta() != null ? d.getTipoPorta().getId() : null,
                d.getTipoPorta() != null ? d.getTipoPorta().getNombre() : null,
                d.getTarea() != null ? d.getTarea().getId() : null,
                d.getTarea() != null ? d.getTarea().getNumeroTarea() : null,
                d.getNumeroBandeja(),
                d.getSlotBandeja(),
                d.getEstado(),
                d.getObservacion()
        );
    }

    private DosimetroDetalleResponse toDetalleResponse(Dosimetro d) {
        List<AsignacionResponse> asignaciones = asignacionRepository
                .findByDosimetroIdOrderByFechaAsignacionDesc(d.getId())
                .stream()
                .map(this::toAsignacionResponse)
                .toList();

        return new DosimetroDetalleResponse(
                d.getId(),
                d.getNumero(),
                d.getTipoDosimetro().getId(),
                d.getTipoDosimetro().getNombre(),
                d.getTipoPorta() != null ? d.getTipoPorta().getId() : null,
                d.getTipoPorta() != null ? d.getTipoPorta().getNombre() : null,
                d.getTarea() != null ? d.getTarea().getId() : null,
                d.getTarea() != null ? d.getTarea().getNumeroTarea() : null,
                d.getNumeroBandeja(),
                d.getSlotBandeja(),
                d.getEstado(),
                d.getObservacion(),
                asignaciones
        );
    }

    private AsignacionResponse toAsignacionResponse(Asignacion a) {
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