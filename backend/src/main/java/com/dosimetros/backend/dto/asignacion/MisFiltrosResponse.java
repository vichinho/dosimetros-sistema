package com.dosimetros.backend.dto.asignacion;

import java.util.List;

/** Opciones disponibles para los filtros de la vista del ejecutivo. */
public class MisFiltrosResponse {

    private List<String> trimestres;
    private List<OpcionResponse> clientes;
    private List<OpcionResponse> portas;

    public MisFiltrosResponse() {
    }

    public MisFiltrosResponse(List<String> trimestres, List<OpcionResponse> clientes,
                              List<OpcionResponse> portas) {
        this.trimestres = trimestres;
        this.clientes = clientes;
        this.portas = portas;
    }

    public List<String> getTrimestres() {
        return trimestres;
    }

    public void setTrimestres(List<String> trimestres) {
        this.trimestres = trimestres;
    }

    public List<OpcionResponse> getClientes() {
        return clientes;
    }

    public void setClientes(List<OpcionResponse> clientes) {
        this.clientes = clientes;
    }

    public List<OpcionResponse> getPortas() {
        return portas;
    }

    public void setPortas(List<OpcionResponse> portas) {
        this.portas = portas;
    }
}
