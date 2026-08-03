package com.dosimetros.backend.comparador.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un usuario normalizado, ya sea del software o del listado del cliente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecord {

    // --- Campos del software (archivo base) ---
    private String servicio;
    private String sede;
    private String area;
    private String usuario;
    private String rut;
    private String ubicacion; // PERSONAL, ABDOMINAL, ANILLO, PULSERA, CRISTALINO, TIROIDEO, CONTROL, AMBIENTAL, REFERENCIA

    // --- Campos del cliente ---
    private String nombreCliente;
    private String apellidoPaternoCliente;
    private String apellidoMaternoCliente;
    private String nombreCompletoCliente;
    private String rutCliente;
    private String areaCliente;
    private String sedeCliente;
    private String ubicacionCliente;

    // --- Resultado de la comparación ---
    private ComparisonResult resultado;
    private String alerta;

    public enum ComparisonResult {
        MANTENER,
        QUITAR,
        NUEVO,
        VERIFICAR
    }

    /**
     * Indica si este registro es un dosímetro especial (sin persona asociada).
     */
    public boolean esDosimetroEspecial() {
        // Solo registros con RUT AUSENTE (vacío) son dosímetros especiales
        // (CONTROL, AMBIENTAL, REFERENCIA, DISPONIBLE, etc.).
        // RUT INVÁLIDO (mal escrito) → es un usuario normal, se busca por nombre.
        if (rut == null || rut.isBlank()) return true;
        if (ubicacion == null) return false;
        String t = ubicacion.toUpperCase().trim();
        return t.contains("CONTROL") || t.contains("AMBIENTAL") || t.contains("REFERENCIA");
    }
}
