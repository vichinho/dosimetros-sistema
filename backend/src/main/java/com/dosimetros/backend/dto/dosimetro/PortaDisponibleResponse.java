package com.dosimetros.backend.dto.dosimetro;

/**
 * Detalle de stock disponible por tipo de porta (HU: detalle de portas disponibles).
 */
public class PortaDisponibleResponse {

    private Integer tipoPortaId;
    private String portaNombre;
    private String tipoDosimetroNombre;
    private Long cantidad;

    public PortaDisponibleResponse() {
    }

    public PortaDisponibleResponse(Integer tipoPortaId, String portaNombre,
                                   String tipoDosimetroNombre, Long cantidad) {
        this.tipoPortaId = tipoPortaId;
        this.portaNombre = portaNombre;
        this.tipoDosimetroNombre = tipoDosimetroNombre;
        this.cantidad = cantidad;
    }

    public Integer getTipoPortaId() {
        return tipoPortaId;
    }

    public void setTipoPortaId(Integer tipoPortaId) {
        this.tipoPortaId = tipoPortaId;
    }

    public String getPortaNombre() {
        return portaNombre;
    }

    public void setPortaNombre(String portaNombre) {
        this.portaNombre = portaNombre;
    }

    public String getTipoDosimetroNombre() {
        return tipoDosimetroNombre;
    }

    public void setTipoDosimetroNombre(String tipoDosimetroNombre) {
        this.tipoDosimetroNombre = tipoDosimetroNombre;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
