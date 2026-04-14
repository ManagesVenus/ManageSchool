package org.manageSchool.professor;

import org.manageSchool.auth.AuthService;
import org.manageSchool.auth.CreateUserRequest;
import org.manageSchool.auth.User;

import java.util.List;

/**
 * Servicio de profesores.
 *
 * Delega la creación de AuthService.createAccount(...) con rol = PROFESOR
 * para reusarlo: validación de correo institucional, validación de duplicacdos,
 * cifrado BCrypt y validación de campos vacíos.
 */

public class ProfessorService {

    private final ProfessorRepository repo;
    private final AuthService authService;

    public ProfessorService(ProfessorRepository repo, AuthService authService) {
        this.repo = repo;
        this.authService = authService;
    }

    /**
     * Crea un nuevo profesor
     *
     * Toda la validación (correo institucional, duplicados, campos vacíos)
     * y el cifrado BCrypt los hace AuthService.createAccount()
     * Aquí solo forzamos el rol PROFESOR y envolvemos el resultado
     */
    public Professor create(String nombre, String correo, String contrasenaTemporal) {
        CreateUserRequest request = new CreateUserRequest(
          nombre, correo,contrasenaTemporal, Professor.ROL
        );
        User nuevoUser = authService.createAccount(request);
        return new Professor(nuevoUser);
    }

    // Lista de todos los profesores registrados
    public List<Professor> listAll () {
        return repo.findAll();
    }
}