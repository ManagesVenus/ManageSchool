package org.manageSchool.task;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.JsonFileManager;
import org.manageSchool.task.Task;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class TaskRepository {

    private static final String FILE = "tasks.json";

    /**
     * @return Lista de todas las tareas; vacía si no hay ninguna.
     */
    public List<Task> findAll() {
        return JsonFileManager.readAll(FILE, Task.class);
    }

    /**
     * @param materiaId ID de la materia a filtrar
     * @return Lista de tareas de esa materia; vacía si no hay ninguna.
     */
    public List<Task> findBySubjectId(String materiaId) {
        return findAll().stream()
                .filter(t -> materiaId.equals(t.getMateriaId()))
                .collect(Collectors.toList());
    }

    /**
     * @param profesorId ID del profesor
     * @return Lista de tareas del profesor; vacía si no creó ninguna.
     */
    public List<Task> findByProfessorId(String profesorId) {
        return findAll().stream()
                .filter(t -> profesorId.equals(t.getProfesorId()))
                .collect(Collectors.toList());
    }

    /**
     * @param id ID de la tarea a buscar
     * @return Optional con la tarea si existe; Optional.empty() si no.
     */
    public Optional<Task> findById(String id) {
        return findAll().stream()
                .filter(t -> id.equals(t.getId()))
                .findFirst();
    }

    // -------------------------------------------------------------------------
    // ESCRITURA
    // -------------------------------------------------------------------------

    /**
     * @param task Tarea a guardar (debe tener ID, titulo, materiaId y profesorId)
     * @throws AppException si no se puede escribir el archivo
     */
    public void save(Task task) {
        List<Task> all = findAll();
        all.add(task);
        JsonFileManager.writeAll(FILE, all);
    }

    /**
     * @param task Tarea con los datos actualizados (debe mantener el mismo id)
     * @throws AppException si no se puede escribir el archivo
     */
    public void update(Task task) {
        List<Task> all = findAll();

        all.replaceAll(t -> t.getId().equals(task.getId()) ? task : t);
        JsonFileManager.writeAll(FILE, all);
    }

    /**
     * @param id ID de la tarea a eliminar
     * @throws AppException si no se puede escribir el archivo
     */
    public void deleteById(String id) {
        List<Task> all = findAll();
        all.removeIf(t -> id.equals(t.getId()));
        JsonFileManager.writeAll(FILE, all);
    }
}