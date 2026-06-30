package com.dosimetros.backend.dto.dosimetro;

import java.util.ArrayList;
import java.util.List;

public class ImportacionDosimetrosResponse {

    private int totalFilas;
    private int exitosas;
    private int fallidas;
    private int omitidas;
    private List<String> errores = new ArrayList<>();

    public ImportacionDosimetrosResponse() {
    }

    public int getOmitidas() {
        return omitidas;
    }

    public void setOmitidas(int omitidas) {
        this.omitidas = omitidas;
    }

    public int getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    public int getExitosas() {
        return exitosas;
    }

    public void setExitosas(int exitosas) {
        this.exitosas = exitosas;
    }

    public int getFallidas() {
        return fallidas;
    }

    public void setFallidas(int fallidas) {
        this.fallidas = fallidas;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
}