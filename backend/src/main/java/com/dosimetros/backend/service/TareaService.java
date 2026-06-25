package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.tarea.TareaRequest;
import com.dosimetros.backend.dto.tarea.TareaResponse;
import com.dosimetros.backend.entity.Tarea;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {

    private final TareaRepository repository;

    public TareaService(TareaRepository repository) {
        this.repository = repository;
    }

    public List<TareaResponse> listar() {
        return repository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TareaResponse obtenerPorId(Integer id) {
        Tarea tarea = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));
        return toResponse(tarea);
    }

    public TareaResponse crear(TareaRequest request) {
        Tarea tarea = new Tarea();
        tarea.setNumeroTarea(request.getNumeroTarea());
        tarea.setFechaCreacion(request.getFechaCreacion());
        tarea.setObservacion(request.getObservacion());

        return toResponse(repository.save(tarea));
    }

    public TareaResponse actualizar(Integer id, TareaRequest request) {
        Tarea tarea = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));

        tarea.setNumeroTarea(request.getNumeroTarea());
        tarea.setFechaCreacion(request.getFechaCreacion());
        tarea.setObservacion(request.getObservacion());

        return toResponse(repository.save(tarea));
    }

    public void eliminar(Integer id) {
        Tarea tarea = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));
        repository.delete(tarea);
    }

    private TareaResponse toResponse(Tarea tarea) {
        return new TareaResponse(
                tarea.getId(),
                tarea.getNumeroTarea(),
                tarea.getFechaCreacion(),
                tarea.getObservacion()
        );
    }
}