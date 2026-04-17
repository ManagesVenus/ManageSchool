package org.manageSchool.professor;

import org.manageSchool.task.TaskRepository;
import org.manageSchool.task.Task;
import org.manageSchool.grade.GradeRepository;
import org.manageSchool.grade.Grade;
import org.manageSchool.shared.AppException;
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
    private final TaskRepository taskRepo;
    private final GradeRepository gradeRepo;

    // Constructor completo (ISSUE-030 requiere acceso a tareas y notas)
    public ProfessorService(ProfessorRepository repo, AuthService authService,
                            TaskRepository taskRepo, GradeRepository gradeRepo) {
        this.repo = repo;
        this.authService = authService;
        this.taskRepo = taskRepo;
        this.gradeRepo = gradeRepo;
    }

    // Constructor retrocompatible (para código que no necesita delete)
    public ProfessorService(ProfessorRepository repo, AuthService authService) {
        this(repo, authService, new TaskRepository(), new GradeRepository());
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

    // ISSUE-028 / CP-PROF-002: lista profesores ordenados alfabéticamente por nombre
    public List<Professor> listAllSorted() {
        return repo.findAll().stream()
                .sorted(java.util.Comparator.comparing(
                        p -> p.getNombre() == null ? "" : p.getNombre().toLowerCase()))
                .toList();
    }

    /**
     * Edita un profesor existente. Solo se permiten cambios en nombre y correo.
     *
     * Si un campo viene null o vacío, se conserva el valor actual.
     * Reglas (CP-PROF-003):
     *  - El nuevo correo debe seguir siendo @colegio.edu.co
     *  - El nuevo correo no puede estar en uso por otro usuario (cualquier rol)
     *
     * @param id            ID del profesor a editar
     * @param nuevoNombre   Nuevo nombre (null o vacío para conservar el actual)
     * @param nuevoCorreo   Nuevo correo (null o vacío para conservar el actual)
     * @return el Professor ya actualizado
     */
    public Professor update(String id, String nuevoNombre, String nuevoCorreo) {
        // 1) Buscar el profesor existente
        Professor profesor = repo.findById(id)
                .orElseThrow(() -> new org.manageSchool.shared.AppException(
                        "Profesor no encontrado."));

        User user = profesor.getUser();

        // 2) Actualizar nombre si se proporcionó
        if (org.manageSchool.shared.util.Validator.isNotEmpty(nuevoNombre)) {
            user.setNombre(nuevoNombre.trim());
        }

        // 3) Actualizar correo si se proporcionó (con validaciones)
        if (org.manageSchool.shared.util.Validator.isNotEmpty(nuevoCorreo)) {
            String correoNormalizado = nuevoCorreo.trim();

            // Validar dominio institucional
            if (!org.manageSchool.shared.util.Validator.isValidEmail(correoNormalizado)) {
                throw new org.manageSchool.shared.AppException(
                        "El correo debe pertenecer al dominio @colegio.edu.co.");
            }

            // Validar que no esté en uso por OTRO usuario (cualquier rol)
            // Si es el mismo correo que ya tenía el profesor, no hay conflicto.
            if (!correoNormalizado.equalsIgnoreCase(user.getCorreo())) {
                java.util.Optional<User> existente =
                        authRepository().findByEmail(correoNormalizado);
                if (existente.isPresent() && !existente.get().getId().equals(user.getId())) {
                    throw new org.manageSchool.shared.AppException(
                            "El correo ya está en uso por otro usuario.");
                }
                user.setCorreo(correoNormalizado);
            }
        }

        // 4) Persistir en users.json (fuente de verdad única)
        authRepository().update(user);

        return new Professor(user);
    }

    // Helper para obtener el AuthRepository desde el ProfessorRepository
    private org.manageSchool.auth.AuthRepository authRepository() {
        return repo.getAuthRepository();
    }

    /**
     * Cuenta tareas y notas asociadas a un profesor antes de eliminar.
     * Retorna un array: [cantidadTareas, cantidadNotas]
     */
    public int[] getDeletionInfo(String profesorId) {
        Professor profesor = repo.findById(profesorId)
                .orElseThrow(() -> new AppException("Profesor no encontrado."));

        List<Task> tareas = taskRepo.findByProfessorId(profesor.getId());
        int totalNotas = 0;
        for (Task t : tareas) {
            totalNotas += gradeRepo.findByTaskId(t.getId()).size();
        }

        return new int[]{ tareas.size(), totalNotas };
    }

    /**
     * Elimina un profesor del sistema (de users.json).
     *
     * Las tareas y notas asociadas permanecen intactas (RN-10):
     * sus campos profesorId quedan apuntando a un usuario inexistente.
     * No se hace eliminación en cascada.
     *
     * @param id ID del profesor a eliminar
     */
    public void delete(String id) {
        Professor profesor = repo.findById(id)
                .orElseThrow(() -> new AppException("Profesor no encontrado."));

        // Eliminar el User subyacente de users.json
        authRepository().deleteById(profesor.getId());
    }
}