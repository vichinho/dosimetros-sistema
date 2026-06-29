package com.dosimetros.backend.dto.asignacion;

import java.util.List;

public class AsignacionMasivaResponse {

    private Integer cantidadSolicitada;
    private Integer cantidadAsignada;
    private List<AsignacionResponse> asignaciones;

    public AsignacionMasivaResponse() {
    }

    public AsignacionMasivaResponse(Integer cantidadSolicitada,
                                    Integer cantidadAsignada,
                                    List<AsignacionResponse> asignaciones) {
        this.cantidadSolicitada = cantidadSolicitada;
        this.cantidadAsignada = cantidadAsignada;
        this.asignaciones = asignaciones;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public Integer getCantidadAsignada() {
        return cantidadAsignada;
    }

    public void setCantidadAsignada(Integer cantidadAsignada) {
        this.cantidadAsignada = cantidadAsignada;
    }

    public List<AsignacionResponse> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<AsignacionResponse> asignaciones) {
        this.asignaciones = asignaciones;
    }
}
