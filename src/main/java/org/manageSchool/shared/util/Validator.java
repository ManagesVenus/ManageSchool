package org.manageSchool.shared.util;

import java.util.regex.Pattern;

/**
 * Utilidad de validaciones de entrada del sistema SchoolApp CLI.
 * Centraliza todas las reglas de validación para que no queden
 * repetidas ni dispersas en Services y Controllers.
 * Todas las validaciones son métodos estáticos puros (sin estado).
 * Devuelven boolean: el caller decide qué hacer si la validación falla.
 */
public class Validator {

    /**
     * Patrón de correo institucional.
     * Solo acepta correos que terminen en @colegio.edu.co
     * Ejemplos válidos:   admin@colegio.edu.co, prof.juan@colegio.edu.co
     * Ejemplos inválidos: admin@gmail.com, usuario
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@colegio\\.edu\\.co$");

    // Constructor privado: clase de utilidades, no debe instanciarse.
    private Validator() {}

    /**
     * Valida que el correo pertenezca al dominio institucional @colegio.edu.co.
     *
     * @param email Correo a validar
     * @return true si el correo es válido e institucional; false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida que una nota esté dentro del rango permitido: 0.0 a 5.0 inclusive.
     *
     * @param value Valor de la nota a validar
     * @return true si está en [0.0, 5.0]; false en caso contrario
     */
    public static boolean isValidGrade(double value) {
        return value >= 0.0 && value <= 5.0;
    }

    /**
     * Valida que un String no sea null ni esté vacío (después de trim).
     *
     * @param value Valor a validar
     * @return true si tiene contenido; false si es null o solo espacios
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valida que un String tenga formato de fecha yyyy-MM-dd.
     * También acepta null o vacío porque la fecha límite de una tarea es opcional.
     *
     * @param fecha Fecha a validar (puede ser null o vacío)
     * @return true si es vacío/null (campo opcional) o si cumple el formato
     */
    public static boolean isValidDateOrEmpty(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) return true;
        return fecha.trim().matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
}