package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.ejecutivo.EjecutivoRequest;
import com.dosimetros.backend.dto.ejecutivo.EjecutivoResponse;
import com.dosimetros.backend.entity.Ejecutivo;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.EjecutivoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EjecutivoService {

    private final EjecutivoRepository ejecutivoRepository;

    public EjecutivoService(EjecutivoRepository ejecutivoRepository) {
        this.ejecutivoRepository = ejecutivoRepository;
    }

    public List<EjecutivoResponse> listarActivos() {
        return ejecutivoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EjecutivoResponse obtenerPorId(Integer id) {
        Ejecutivo ejecutivo = ejecutivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + id));
        return toResponse(ejecutivo);
    }

    public EjecutivoResponse crear(EjecutivoRequest request) {
        Ejecutivo ejecutivo = new Ejecutivo();
        ejecutivo.setNombre(request.getNombre());
        ejecutivo.setEmail(request.getEmail());
        ejecutivo.setActivo(true);

        return toResponse(ejecutivoRepository.save(ejecutivo));
    }

    public EjecutivoResponse actualizar(Integer id, EjecutivoRequest request) {
        Ejecutivo ejecutivo = ejecutivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + id));

        ejecutivo.setNombre(request.getNombre());
        ejecutivo.setEmail(request.getEmail());

        return toResponse(ejecutivoRepository.save(ejecutivo));
    }

    public void desactivar(Integer id) {
        Ejecutivo ejecutivo = ejecutivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + id));

        ejecutivo.setActivo(false);
        ejecutivoRepository.save(ejecutivo);
    }

    private EjecutivoResponse toResponse(Ejecutivo e) {
        return new EjecutivoResponse(
                e.getId(),
                e.getNombre(),
                e.getEmail(),
                e.getActivo()
        );
    }
}