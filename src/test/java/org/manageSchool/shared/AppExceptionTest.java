package org.manageSchool.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AppExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje: almacena mensaje en español")
    void constructor_conMensaje() {
        AppException ex = new AppException("El nombre no puede estar vacío.");

        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("Constructor con mensaje y causa: envuelve excepción técnica")
    void constructor_conMensajeYCausa() {
        IOException ioEx = new IOException("disk full");
        AppException ex = new AppException(
                "No se pudo escribir en el archivo users.json", ioEx);

        assertEquals("No se pudo escribir en el archivo users.json", ex.getMessage());
        assertSame(ioEx, ex.getCause());
    }

    @Test
    @DisplayName("AppException es RuntimeException (no necesita throws)")
    void esRuntimeException() {
        AppException ex = new AppException("test");

        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    @DisplayName("AppException se puede capturar con catch(AppException)")
    void sePuedeCatchear() {
        assertThrows(AppException.class, () -> {
            throw new AppException("Error de prueba");
        });
    }
}