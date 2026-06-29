package com.dosimetros.backend.dto.dosimetro;

import java.util.List;

/**
 * Agrupa los dosímetros que comparten un mismo número físico (HU #9).
 * El número puede repetirse entre dosímetros; el id interno es único.
 */
public class DuplicadoResponse {

    private Integer numero;
    private Integer cantidad;
    private List<DosimetroResponse> dosimetros;

    public DuplicadoResponse() {
    }

    public DuplicadoResponse(Integer numero, Integer cantidad, List<DosimetroResponse> dosimetros) {
        this.numero = numero;
        this.cantidad = cantidad;
        this.dosimetros = dosimetros;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<DosimetroResponse> getDosimetros() {
        return dosimetros;
    }

    public void setDosimetros(List<DosimetroResponse> dosimetros) {
        this.dosimetros = dosimetros;
    }
}
