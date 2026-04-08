package test;  // Define el paquete donde esta esta clase de prueba

import org.junit.jupiter.api.BeforeEach;  // Se ejecuta antes de cada prueba
import org.junit.jupiter.api.Test;  // Marca un metodo como prueba
import org.manageSchool.grade.Grade;  // Importa el modelo Grade
import org.manageSchool.grade.GradeRepository;  // Importa el repositorio
import org.manageSchool.grade.GradeService;  // Importa el servicio
import org.manageSchool.shared.util.JsonFileManager;  // Importa el manejador de JSON
import java.util.ArrayList;  // Importa ArrayList para limpiar datos
import java.util.OptionalDouble;
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

    // ============ TESTS PARA ISSUE-018 ============

    @Test  // Prueba 5: listByTask devuelve notas de una tarea especifica
    void listByTask_devuelveNotasDeLaTarea() {  // Verifica que listByTask filtra correctamente por tareaId
        // Crear 3 notas: dos para task-001 y una para task-002
        gradeService.create("est-001", "task-001", "mat-001", 4.0, "prof-001");
        gradeService.create("est-002", "task-001", "mat-001", 3.5, "prof-001");
        gradeService.create("est-001", "task-002", "mat-001", 5.0, "prof-001");

        // Verificar que task-001 tiene 2 notas
        assertEquals(2, gradeService.listByTask("task-001").size());
        // Verificar que task-002 tiene 1 nota
        assertEquals(1, gradeService.listByTask("task-002").size());
        // Verificar que task-003 tiene 0 notas
        assertEquals(0, gradeService.listByTask("task-003").size());
    }

    @Test  // Prueba 6: update edita una nota existente correctamente
    void update_editaNotaExitosamente() {  // Verifica que se puede editar el valor de una nota
        Grade creada = gradeService.create("est-001", "task-001", "mat-001", 3.0, "prof-001");
        assertEquals(3.0, creada.getValor());  // Verifica valor inicial

        Grade actualizada = gradeService.update(creada.getId(), 4.5);  // Edita la nota
        assertEquals(4.5, actualizada.getValor());  // Verifica que el valor cambio
        assertEquals(1, repo.findAll().size());  // Sigue habiendo 1 nota (no se creo una nueva)
    }

    @Test  // Prueba 7: update no permite nota mayor a 5.0
    void update_lanzaErrorSiNotaMayorA5() {  // Verifica que no permite editar con nota > 5.0
        Grade creada = gradeService.create("est-001", "task-001", "mat-001", 3.0, "prof-001");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            gradeService.update(creada.getId(), 6.0);  // Intenta poner nota 6.0
        });
        assertEquals("La nota debe estar entre 0.0 y 5.0", exception.getMessage());
    }

    @Test  // Prueba 8: update no permite nota menor a 0
    void update_lanzaErrorSiNotaMenorA0() {  // Verifica que no permite editar con nota < 0
        Grade creada = gradeService.create("est-001", "task-001", "mat-001", 3.0, "prof-001");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            gradeService.update(creada.getId(), -1.0);  // Intenta poner nota -1.0
        });
        assertEquals("La nota debe estar entre 0.0 y 5.0", exception.getMessage());
    }

    @Test  // Prueba 9: update lanza error si la nota no existe
    void update_lanzaErrorSiNotaNoExiste() {  // Verifica que no permite editar una nota que no existe
        Exception exception = assertThrows(RuntimeException.class, () -> {
            gradeService.update("id-inexistente", 4.0);  // ID que no existe
        });
        assertEquals("Nota no encontrada.", exception.getMessage());
    }

    @Test  // Prueba 10: delete elimina una nota existente
    void delete_eliminaNotaExitosamente() {  // Verifica que se puede eliminar una nota
        Grade creada = gradeService.create("est-001", "task-001", "mat-001", 4.0, "prof-001");
        assertEquals(1, repo.findAll().size());  // Verifica que hay 1 nota

        gradeService.delete(creada.getId());  // Elimina la nota
        assertEquals(0, repo.findAll().size());  // Verifica que ya no hay notas
    }

    @Test  // Prueba 11: delete lanza error si la nota no existe
    void delete_lanzaErrorSiNotaNoExiste() {  // Verifica que no permite eliminar una nota que no existe
        Exception exception = assertThrows(RuntimeException.class, () -> {
            gradeService.delete("id-inexistente");  // ID que no existe
        });
        assertEquals("Nota no encontrada.", exception.getMessage());
    }

    @Test  // Prueba 12: calcularPromedioPorMateria calcula correctamente el promedio
    void calcularPromedioPorMateria_devuelvePromedioCorrecto() {  // Verifica el calculo del promedio
        // Crear 3 notas para el mismo estudiante en la misma materia
        gradeService.create("est-001", "task-001", "mat-001", 4.0, "prof-001");
        gradeService.create("est-001", "task-002", "mat-001", 5.0, "prof-001");
        gradeService.create("est-001", "task-003", "mat-001", 3.0, "prof-001");

        OptionalDouble promedio = gradeService.calcularPromedioPorMateria("est-001", "mat-001");

        assertTrue(promedio.isPresent());  // Verifica que hay promedio
        // (4.0 + 5.0 + 3.0) = 12.0 / 3 = 4.0
        assertEquals(4.0, promedio.getAsDouble(), 0.01);  // Verifica el valor con margen de error 0.01
    }

    @Test  // Prueba 13: calcularPromedioPorMateria retorna vacio si no hay notas
    void calcularPromedioPorMateria_retornaVacioSiNoHayNotas() {  // Verifica que retorna vacio cuando no hay notas
        OptionalDouble promedio = gradeService.calcularPromedioPorMateria("est-001", "mat-001");
        assertFalse(promedio.isPresent());  // Verifica que esta vacio
    }

    @Test  // Prueba 14: calcularPromedioPorMateria solo considera notas de la materia especificada
    void calcularPromedioPorMateria_soloConsideraNotasDeEsaMateria() {  // Verifica que no mezcla materias
        // Notas para mat-001
        gradeService.create("est-001", "task-001", "mat-001", 4.0, "prof-001");
        gradeService.create("est-001", "task-002", "mat-001", 5.0, "prof-001");
        // Nota para mat-002 (diferente materia)
        gradeService.create("est-001", "task-003", "mat-002", 5.0, "prof-001");

        OptionalDouble promedioMat001 = gradeService.calcularPromedioPorMateria("est-001", "mat-001");
        OptionalDouble promedioMat002 = gradeService.calcularPromedioPorMateria("est-001", "mat-002");

        assertTrue(promedioMat001.isPresent());  // mat-001 tiene notas
        assertEquals(4.5, promedioMat001.getAsDouble(), 0.01);  // (4.0 + 5.0) / 2 = 4.5
        assertTrue(promedioMat002.isPresent());  // mat-002 tiene nota
        assertEquals(5.0, promedioMat002.getAsDouble(), 0.01);  // 5.0
    }

    // ============ TESTS PARA ISSUE-019 ============

    @Test  // Prueba 15: listByStudent devuelve notas de un estudiante especifico
    void listByStudent_devuelveNotasDelEstudiante() {  // Verifica que listByStudent filtra correctamente por estudianteId
        // Crear notas para dos estudiantes diferentes
        gradeService.create("est-001", "task-001", "mat-001", 4.0, "prof-001");
        gradeService.create("est-001", "task-002", "mat-001", 5.0, "prof-001");
        gradeService.create("est-002", "task-001", "mat-001", 3.5, "prof-001");

        // Verificar que est-001 tiene 2 notas
        assertEquals(2, gradeService.listByStudent("est-001").size());
        // Verificar que est-002 tiene 1 nota
        assertEquals(1, gradeService.listByStudent("est-002").size());
        // Verificar que est-003 tiene 0 notas
        assertEquals(0, gradeService.listByStudent("est-003").size());
    }

    @Test  // Prueba 16: listByStudent lanza error si estudianteId es vacio
    void listByStudent_lanzaErrorSiEstudianteIdVacio() {  // Verifica que no permite buscar con ID vacio
        Exception exception = assertThrows(RuntimeException.class, () -> {
            gradeService.listByStudent("");  // ID vacio
        });
        assertEquals("El ID del estudiante no puede estar vacio.", exception.getMessage());
    }

    @Test  // Prueba 17: listByStudent lanza error si estudianteId es null
    void listByStudent_lanzaErrorSiEstudianteIdNull() {  // Verifica que no permite buscar con ID null
        Exception exception = assertThrows(RuntimeException.class, () -> {
            gradeService.listByStudent(null);  // ID null
        });
        assertEquals("El ID del estudiante no puede estar vacio.", exception.getMessage());
    }
}