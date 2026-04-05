package org.manageSchool.task;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.JsonFileManager;
import org.manageSchool.task.Task;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class TaskRepository {

    /** Nombre del archivo JSON donde se persisten las tareas. */
    private static final String FILE = "tasks.json";

    // -------------------------------------------------------------------------
    // LECTURA
    // -------------------------------------------------------------------------

    /**
     * Devuelve todas las tareas almacenadas en tasks.json.
     *
     * Si el archivo no existe (primer inicio), devuelve lista vacía.
     * Delega completamente en JsonFileManager — no conoce detalles de Jackson.
     *
     * @return Lista de todas las tareas; vacía si no hay ninguna.
     */
    public List<Task> findAll() {
        return JsonFileManager.readAll(FILE, Task.class);
    }

    /**
     * Devuelve todas las tareas que pertenecen a una materia específica.
     *
     * Filtra en memoria sobre la lista completa. En v1.0 con pocos datos
     * esto es suficientemente eficiente. En v2.0 sería una query SQL:
     * SELECT * FROM tasks WHERE materia_id = ?
     *
     * @param materiaId ID de la materia a filtrar
     * @return Lista de tareas de esa materia; vacía si no hay ninguna.
     */
    public List<Task> findBySubjectId(String materiaId) {
        return findAll().stream()
                .filter(t -> materiaId.equals(t.getMateriaId()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las tareas creadas por un profesor específico.
     *
     * Se usa para:
     *  - Mostrar al profesor solo SUS tareas (seguridad RNF-06)
     *  - Contar tareas antes de eliminar un profesor (ISSUE-030)
     *
     * @param profesorId ID del profesor
     * @return Lista de tareas del profesor; vacía si no creó ninguna.
     */
    public List<Task> findByProfessorId(String profesorId) {
        return findAll().stream()
                .filter(t -> profesorId.equals(t.getProfesorId()))
                .collect(Collectors.toList());
    }

    /**
     * Busca una tarea por su ID único.
     *
     * Devuelve Optional para forzar al caller a manejar el caso
     * "tarea no encontrada" de forma explícita, sin nulls.
     *
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
     * Guarda una nueva tarea en tasks.json.
     *
     * Patrón leer-modificar-escribir:
     *   1. Lee la lista completa existente
     *   2. Agrega la nueva tarea al final
     *   3. Sobreescribe el archivo con la lista actualizada
     *
     * No verifica duplicados — esa responsabilidad es del TaskService.
     *
     * @param task Tarea a guardar (debe tener ID, titulo, materiaId y profesorId)
     * @throws AppException si no se puede escribir el archivo
     */
    public void save(Task task) {
        List<Task> all = findAll();
        all.add(task);
        JsonFileManager.writeAll(FILE, all);
    }

    /**
     * Actualiza una tarea existente en tasks.json.
     *
     * Busca la tarea por su ID y la reemplaza en la posición correcta
     * de la lista. Si el ID no existe, la lista no cambia (falla silenciosa
     * intencional — el Service valida la existencia antes de llamar update).
     *
     * Patrón leer-modificar-escribir:
     *   1. Lee la lista completa
     *   2. Reemplaza el elemento cuyo id coincide con el de la tarea recibida
     *   3. Sobreescribe el archivo
     *
     * @param task Tarea con los datos actualizados (debe mantener el mismo id)
     * @throws AppException si no se puede escribir el archivo
     */
    public void update(Task task) {
        List<Task> all = findAll();
        // replaceAll itera la lista y reemplaza cada elemento por el resultado
        // del lambda. Si el id coincide, usa la task nueva; si no, conserva la original.
        all.replaceAll(t -> t.getId().equals(task.getId()) ? task : t);
        JsonFileManager.writeAll(FILE, all);
    }

    /**
     * Elimina una tarea de tasks.json por su ID.
     *
     * Patrón leer-modificar-escribir:
     *   1. Lee la lista completa
     *   2. Filtra fuera el elemento con el ID dado
     *   3. Sobreescribe el archivo con la lista filtrada
     *
     * Si el ID no existe, el archivo queda igual (no lanza excepción).
     * El Service valida la existencia antes de llamar deleteById.
     *
     * @param id ID de la tarea a eliminar
     * @throws AppException si no se puede escribir el archivo
     */
    public void deleteById(String id) {
        List<Task> all = findAll();
        all.removeIf(t -> id.equals(t.getId()));
        JsonFileManager.writeAll(FILE, all);
    }
}