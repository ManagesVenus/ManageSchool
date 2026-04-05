package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.manageSchool.shared.AppException;
import org.manageSchool.task.Task;
import org.manageSchool.task.TaskService;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
        // Nota: Como tu Repo escribe en "tasks.json", ten cuidado de que
        // este archivo exista o usa un entorno de pruebas.
    }

    @Test
    @DisplayName("CP-TASK-001: Escenario de creación exitosa")
    void debeCrearTareaCuandoLosDatosSonValidos() {
        // Arrange (Preparar datos basados en tu HU)
        String titulo = "Taller de álgebra";
        String descripcion = "Capítulo 5";
        String fechaLimite = "2025-03-01";
        String materiaId = "MAT-101"; // ID de Matemáticas
        String profesorId = "PROF-001";

        // Act (Ejecutar la lógica de tu Service)
        Task resultado = taskService.create(titulo, descripcion, fechaLimite, materiaId, profesorId);

        // Assert (Verificar resultados)
        assertNotNull(resultado.getId(), "El ID debería haberse generado con UUID");
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(materiaId, resultado.getMateriaId());
        System.out.println("Test Exitoso: Tarea creada -> " + resultado);
    }

    @Test
    @DisplayName("CP-TASK-001: Escenario fallido sin título")
    void debeLanzarExcepcionCuandoTituloEstaVacio() {
        // Arrange
        String tituloInvalido = ""; // Vacío
        String profesorId = "PROF-001";

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            taskService.create(tituloInvalido, "Desc", "2025-03-01", "MAT1", profesorId);
        });

        // Verificamos que el mensaje sea el que definiste en tu Service
        assertEquals("El título es obligatorio.", exception.getMessage());
    }
}