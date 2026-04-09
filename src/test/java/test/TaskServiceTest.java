package test;


import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.JsonFileManager;
import org.manageSchool.task.Task;
import org.manageSchool.task.TaskService;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TaskServiceTest {

    private static final String PROF_ID      = "prof-001";
    private static final String OTRO_PROF_ID = "prof-999";
    private static final String MATERIA_ESP  = "mat-espanol";
    private static final String MATERIA_MAT  = "mat-matematicas";

    private Path       tempDir;
    private String     originalUserDir;
    private TaskService service;

    @BeforeEach
    void setUp() throws IOException {
        originalUserDir = System.getProperty("user.dir");
        tempDir = Files.createTempDirectory("schoolapp-task-test-");
        System.setProperty("user.dir", tempDir.toString());
        Files.createDirectories(tempDir.resolve("data"));
        service = new TaskService();
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setProperty("user.dir", originalUserDir);
        deleteRecursively(tempDir.toFile());
    }

    @Test
    @DisplayName("CP-TASK-001 | S1 | Crea tarea con todos los campos")
    void create_allFields_success() {
        Task result = service.create("Taller de álgebra", "Cap 5", "2025-03-01", MATERIA_MAT, PROF_ID);

        assertNotNull(result);
        assertFalse(result.getId().isBlank());
        assertEquals("Taller de álgebra", result.getTitulo());
        assertEquals("Cap 5",             result.getDescripcion());
        assertEquals("2025-03-01",        result.getFechaLimite());
        assertEquals(MATERIA_MAT,         result.getMateriaId());
        assertEquals(PROF_ID,             result.getProfesorId());
    }

    @Test
    @DisplayName("CP-TASK-001 | S1 | La tarea persiste en tasks.json")
    void create_persistsInJson() {
        service.create("Taller de álgebra", "Cap 5", "2025-03-01", MATERIA_MAT, PROF_ID);

        List<Task> fromFile = JsonFileManager.readAll("tasks.json", Task.class);
        assertEquals(1, fromFile.size());
        assertEquals("Taller de álgebra", fromFile.get(0).getTitulo());
    }

    @Test
    @DisplayName("CP-TASK-001 | S1 | Descripción null aceptada (campo opcional)")
    void create_nullDescription_accepted() {
        Task result = service.create("Quiz", null, "2025-03-01", MATERIA_MAT, PROF_ID);
        assertNull(result.getDescripcion());
    }

    @Test
    @DisplayName("CP-TASK-001 | S1 | Fecha null aceptada (campo opcional)")
    void create_nullFecha_accepted() {
        Task result = service.create("Quiz", "Desc", null, MATERIA_MAT, PROF_ID);
        assertNull(result.getFechaLimite());
    }

    @Test
    @DisplayName("CP-TASK-001 | S1 | Cada tarea recibe UUID único")
    void create_uniqueUUIDs() {
        Task t1 = service.create("Tarea 1", null, null, MATERIA_MAT, PROF_ID);
        Task t2 = service.create("Tarea 2", null, null, MATERIA_MAT, PROF_ID);
        assertNotEquals(t1.getId(), t2.getId());
    }

    @Test
    @DisplayName("CP-TASK-001 | S1 | Título se trimea")
    void create_trimsTitulo() {
        Task result = service.create("  Taller de álgebra  ", null, null, MATERIA_MAT, PROF_ID);
        assertEquals("Taller de álgebra", result.getTitulo());
    }

    @Test
    @DisplayName("CP-TASK-001 | S2 | Lanza AppException si título es null")
    void create_throwsOnNullTitulo() {
        AppException ex = assertThrows(AppException.class, () ->
                service.create(null, "Desc", "2025-03-01", MATERIA_MAT, PROF_ID));
        assertEquals("El título es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-001 | S2 | Lanza AppException si título está vacío")
    void create_throwsOnEmptyTitulo() {
        AppException ex = assertThrows(AppException.class, () ->
                service.create("", "Desc", "2025-03-01", MATERIA_MAT, PROF_ID));
        assertEquals("El título es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-001 | S2 | Lanza AppException si título es solo espacios")
    void create_throwsOnBlankTitulo() {
        AppException ex = assertThrows(AppException.class, () ->
                service.create("   ", "Desc", "2025-03-01", MATERIA_MAT, PROF_ID));
        assertEquals("El título es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-001 | Lanza AppException si materiaId es null")
    void create_throwsOnNullMateriaId() {
        AppException ex = assertThrows(AppException.class, () ->
                service.create("Taller", "Desc", null, null, PROF_ID));
        assertEquals("Debe seleccionar una materia.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-001 | Lanza AppException si fecha tiene formato inválido")
    void create_throwsOnInvalidDate() {
        AppException ex = assertThrows(AppException.class, () ->
                service.create("Taller", "Desc", "01-03-2025", MATERIA_MAT, PROF_ID));
        assertTrue(ex.getMessage().contains("Formato de fecha inválido"));
    }

    @Test
    @DisplayName("CP-TASK-001 | No persiste si la validación falla")
    void create_doesNotPersistOnValidationFailure() {
        assertThrows(AppException.class, () ->
                service.create(null, "Desc", null, MATERIA_MAT, PROF_ID));
        assertTrue(JsonFileManager.readAll("tasks.json", Task.class).isEmpty());
    }

    @Test
    @DisplayName("CP-TASK-002 | Lista 3 tareas de la materia Español")
    void listBySubject_returnsThreeTasks() {
        service.create("Dictado 1",    null, "2025-02-01", MATERIA_ESP, PROF_ID);
        service.create("Lectura comp", null, "2025-02-15", MATERIA_ESP, PROF_ID);
        service.create("Redacción",    null, "2025-03-01", MATERIA_ESP, PROF_ID);

        List<Task> result = service.listBySubject(MATERIA_ESP, PROF_ID);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getTitulo().equals("Dictado 1")));
        assertTrue(result.stream().anyMatch(t -> t.getTitulo().equals("Lectura comp")));
        assertTrue(result.stream().anyMatch(t -> t.getTitulo().equals("Redacción")));
    }

    @Test
    @DisplayName("CP-TASK-002 | Solo lista tareas del profesor autenticado (RNF-06)")
    void listBySubject_isolatesByProfessor() {
        service.create("Tarea prof 1a", null, null, MATERIA_ESP, PROF_ID);
        service.create("Tarea prof 1b", null, null, MATERIA_ESP, PROF_ID);
        service.create("Tarea prof 2",  null, null, MATERIA_ESP, OTRO_PROF_ID);

        List<Task> result = service.listBySubject(MATERIA_ESP, PROF_ID);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> PROF_ID.equals(t.getProfesorId())));
    }

    @Test
    @DisplayName("CP-TASK-002 | Devuelve lista vacía si no hay tareas en esa materia")
    void listBySubject_returnsEmptyWhenNone() {
        List<Task> result = service.listBySubject(MATERIA_ESP, PROF_ID);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("CP-TASK-002 | No mezcla tareas de distintas materias")
    void listBySubject_doesNotMixSubjects() {
        service.create("Tarea Español",     null, null, MATERIA_ESP, PROF_ID);
        service.create("Tarea Matemáticas", null, null, MATERIA_MAT, PROF_ID);

        List<Task> result = service.listBySubject(MATERIA_ESP, PROF_ID);

        assertEquals(1, result.size());
        assertEquals("Tarea Español", result.get(0).getTitulo());
    }

    @Test
    @DisplayName("CP-TASK-003 | Edita la fecha límite de 2025-03-01 a 2025-03-10")
    void update_changesFechaLimite() {
        Task original = service.create("Taller de álgebra", "Cap 5", "2025-03-01", MATERIA_MAT, PROF_ID);

        Task actualizada = service.update(original.getId(), "Taller de álgebra", "Cap 5", "2025-03-10", PROF_ID);

        assertEquals("2025-03-10", actualizada.getFechaLimite());
    }

    @Test
    @DisplayName("CP-TASK-003 | El cambio persiste en tasks.json")
    void update_persistsInJson() {
        Task original = service.create("Taller de álgebra", "Cap 5", "2025-03-01", MATERIA_MAT, PROF_ID);
        service.update(original.getId(), "Taller de álgebra", "Cap 5", "2025-03-10", PROF_ID);

        List<Task> fromFile = JsonFileManager.readAll("tasks.json", Task.class);
        assertEquals("2025-03-10", fromFile.get(0).getFechaLimite());
    }

    @Test
    @DisplayName("CP-TASK-003 | Edita el título correctamente")
    void update_changesTitulo() {
        Task original = service.create("Título viejo", null, null, MATERIA_MAT, PROF_ID);
        Task actualizada = service.update(original.getId(), "Título nuevo", null, null, PROF_ID);
        assertEquals("Título nuevo", actualizada.getTitulo());
    }

    @Test
    @DisplayName("CP-TASK-003 | Lanza AppException si la tarea no existe")
    void update_throwsOnNonExistentTask() {
        AppException ex = assertThrows(AppException.class, () ->
                service.update("id-inexistente", "Título", null, null, PROF_ID));
        assertTrue(ex.getMessage().contains("Tarea no encontrada"));
    }

    @Test
    @DisplayName("CP-TASK-003 | Lanza AppException si el profesor no es el autor (RNF-06)")
    void update_throwsOnWrongProfessor() {
        Task original = service.create("Tarea", null, null, MATERIA_MAT, PROF_ID);
        AppException ex = assertThrows(AppException.class, () ->
                service.update(original.getId(), "Nuevo título", null, null, OTRO_PROF_ID));
        assertEquals("No tienes permiso para editar esta tarea.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-003 | Lanza AppException si el nuevo título está vacío")
    void update_throwsOnEmptyTitulo() {
        Task original = service.create("Tarea", null, null, MATERIA_MAT, PROF_ID);
        AppException ex = assertThrows(AppException.class, () ->
                service.update(original.getId(), "", null, null, PROF_ID));
        assertEquals("El título es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-003 | Lanza AppException si la nueva fecha tiene formato inválido")
    void update_throwsOnInvalidDate() {
        Task original = service.create("Examen", null, null, MATERIA_MAT, PROF_ID);
        AppException ex = assertThrows(AppException.class, () ->
                service.update(original.getId(), "Examen", null, "31/12/2025", PROF_ID));
        assertTrue(ex.getMessage().contains("Formato de fecha inválido"));
    }

    @Test
    @DisplayName("CP-TASK-004 | S1 | Elimina tarea sin notas — no aparece en la lista")
    void delete_noGrades_removedFromList() {
        Task tarea = service.create("Quiz de ortografía", null, null, MATERIA_ESP, PROF_ID);
        service.delete(tarea.getId(), PROF_ID);
        assertTrue(service.listBySubject(MATERIA_ESP, PROF_ID).isEmpty());
    }

    @Test
    @DisplayName("CP-TASK-004 | S1 | Elimina tarea sin notas — se borra de tasks.json")
    void delete_noGrades_removedFromJson() {
        Task tarea = service.create("Quiz de ortografía", null, null, MATERIA_ESP, PROF_ID);
        service.delete(tarea.getId(), PROF_ID);
        assertTrue(JsonFileManager.readAll("tasks.json", Task.class).isEmpty());
    }

    @Test
    @DisplayName("CP-TASK-004 | S1 | countGradesByTask devuelve 0 sin notas")
    void countGrades_returnsZero() {
        Task tarea = service.create("Quiz sin notas", null, null, MATERIA_ESP, PROF_ID);
        assertEquals(0, service.countGradesByTask(tarea.getId()));
    }

    @Test
    @DisplayName("CP-TASK-004 | S2 | countGradesByTask cuenta 10 notas correctamente")
    void countGrades_returnsTen() {
        Task tarea = service.create("Parcial 1", null, null, MATERIA_ESP, PROF_ID);
        crearNotasEnArchivo(tarea.getId(), 10);
        assertEquals(10, service.countGradesByTask(tarea.getId()));
    }

    @Test
    @DisplayName("CP-TASK-004 | S2 | Elimina tarea con notas y borra las notas en cascada (RN-09b)")
    void delete_withGrades_cascadeDeletesGrades() {
        Task tarea = service.create("Parcial 1", null, null, MATERIA_ESP, PROF_ID);
        crearNotasEnArchivo(tarea.getId(), 10);

        service.delete(tarea.getId(), PROF_ID);

        assertTrue(JsonFileManager.readAll("tasks.json", Task.class).isEmpty());
        assertEquals(0, service.countGradesByTask(tarea.getId()));
    }

    @Test
    @DisplayName("CP-TASK-004 | S2 | Solo elimina notas de la tarea borrada, no las de otras")
    void delete_onlyDeletesTargetTaskGrades() {
        Task tarea1 = service.create("Parcial 1", null, null, MATERIA_ESP, PROF_ID);
        Task tarea2 = service.create("Parcial 2", null, null, MATERIA_ESP, PROF_ID);
        crearNotasEnArchivo(tarea1.getId(), 3);
        crearNotasEnArchivo(tarea2.getId(), 3);

        service.delete(tarea1.getId(), PROF_ID);

        assertEquals(3, service.countGradesByTask(tarea2.getId()),
                "Las notas de otras tareas deben permanecer intactas");
    }

    @Test
    @DisplayName("CP-TASK-004 | Lanza AppException si la tarea no existe")
    void delete_throwsOnNonExistentTask() {
        AppException ex = assertThrows(AppException.class, () ->
                service.delete("id-inexistente", PROF_ID));
        assertTrue(ex.getMessage().contains("Tarea no encontrada"));
    }

    @Test
    @DisplayName("CP-TASK-004 | Lanza AppException si el profesor no es el autor (RNF-06)")
    void delete_throwsOnWrongProfessor() {
        Task tarea = service.create("Tarea protegida", null, null, MATERIA_MAT, PROF_ID);
        AppException ex = assertThrows(AppException.class, () ->
                service.delete(tarea.getId(), OTRO_PROF_ID));
        assertEquals("No tienes permiso para eliminar esta tarea.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-TASK-004 | Si falla la autorización, las notas no se tocan")
    void delete_authFailure_gradesUntouched() {
        Task tarea = service.create("Tarea protegida", null, null, MATERIA_MAT, PROF_ID);
        crearNotasEnArchivo(tarea.getId(), 5);

        assertThrows(AppException.class, () -> service.delete(tarea.getId(), OTRO_PROF_ID));

        assertEquals(5, service.countGradesByTask(tarea.getId()),
                "Las notas no deben borrarse si la autorización falla");
    }


    private void crearNotasEnArchivo(String tareaId, int cantidad) {
        List<Map> notas = new ArrayList<>(JsonFileManager.readAll("grades.json", Map.class));
        for (int i = 0; i < cantidad; i++) {
            Map<String, Object> nota = new HashMap<>();
            nota.put("id",           "grade-" + tareaId + "-" + i);
            nota.put("tareaId",      tareaId);
            nota.put("estudianteId", "est-" + i);
            nota.put("materiaId",    MATERIA_ESP);
            nota.put("valor",        4.0);
            nota.put("profesorId",   PROF_ID);
            notas.add(nota);
        }
        JsonFileManager.writeAll("grades.json", notas);
    }

    private void deleteRecursively(java.io.File dir) throws IOException {
        if (dir.isDirectory()) {
            for (java.io.File child : dir.listFiles()) deleteRecursively(child);
        }
        dir.delete();
    }
}