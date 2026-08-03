package com.dosimetros.backend.comparador.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapeo de columnas del archivo del cliente a los campos estándar.
 * El usuario define en la interfaz qué columna del cliente corresponde a qué campo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMapping {

    // Índices de columna (0-based) del archivo del cliente
    // -1 significa "esta columna no existe en el archivo"

    private int colRut;                  // RUT (puede estar todo junto)
    private int colRutSinDv;             // RUT sin dígito verificador (si viene separado)
    private int colDv;                   // Dígito verificador (si viene separado)

    private int colNombreCompleto;       // Nombre completo en una celda (-1 si viene separado)
    private int colNombre;               // Solo nombre(s)
    private int colApellidoPaterno;      // Apellido paterno
    private int colApellidoMaterno;      // Apellido materno (-1 si no existe)

    private int colArea;                 // Área
    private int colSede;                 // Sede (-1 si no existe)

    // Fila desde donde comienzan los datos (0-based, para saltar encabezados)
    private int filaInicio;

    // Nombre de la hoja del archivo cliente
    private String nombreHoja;
}
