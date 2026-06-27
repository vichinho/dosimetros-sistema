package com.dosimetros.backend.dto.dosimetro;

public class ActualizarTipoPortaRangoResponse {

    private int dosimetrosActualizados;

    public ActualizarTipoPortaRangoResponse() {}

    public ActualizarTipoPortaRangoResponse(int dosimetrosActualizados) {
        this.dosimetrosActualizados = dosimetrosActualizados;
    }

    public int getDosimetrosActualizados() { return dosimetrosActualizados; }
    public void setDosimetrosActualizados(int dosimetrosActualizados) { this.dosimetrosActualizados = dosimetrosActualizados; }
}
