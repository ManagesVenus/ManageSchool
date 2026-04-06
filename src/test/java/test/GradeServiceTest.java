package test;  // Define el paquete donde esta esta clase de prueba

import org.junit.jupiter.api.BeforeEach;  // Se ejecuta antes de cada prueba
import org.junit.jupiter.api.Test;  // Marca un metodo como prueba
import org.manageSchool.grade.Grade;  // Importa el modelo Grade
import org.manageSchool.grade.GradeRepository;  // Importa el repositorio
import org.manageSchool.grade.GradeService;  // Importa el servicio
import org.manageSchool.shared.util.JsonFileManager;  // Importa el manejador de JSON
import java.util.ArrayList;  // Importa ArrayList para limpiar datos

import static org.junit.jupiter.api.Assertions.*;  // Importa metodos de verificacion

class GradeServiceTest {  // Clase con todas las pruebas de GradeService
    private GradeService gradeService;  // Servicio a probar
    private GradeRepository repo;  // Repositorio para verificar datos

    @BeforeEach  // Se ejecuta antes de CADA prueba
    void setUp() {  // Configuracion inicial
        gradeService = new GradeService();  // Crea instancia del servicio
        repo = new GradeRepository();  // Crea instancia del repositorio

        // Limpiar todos los datos existentes antes de cada prueba
        JsonFileManager.writeAll("grades.json", new ArrayList<Grade>());  // Sobrescribe con lista vacia
    }

    @Test  // Prueba 1: Crear nota exitosamente
    void create_creaNotaExitosamente() {
        Grade nueva = gradeService.create("est-001", "task-001", "mat-001", 4.5, "prof-001");  // Crea una nota

        assertNotNull(nueva.getId());  // Verifica que tiene ID
        assertEquals(4.5, nueva.getValor());  // Verifica el valor
        assertEquals("est-001", nueva.getEstudianteId());  // Verifica ID del estudiante
        assertEquals("task-001", nueva.getTareaId());  // Verifica ID de la tarea
        assertEquals("prof-001", nueva.getProfesorId());  // Verifica ID del profesor
        assertNotNull(nueva.getFechaRegistro());  // Verifica que tiene fecha
        assertEquals(1, repo.findAll().size());  // Verifica que hay 1 nota en el repositorio
    }

    @Test  // Prueba 2: No permite nota mayor a 5.0
    void create_lanzaErrorSiNotaMayorA5() {
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera excepcion
            gradeService.create("est-001", "task-001", "mat-001", 6.0, "prof-001");  // Intenta con nota 6.0
        });
        assertEquals("La nota debe estar entre 0.0 y 5.0", exception.getMessage());  // Verifica mensaje
    }

    @Test  // Prueba 3: No permite nota menor a 0
    void create_lanzaErrorSiNotaMenorA0() {
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera excepcion
            gradeService.create("est-001", "task-001", "mat-001", -1.0, "prof-001");  // Intenta con nota -1.0
        });
        assertEquals("La nota debe estar entre 0.0 y 5.0", exception.getMessage());  // Verifica mensaje
    }

    @Test  // Prueba 4: No permite nota duplicada (mismo estudiante y misma tarea)
    void create_lanzaErrorSiNotaDuplicada() {
        gradeService.create("est-001", "task-001", "mat-001", 4.0, "prof-001");  // Crea primera nota

        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera excepcion
            gradeService.create("est-001", "task-001", "mat-001", 5.0, "prof-001");  // Intenta crear duplicado
        });
        assertEquals("Ya existe una nota para este estudiante en esta tarea. Use editar.", exception.getMessage());  // Verifica mensaje
    }

    @Test  // Prueba 5: No permite estudianteId vacio
    void create_lanzaErrorSiEstudianteIdVacio() {
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera excepcion
            gradeService.create("", "task-001", "mat-001", 4.0, "prof-001");  // EstudianteId vacio
        });
        assertEquals("El ID del estudiante no puede estar vacio.", exception.getMessage());  // Verifica mensaje
    }

    @Test  // Prueba 6: No permite tareaId vacio
    void create_lanzaErrorSiTareaIdVacio() {
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera excepcion
            gradeService.create("est-001", "", "mat-001", 4.0, "prof-001");  // TareaId vacio
        });
        assertEquals("El ID de la tarea no puede estar vacio.", exception.getMessage());  // Verifica mensaje
    }

    @Test  // Prueba 7: No permite profesorId vacio
    void create_lanzaErrorSiProfesorIdVacio() {
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera excepcion
            gradeService.create("est-001", "task-001", "mat-001", 4.0, "");  // ProfesorId vacio
        });
        assertEquals("El ID del profesor no puede estar vacio.", exception.getMessage());  // Verifica mensaje
    }
}