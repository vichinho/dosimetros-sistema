package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.cliente.ClienteRequest;
import com.dosimetros.backend.dto.cliente.ClienteResponse;
import com.dosimetros.backend.entity.Cliente;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listarActivos() {
        return clienteRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // HU #3: solo los clientes que tienen asignaciones con el ejecutivo logueado.
    public List<ClienteResponse> listarPorEjecutivo(Integer ejecutivoId) {
        return clienteRepository.findDistinctClientesByEjecutivoId(ejecutivoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse obtenerPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        return toResponse(cliente);
    }

    public ClienteResponse crear(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setRazonSocial(request.getRazonSocial());
        cliente.setNombreCorto(request.getNombreCorto());
        cliente.setActivo(true);

        return toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse actualizar(Integer id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        cliente.setRazonSocial(request.getRazonSocial());
        cliente.setNombreCorto(request.getNombreCorto());

        return toResponse(clienteRepository.save(cliente));
    }

    public void desactivar(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getRazonSocial(),
                cliente.getNombreCorto(),
                cliente.getActivo()
        );
    }
}