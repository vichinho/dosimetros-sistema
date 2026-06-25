package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.tipoporta.TipoPortaRequest;
import com.dosimetros.backend.dto.tipoporta.TipoPortaResponse;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.TipoDosimetroRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoPortaService {

    private final TipoPortaRepository repository;
    private final TipoDosimetroRepository tipoDosimetroRepository;

    public TipoPortaService(TipoPortaRepository repository, TipoDosimetroRepository tipoDosimetroRepository) {
        this.repository = repository;
        this.tipoDosimetroRepository = tipoDosimetroRepository;
    }

    public List<TipoPortaResponse> listar() {
        return repository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TipoPortaResponse> listarPorTipoDosimetro(Integer tipoDosimetroId) {
        return repository.findByTipoDosimetroIdOrderByNombreAsc(tipoDosimetroId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TipoPortaResponse obtenerPorId(Integer id) {
        TipoPorta tp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de porta no encontrado con id: " + id));
        return toResponse(tp);
    }

    public TipoPortaResponse crear(TipoPortaRequest request) {
        TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findById(request.getTipoDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de dosímetro no encontrado con id: " + request.getTipoDosimetroId()));

        TipoPorta tp = new TipoPorta();
        tp.setNombre(request.getNombre());
        tp.setTipoDosimetro(tipoDosimetro);

        return toResponse(repository.save(tp));
    }

    public TipoPortaResponse actualizar(Integer id, TipoPortaRequest request) {
        TipoPorta tp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de porta no encontrado con id: " + id));

        TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findById(request.getTipoDosimetroId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de dosímetro no encontrado con id: " + request.getTipoDosimetroId()));

        tp.setNombre(request.getNombre());
        tp.setTipoDosimetro(tipoDosimetro);

        return toResponse(repository.save(tp));
    }

    public void eliminar(Integer id) {
        TipoPorta tp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de porta no encontrado con id: " + id));
        repository.delete(tp);
    }

    private TipoPortaResponse toResponse(TipoPorta tp) {
        return new TipoPortaResponse(
                tp.getId(),
                tp.getNombre(),
                tp.getTipoDosimetro().getId(),
                tp.getTipoDosimetro().getNombre()
        );
    }
}