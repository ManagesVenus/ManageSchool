package org.manageSchool.shared;


public class AppException extends RuntimeException {

    /**
     * Constructor para errores de lógica de negocio.
     *
     * @param message Mensaje claro en español para mostrar al usuario.
     */
    public AppException(String message) {
        super(message);
    }

    /**
     * Constructor para errores de I/O: envuelve una excepción técnica
     * en una AppException con un mensaje legible.
     *
     * @param message Mensaje claro en español para mostrar al usuario.
     * @param cause   La excepción técnica original (ej. IOException).
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}