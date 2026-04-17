package org.manageSchool.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    // ===== isValidEmail =====

    @Test
    @DisplayName("isValidEmail: acepta correo institucional válido")
    void isValidEmail_aceptaCorreoValido() {
        assertTrue(Validator.isValidEmail("admin@colegio.edu.co"));
        assertTrue(Validator.isValidEmail("prof.juan@colegio.edu.co"));
        assertTrue(Validator.isValidEmail("est123@colegio.edu.co"));
    }

    @Test
    @DisplayName("isValidEmail: rechaza correo de otro dominio")
    void isValidEmail_rechazaDominioIncorrecto() {
        assertFalse(Validator.isValidEmail("admin@gmail.com"));
        assertFalse(Validator.isValidEmail("user@colegio.edu.mx"));
        assertFalse(Validator.isValidEmail("user@otro.edu.co"));
    }

    @Test
    @DisplayName("isValidEmail: rechaza null sin lanzar NullPointerException")
    void isValidEmail_rechazaNull() {
        assertDoesNotThrow(() -> assertFalse(Validator.isValidEmail(null)));
    }

    @Test
    @DisplayName("isValidEmail: rechaza cadena vacía y solo espacios")
    void isValidEmail_rechazaVacioYEspacios() {
        assertFalse(Validator.isValidEmail(""));
        assertFalse(Validator.isValidEmail("   "));
    }

    @Test
    @DisplayName("isValidEmail: rechaza texto sin formato de correo")
    void isValidEmail_rechazaTextoSinFormato() {
        assertFalse(Validator.isValidEmail("usuario"));
        assertFalse(Validator.isValidEmail("@colegio.edu.co"));
    }

    // ===== isValidGrade =====

    @Test
    @DisplayName("isValidGrade: acepta notas en rango [0.0, 5.0]")
    void isValidGrade_aceptaRangoValido() {
        assertTrue(Validator.isValidGrade(0.0));
        assertTrue(Validator.isValidGrade(2.5));
        assertTrue(Validator.isValidGrade(5.0));
    }

    @Test
    @DisplayName("isValidGrade: rechaza notas fuera de rango")
    void isValidGrade_rechazaFueraDeRango() {
        assertFalse(Validator.isValidGrade(-0.1));
        assertFalse(Validator.isValidGrade(5.1));
        assertFalse(Validator.isValidGrade(100.0));
        assertFalse(Validator.isValidGrade(-10.0));
    }

    @Test
    @DisplayName("isValidGrade: acepta límites exactos (0.0 y 5.0)")
    void isValidGrade_aceptaLimitesExactos() {
        assertTrue(Validator.isValidGrade(0.0));
        assertTrue(Validator.isValidGrade(5.0));
    }

    // ===== isNotEmpty =====

    @Test
    @DisplayName("isNotEmpty: acepta cadenas con contenido")
    void isNotEmpty_aceptaCadenasConContenido() {
        assertTrue(Validator.isNotEmpty("hola"));
        assertTrue(Validator.isNotEmpty("  texto con espacios  "));
        assertTrue(Validator.isNotEmpty("a"));
    }

    @Test
    @DisplayName("isNotEmpty: rechaza null sin lanzar NullPointerException")
    void isNotEmpty_rechazaNull() {
        assertDoesNotThrow(() -> assertFalse(Validator.isNotEmpty(null)));
    }

    @Test
    @DisplayName("isNotEmpty: rechaza cadena vacía y solo espacios")
    void isNotEmpty_rechazaVacioYEspacios() {
        assertFalse(Validator.isNotEmpty(""));
        assertFalse(Validator.isNotEmpty("   "));
        assertFalse(Validator.isNotEmpty("\t\n"));
    }

    // ===== isValidDateOrEmpty =====

    @Test
    @DisplayName("isValidDateOrEmpty: acepta null y vacío (campo opcional)")
    void isValidDateOrEmpty_aceptaNullYVacio() {
        assertTrue(Validator.isValidDateOrEmpty(null));
        assertTrue(Validator.isValidDateOrEmpty(""));
        assertTrue(Validator.isValidDateOrEmpty("   "));
    }

    @Test
    @DisplayName("isValidDateOrEmpty: acepta formato yyyy-MM-dd")
    void isValidDateOrEmpty_aceptaFormatoCorrecto() {
        assertTrue(Validator.isValidDateOrEmpty("2026-04-17"));
        assertTrue(Validator.isValidDateOrEmpty("2025-01-01"));
    }

    @Test
    @DisplayName("isValidDateOrEmpty: rechaza formato incorrecto")
    void isValidDateOrEmpty_rechazaFormatoIncorrecto() {
        assertFalse(Validator.isValidDateOrEmpty("17-04-2026"));
        assertFalse(Validator.isValidDateOrEmpty("2026/04/17"));
        assertFalse(Validator.isValidDateOrEmpty("abril 17"));
    }
}