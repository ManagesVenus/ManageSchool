package org.manageSchool.auth;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.Validator;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final AuthRepository repo;

    public AuthService(AuthRepository repo) {
        this.repo = repo;
    }

    // Crea el admin por defecto si no existe
    public void seedDefaultAdmin() {
        if (repo.findByEmail("admin@colegio.edu.co").isPresent()) return;

        String hashContrasena = BCrypt.hashpw("Admin2026", BCrypt.gensalt());
        User admin = User.crear("Administrador", "admin@colegio.edu.co", hashContrasena, "ADMIN");
        repo.save(admin);
    }

    // Autentica un usuario. Lanza AppException si las credenciales son inválidas.
    public User login(String correo, String contrasena) {
        User user = repo.findByEmail(correo)
                .orElseThrow(() -> new AppException("Correo no registrado en el sistema."));

        if (!BCrypt.checkpw(contrasena, user.getContrasena())) {
            throw new AppException("Contraseña incorrecta.");
        }

        if (!user.isActivo()) {
            throw new AppException("Esta cuenta está desactivada.");
        }

        System.out.println("Bienvenido "+user.getNombre()+"!");
        return user;
    }

    // Crea una cuenta nueva. Valida correo institucional, duplicados y campos vacíos.
    public User createAccount(CreateUserRequest request) {
        if (!Validator.isNotEmpty(request.nombre())) {
            throw new AppException("El nombre no puede estar vacío.");
        }

        if (!Validator.isNotEmpty(request.correo())) {
            throw new AppException("El correo no puede estar vacío.");
        }

        if (!Validator.isValidEmail(request.correo())) {
            throw new AppException("El correo debe pertenecer al dominio @colegio.edu.co.");
        }

        if (!Validator.isNotEmpty(request.contrasena())) {
            throw new AppException("La contraseña no puede estar vacía.");
        }

        if (!Validator.isNotEmpty(request.rol())) {
            throw new AppException("El rol no puede estar vacío.");
        }

        if (repo.existsByEmail(request.correo())) {
            throw new AppException("Ya existe una cuenta con ese correo.");
        }

        String hashContrasena = BCrypt.hashpw(request.contrasena(), BCrypt.gensalt());
        User nuevoUsuario = User.crear(request.nombre(), request.correo(), hashContrasena, request.rol().toUpperCase());
        repo.save(nuevoUsuario);

        return nuevoUsuario;
    }
}