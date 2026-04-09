package org.manageSchool.auth;

public record CreateUserRequest(
        String nombre,
        String correo,
        String contrasena,
        String rol
) {}
