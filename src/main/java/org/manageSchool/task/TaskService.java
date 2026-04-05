package org.manageSchool.task;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.JsonFileManager;
import org.manageSchool.shared.util.Validator;
import org.manageSchool.task.Task;
import org.manageSchool.task.TaskRepository;

import java.util.List;
import java.util.Map;

public class TaskService {

    private final TaskRepository repo = new TaskRepository();

    private static final String GRADES_FILE = "grades.json";


    /**
     * @param titulo      Título de la tarea (obligatorio)
     * @param descripcion Descripción o instrucciones (opcional, puede ser null)
     * @param fechaLimite Fecha límite en "yyyy-MM-dd" (opcional, puede ser null)
     * @param materiaId   ID de la materia a la que pertenece (obligatorio)
     * @param profesorId  ID del profesor autenticado (obligatorio)
     * @return La tarea creada con su ID generado
     * @throws AppException si alguna validación falla
     */
    public Task create(String titulo, String descripcion, String fechaLimite,
                       String materiaId, String profesorId) {

        if (!Validator.isNotEmpty(titulo)) {
            throw new AppException("El título es obligatorio.");
        }

        if (!Validator.isNotEmpty(materiaId)) {
            throw new AppException("Debe seleccionar una materia.");
        }

        if (!Validator.isNotEmpty(profesorId)) {
            throw new AppException("Error de sesión: no se identificó al profesor.");
        }

        if (!Validator.isValidDateOrEmpty(fechaLimite)) {
            throw new AppException(
                    "Formato de fecha inválido. Use el formato: yyyy-MM-dd (Ejemplo: 2025-03-01)"
            );
        }

        Task nuevaTarea = Task.create(
                titulo.trim(),
                descripcion != null ? descripcion.trim() : null,
                fechaLimite != null ? fechaLimite.trim() : null,
                materiaId,
                profesorId
        );

        repo.save(nuevaTarea);

        return nuevaTarea;
    }


    /**
     * @param materiaId  ID de la materia
     * @param profesorId ID del profesor autenticado
     * @return Lista de tareas de esa materia creadas por ese profesor
     */
    public List<Task> listBySubject(String materiaId, String profesorId) {
        return repo.findBySubjectId(materiaId).stream()
                .filter(t -> profesorId.equals(t.getProfesorId()))
                .toList();
    }
    /**
     * @param taskId ID de la tarea a actualizar
     * @param titulo Nuevo título (obligatorio)
     * @param descripcion Nueva descripción (opcional)
     * @param fechaLimite Nueva fecha límite en "yyyy-MM-dd" (opcional)
     * @param profesorId ID del profesor autenticado (para verificar autoría)
     * @return La tarea con los datos actualizados
     * @throws AppException si la tarea no existe, el profesor no es el autor, el título está vacío o la fecha tiene formato inválido
     */
    public Task update(String taskId, String titulo, String descripcion,
                       String fechaLimite, String profesorId) {

        Task tarea = repo.findById(taskId)
                .orElseThrow(() -> new AppException("Tarea no encontrada con ID: " + taskId));

        if (!profesorId.equals(tarea.getProfesorId())) {
            throw new AppException("No tienes permiso para editar esta tarea.");
        }

        if (!Validator.isNotEmpty(titulo)) {
            throw new AppException("El título es obligatorio.");
        }

        if (!Validator.isValidDateOrEmpty(fechaLimite)) {
            throw new AppException(
                    "Formato de fecha inválido. Use el formato: yyyy-MM-dd (Ejemplo: 2025-03-01)"
            );
        }

        tarea.setTitulo(titulo.trim());
        tarea.setDescripcion(descripcion != null ? descripcion.trim() : null);
        tarea.setFechaLimite(fechaLimite != null ? fechaLimite.trim() : null);

        repo.update(tarea);

        return tarea;
    }

    /**
     * @param taskId ID de la tarea
     * @return Número de notas asociadas a esa tarea (0 si no tiene ninguna)
     */
    public int countGradesByTask(String taskId) {
        List<Map> grades = JsonFileManager.readAll(GRADES_FILE, Map.class);
        return (int) grades.stream()
                .filter(g -> taskId.equals(g.get("tareaId")))
                .count();
    }

    /**
     * @param taskId     ID de la tarea a eliminar
     * @param profesorId ID del profesor autenticado (para verificar autoría)
     * @throws AppException si la tarea no existe o el profesor no es el autor
     */
    public void delete(String taskId, String profesorId) {

        Task tarea = repo.findById(taskId)
                .orElseThrow(() -> new AppException("Tarea no encontrada con ID: " + taskId));

        if (!profesorId.equals(tarea.getProfesorId())) {
            throw new AppException("No tienes permiso para eliminar esta tarea.");
        }

        List<Map> todasLasNotas = JsonFileManager.readAll(GRADES_FILE, Map.class);

        List<Map> notasRestantes = todasLasNotas.stream()
                .filter(g -> !taskId.equals(g.get("tareaId")))
                .toList();

        JsonFileManager.writeAll(GRADES_FILE, notasRestantes);

        repo.deleteById(taskId);
    }
}