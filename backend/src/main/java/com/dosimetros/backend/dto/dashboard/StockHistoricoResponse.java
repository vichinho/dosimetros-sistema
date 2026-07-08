package com.dosimetros.backend.dto.dashboard;

import java.time.LocalDate;
import java.util.List;

/**
 * Stock del inventario a una fecha pasada, calculado por la fecha de creación
 * de cada dosímetro (requerimiento #4 del dashboard).
 */
public class StockHistoricoResponse {

    private LocalDate fecha;
    private long total;
    private List<ConteoResponse> porTipoPorta;
    private List<ConteoResponse> porTipoDosimetro;

    public StockHistoricoResponse() {
    }

    public StockHistoricoResponse(LocalDate fecha, long total,
                                  List<ConteoResponse> porTipoPorta,
                                  List<ConteoResponse> porTipoDosimetro) {
        this.fecha = fecha;
        this.total = total;
        this.porTipoPorta = porTipoPorta;
        this.porTipoDosimetro = porTipoDosimetro;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<ConteoResponse> getPorTipoPorta() {
        return porTipoPorta;
    }

    public void setPorTipoPorta(List<ConteoResponse> porTipoPorta) {
        this.porTipoPorta = porTipoPorta;
    }

    public List<ConteoResponse> getPorTipoDosimetro() {
        return porTipoDosimetro;
    }

    public void setPorTipoDosimetro(List<ConteoResponse> porTipoDosimetro) {
        this.porTipoDosimetro = porTipoDosimetro;
    }
}
