package org.manageSchool.professor;

import org.manageSchool.auth.AuthRepository;
import org.manageSchool.auth.User;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de profesores.
 *
 * No usa archivo propio: delega en authRepository y filtra por rol = PROFESOR.
 * Esto evita duplicar la fuente de verdad (users.json).
 */
public class ProfessorRepository{

    private final AuthRepository authRepository;

    public ProfessorRepository() {
        this(new AuthRepository());
    }

    // constructor para inyección en tests
    public ProfessorRepository(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // devuelve todos los usuarios con el rol PROFESOR, envueltos como Professor
    public List<Professor> findAll() {
        return authRepository.findByRole(Professor.ROL).stream()
                .map(Professor::new)
                .toList();
    }

    // busca un profesor por correo (ignora las mayusculas)
    public Optional<Professor> findByCorreo(String correo){
        return authRepository.findByEmail(correo)
                .filter(u -> Professor.ROL.equalsIgnoreCase(u.getRol()))
                .map(Professor::new);
    }

    // busca un profesor por id
    public Optional<Professor> findById(String id) {
        return findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    // verifica si ya existe profesor con ese correo
    public boolean existsByCorreo(String correo) {
        return findByCorreo(correo).isPresent();
    }


}