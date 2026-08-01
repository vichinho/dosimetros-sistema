package com.dosimetros.backend.dto.asignacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de aplicar correcciones a asignaciones desde un Excel editado.
 * La clave de cada fila es el id de la asignación. Si hay cualquier error no
 * se guarda nada (todo o nada) y se conserva el detalle para corregirlo.
 */
public class CorreccionExcelResponse {

    private int totalFilas;
    private int actualizadas;
    private int sinCambios;
    private int fallidas;
    private boolean aplicado = true;
    private List<String> errores = new ArrayList<>();

    public int getTotalFilas() { return totalFilas; }
    public void setTotalFilas(int totalFilas) { this.totalFilas = totalFilas; }

    public int getActualizadas() { return actualizadas; }
    public void setActualizadas(int actualizadas) { this.actualizadas = actualizadas; }

    public int getSinCambios() { return sinCambios; }
    public void setSinCambios(int sinCambios) { this.sinCambios = sinCambios; }

    public int getFallidas() { return fallidas; }
    public void setFallidas(int fallidas) { this.fallidas = fallidas; }

    public boolean isAplicado() { return aplicado; }
    public void setAplicado(boolean aplicado) { this.aplicado = aplicado; }

    public List<String> getErrores() { return errores; }
    public void setErrores(List<String> errores) { this.errores = errores; }
}
