package com.dosimetros.backend.dto.dashboard;

import java.time.LocalDate;
import java.util.List;

/**
 * Stock histórico "a una fecha" (HU dashboard #4): cuántos dosímetros existían
 * en el inventario a esa fecha, desglosados por tipo de porta.
 */
public class StockHistoricoResponse {

    private LocalDate fecha;
    private long total;
    private List<ConteoClaveResponse> porPorta;

    public StockHistoricoResponse() {
    }

    public StockHistoricoResponse(LocalDate fecha, long total, List<ConteoClaveResponse> porPorta) {
        this.fecha = fecha;
        this.total = total;
        this.porPorta = porPorta;
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

    public List<ConteoClaveResponse> getPorPorta() {
        return porPorta;
    }

    public void setPorPorta(List<ConteoClaveResponse> porPorta) {
        this.porPorta = porPorta;
    }
}
