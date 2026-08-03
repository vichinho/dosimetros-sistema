package com.dosimetros.backend.comparador.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Servicio de normalización de RUTs y nombres.
 * Maneja todos los casos de formato inconsistente que llegan de los clientes.
 */
@Service
public class NormalizationService {

    // ─────────────────────────────────────────────
    //  NORMALIZACIÓN DE RUT
    // ─────────────────────────────────────────────

    /**
     * Normaliza un RUT a formato estándar: XXXXXXXX-X (sin puntos, con guión, DV en mayúscula).
     * Maneja los siguientes casos:
     *   - "12.345.678-9"  → "12345678-9"
     *   - "12345678 9"    → "12345678-9"
     *   - "123456789"     → "12345678-9"  (último dígito es el DV)
     *   - "12345678"      → inválido si no hay DV
     *   - "172770385-k"   → marcado como inválido (demasiados dígitos)
     *
     * @param rawRut RUT en cualquier formato
     * @return RUT normalizado o cadena vacía si es inválido/vacío
     */
    public String normalizeRut(String rawRut) {
        if (rawRut == null || rawRut.isBlank()) return "";

        // Quitar espacios y convertir a mayúsculas
        String rut = rawRut.trim().toUpperCase();

        // Quitar puntos
        rut = rut.replace(".", "");

        // Si viene con guión, separar cuerpo y DV
        if (rut.contains("-")) {
            String[] parts = rut.split("-");
            if (parts.length != 2) return buildInvalidRut(rawRut);
            String cuerpo = parts[0].trim().replaceAll("[^0-9]", "");
            String dv = parts[1].trim().replaceAll("[^0-9K]", "");
            if (cuerpo.isEmpty() || dv.isEmpty()) return buildInvalidRut(rawRut);
            return validateAndBuild(cuerpo, dv, rawRut);
        }

        // Sin guión: el último carácter es el DV
        String digits = rut.replaceAll("[^0-9K]", "");
        if (digits.length() < 2) return buildInvalidRut(rawRut);
        String cuerpo = digits.substring(0, digits.length() - 1);
        String dv = digits.substring(digits.length() - 1);
        return validateAndBuild(cuerpo, dv, rawRut);
    }

    /**
     * Normaliza un RUT construido desde dos celdas separadas (cuerpo y DV).
     */
    public String normalizeRutFromParts(String cuerpo, String dv) {
        if (cuerpo == null || cuerpo.isBlank()) return "";
        String c = cuerpo.trim().replaceAll("[^0-9]", "");
        String d = (dv == null) ? "" : dv.trim().toUpperCase().replaceAll("[^0-9K]", "");
        if (d.isEmpty()) {
            // El DV puede estar pegado al final del cuerpo
            return normalizeRut(c);
        }
        return validateAndBuild(c, d, cuerpo + "-" + dv);
    }

    private String validateAndBuild(String cuerpo, String dv, String original) {
        // Validar longitud del cuerpo (7 u 8 dígitos para RUT chileno)
        if (cuerpo.length() < 7 || cuerpo.length() > 8) {
            return buildInvalidRut(original);
        }
        if (!dv.matches("[0-9K]")) {
            return buildInvalidRut(original);
        }
        return cuerpo + "-" + dv;
    }

    private String buildInvalidRut(String original) {
        // Retornamos el RUT marcado para que el sistema lo muestre como alerta
        return "INVALIDO:" + original.trim();
    }

    /**
     * Verifica si un RUT normalizado tiene formato válido.
     */
    public boolean isValidRut(String normalizedRut) {
        return normalizedRut != null
                && !normalizedRut.isBlank()
                && !normalizedRut.startsWith("INVALIDO:")
                && normalizedRut.matches("\\d{7,8}-[0-9K]");
    }

    /**
     * Verifica el dígito verificador de un RUT chileno.
     * Útil para detectar RUTs con dígito verificador incorrecto.
     */
    public boolean verificarDigito(String normalizedRut) {
        if (!isValidRut(normalizedRut)) return false;
        String[] parts = normalizedRut.split("-");
        String cuerpo = parts[0];
        String dvEsperado = parts[1];

        int suma = 0;
        int multiplo = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * multiplo;
            multiplo = (multiplo == 7) ? 2 : multiplo + 1;
        }
        int resto = 11 - (suma % 11);
        String dvCalculado = switch (resto) {
            case 11 -> "0";
            case 10 -> "K";
            default -> String.valueOf(resto);
        };
        return dvCalculado.equals(dvEsperado);
    }

    // ─────────────────────────────────────────────
    //  NORMALIZACIÓN DE NOMBRES
    // ─────────────────────────────────────────────

    /**
     * Normaliza un nombre o texto: sin tildes, en mayúsculas, espacios múltiples reducidos.
     * Ejemplo: "María  José" → "MARIA JOSE"
     */
    public String normalizeName(String name) {
        if (name == null || name.isBlank()) return "";
        // Quitar tildes y caracteres especiales (mantiene la Ñ)
        String normalized = removeDiacritics(name.trim());
        // Convertir a mayúsculas
        normalized = normalized.toUpperCase();
        // Reducir espacios múltiples a uno solo
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    /**
     * Construye el nombre completo normalizado desde partes separadas.
     * Formato resultante: "APELLIDO_P APELLIDO_M NOMBRE(S)"
     * Esto permite comparar con el formato del software que viene como "APELLIDO_P APELLIDO_M NOMBRE"
     */
    public String buildFullNameFromParts(String nombre, String apellidoP, String apellidoM) {
        String n = normalizeName(nombre);
        String ap = normalizeName(apellidoP);
        String am = normalizeName(apellidoM);

        StringBuilder sb = new StringBuilder();
        if (!ap.isEmpty()) sb.append(ap);
        if (!am.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(am);
        }
        if (!n.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(n);
        }
        return sb.toString().trim();
    }

    /**
     * Quita tildes pero preserva la Ñ (importante para nombres chilenos).
     */
    private String removeDiacritics(String text) {
        // Preservar Ñ/ñ antes de normalizar
        String result = text.replace('Ñ', '\u0001').replace('ñ', '\u0002');
        // Normalizar y quitar diacríticos
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Restaurar Ñ/ñ
        result = result.replace('\u0001', 'Ñ').replace('\u0002', 'ñ');
        return result;
    }

    /**
     * Normaliza el nombre del área: sin tildes, mayúsculas, espacios normalizados.
     */
    public String normalizeArea(String area) {
        return normalizeName(area);
    }
}
