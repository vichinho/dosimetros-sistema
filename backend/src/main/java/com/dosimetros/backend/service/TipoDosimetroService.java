package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.tipodosimetro.TipoDosimetroRequest;
import com.dosimetros.backend.dto.tipodosimetro.TipoDosimetroResponse;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.TipoDosimetroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoDosimetroService {

    private final TipoDosimetroRepository repository;

    public TipoDosimetroService(TipoDosimetroRepository repository) {
        this.repository = repository;
    }

    public List<TipoDosimetroResponse> listar() {
        return repository.findAllByOrderByNombreAsc()
                .stream()
                .map(t -> new TipoDosimetroResponse(t.getId(), t.getNombre()))
                .toList();
    }

    public TipoDosimetroResponse obtenerPorId(Integer id) {
        TipoDosimetro t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de dosímetro no encontrado con id: " + id));
        return new TipoDosimetroResponse(t.getId(), t.getNombre());
    }

    public TipoDosimetroResponse crear(TipoDosimetroRequest request) {
        TipoDosimetro t = new TipoDosimetro();
        t.setNombre(request.getNombre());
        t = repository.save(t);
        return new TipoDosimetroResponse(t.getId(), t.getNombre());
    }

    public TipoDosimetroResponse actualizar(Integer id, TipoDosimetroRequest request) {
        TipoDosimetro t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de dosímetro no encontrado con id: " + id));
        t.setNombre(request.getNombre());
        t = repository.save(t);
        return new TipoDosimetroResponse(t.getId(), t.getNombre());
    }

    public void eliminar(Integer id) {
        TipoDosimetro t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de dosímetro no encontrado con id: " + id));
        repository.delete(t);
    }
}