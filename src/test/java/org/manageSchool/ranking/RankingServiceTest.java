package org.manageSchool.ranking;  // Paquete de tests de ranking

import org.junit.jupiter.api.BeforeEach;  // Se ejecuta antes de cada prueba
import org.junit.jupiter.api.Test;  // Marca un metodo como prueba
import org.manageSchool.grade.GradeService;
import org.manageSchool.shared.util.JsonFileManager;
import org.manageSchool.student.Student;
import org.manageSchool.subject.Subject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;  // Importa metodos de verificacion

class RankingServiceTest {  // Tests para RankingService — cubre CP-RANK-001 y CP-RANK-004

    private RankingService rankingService;  // Servicio a probar
    private GradeService gradeService;     // Para registrar notas en los tests

    @BeforeEach  // Se ejecuta antes de CADA prueba
    void setUp() {  // Limpia todos los archivos JSON y prepara datos base
        rankingService = new RankingService();
        gradeService   = new GradeService();

        // Limpiar archivos del trimestre
        JsonFileManager.writeAll("periods.json",  new ArrayList<>());
        JsonFileManager.writeAll("averages.json", new ArrayList<>());
        JsonFileManager.writeAll("grades.json",   new ArrayList<>());
        JsonFileManager.writeAll("students.json", new ArrayList<>());
        JsonFileManager.writeAll("subjects.json", new ArrayList<>());

        // Crear las 5 materias predeterminadas (CP-RANK-004)
        List<Subject> materias = List.of(
                crearMateria("mat-001", "Matematicas",          true),
                crearMateria("mat-002", "Espanol",              true),
                crearMateria("mat-003", "Ciencias Naturales",   true),
                crearMateria("mat-004", "Ciencias Sociales",    true),
                crearMateria("mat-005", "Ingles",               true)
        );
        JsonFileManager.writeAll("subjects.json", materias);

        // Crear un estudiante activo
        Student ana = new Student();
        ana.setId(String.valueOf(1));
        ana.setNombre("Ana");
        ana.setActivo(true);
        JsonFileManager.writeAll("students.json", List.of(ana));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Subject crearMateria(String id, String nombre, boolean predeterminada) {  // Crea una materia para el test
        Subject s = new Subject();
        s.setId(id);
        s.setNombre(nombre);
        s.setPredeterminada(predeterminada);
        s.setActiva(true);
        return s;
    }

    // ── crearPeriodo ──────────────────────────────────────────────────────────

    @Test  // CP-RANK-001: El periodo se crea con fechaCierre = fechaInicio + 90 dias
    void crearPeriodo_creaConFechaCierreCorrecta() {
        Period period = rankingService.crearPeriodo("2025-01-01");  // Fecha de inicio del trimestre 1

        assertNotNull(period.getId());                              // Debe tener ID
        assertEquals(1, period.getNumero());                        // Primer trimestre
        assertEquals("2025-01-01", period.getFechaInicio());       // Fecha inicio correcta
        assertEquals("2025-04-01", period.getFechaCierre());       // +90 dias
        assertFalse(period.isCerrado());                            // Inicia abierto
    }

    @Test  // El numero de trimestre se incrementa automaticamente
    void crearPeriodo_numeroSeIncrementaAutomaticamente() {
        rankingService.crearPeriodo("2025-01-01");  // Trimestre 1
        Period segundo = rankingService.crearPeriodo("2025-04-01");  // Trimestre 2

        assertEquals(2, segundo.getNumero());  // Debe ser el segundo trimestre
    }

    @Test  // Lanza error si la fecha de inicio esta vacia
    void crearPeriodo_lanzaErrorSiFechaVacia() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            rankingService.crearPeriodo("");  // Fecha vacia
        });
        assertEquals("La fecha de inicio no puede estar vacia.", ex.getMessage());
    }

    @Test  // Lanza error si la fecha de inicio es null
    void crearPeriodo_lanzaErrorSiFechaNull() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            rankingService.crearPeriodo(null);  // Fecha null
        });
        assertEquals("La fecha de inicio no puede estar vacia.", ex.getMessage());
    }

    // ── cerrarTrimestre — CP-RANK-001 ─────────────────────────────────────────

    @Test  // CP-RANK-001: El promedio de Ana con notas 4.5, 4.0, 3.5, 4.5, 5.0 debe ser 4.30
    void cerrarTrimestre_calculaPromedioCorrectoCP_RANK_001() {
        // Registrar notas de Ana en las 5 materias predeterminadas
        gradeService.create("1", "task-001", "mat-001", 4.5, "prof-001");  // Matematicas
        gradeService.create("1", "task-002", "mat-002", 4.0, "prof-001");  // Espanol
        gradeService.create("1", "task-003", "mat-003", 3.5, "prof-001");  // Ciencias Naturales
        gradeService.create("1", "task-004", "mat-004", 4.5, "prof-001");  // Ciencias Sociales
        gradeService.create("1", "task-005", "mat-005", 5.0, "prof-001");  // Ingles

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        List<StudentTrimesterAverage> ranking = rankingService.obtenerRanking(period.getId());
        assertEquals(1, ranking.size());  // Solo hay 1 estudiante
        assertEquals(4.30, ranking.get(0).getPromedio(), 0.01);  // (4.5+4.0+3.5+4.5+5.0)/5 = 4.30
    }

    @Test  // CP-RANK-001: El promedio queda guardado en el periodo correcto
    void cerrarTrimestre_promedioQuedaGuardadoEnPeriodoCorrecto() {
        gradeService.create("1", "task-001", "mat-001", 4.0, "prof-001");

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        List<StudentTrimesterAverage> promedios = rankingService.obtenerRanking(period.getId());
        assertEquals(period.getId(), promedios.get(0).getPeriodId());  // periodId debe coincidir
    }

    @Test  // CP-RANK-001: El periodo queda marcado como cerrado
    void cerrarTrimestre_marcaPeriodoComoCerrado() {
        gradeService.create("1", "task-001", "mat-001", 4.0, "prof-001");

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        List<Period> periodos = rankingService.listarPeriodos();
        assertTrue(periodos.get(0).isCerrado());  // Debe estar cerrado
    }

    @Test  // No se puede cerrar un periodo que ya fue cerrado
    void cerrarTrimestre_lanzaErrorSiPeriodoYaCerrado() {
        gradeService.create("1", "task-001", "mat-001", 4.0, "prof-001");

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());  // Primer cierre: OK

        Exception ex = assertThrows(RuntimeException.class, () -> {
            rankingService.cerrarTrimestre(period.getId());  // Segundo cierre: debe fallar
        });
        assertEquals("El periodo ya fue cerrado.", ex.getMessage());
    }

    @Test  // Lanza error si el ID del periodo no existe
    void cerrarTrimestre_lanzaErrorSiPeriodoNoExiste() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            rankingService.cerrarTrimestre("id-que-no-existe");
        });
        assertTrue(ex.getMessage().contains("Periodo no encontrado"));
    }

    // ── cerrarTrimestre — CP-RANK-004 ─────────────────────────────────────────

    @Test  // CP-RANK-004: Una materia con predeterminada=false no se incluye en el calculo
    void cerrarTrimestre_excluyeMateriaNoPrederterminadaCP_RANK_004() {
        // Agregar materia extra "Teatro" con predeterminada=false
        List<Subject> materias = new ArrayList<>(JsonFileManager.readAll("subjects.json", Subject.class));
        materias.add(crearMateria("mat-teatro", "Teatro", false));
        JsonFileManager.writeAll("subjects.json", materias);

        // Ana tiene 4.0 en las 5 predeterminadas y 5.0 en Teatro
        gradeService.create("1", "task-001", "mat-001", 4.0, "prof-001");
        gradeService.create("1", "task-002", "mat-002", 4.0, "prof-001");
        gradeService.create("1", "task-003", "mat-003", 4.0, "prof-001");
        gradeService.create("1", "task-004", "mat-004", 4.0, "prof-001");
        gradeService.create("1", "task-005", "mat-005", 4.0, "prof-001");
        gradeService.create("1", "task-teatro", "mat-teatro", 5.0, "prof-001");  // Teatro: no debe contar

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        List<StudentTrimesterAverage> ranking = rankingService.obtenerRanking(period.getId());
        // Si Teatro se excluye correctamente: (4+4+4+4+4)/5 = 4.0
        // Si Teatro se incluyera: (4+4+4+4+4+5)/6 = 4.17 — falla el test
        assertEquals(4.0, ranking.get(0).getPromedio(), 0.01);
    }

    @Test  // CP-RANK-004: Materias sin notas no afectan el calculo
    void cerrarTrimestre_soloConsideraMateriasConNotas() {
        // Solo 3 de las 5 materias tienen nota
        gradeService.create("1", "task-001", "mat-001", 5.0, "prof-001");  // Matematicas: 5.0
        gradeService.create("1", "task-002", "mat-002", 3.0, "prof-001");  // Espanol: 3.0
        gradeService.create("1", "task-003", "mat-003", 4.0, "prof-001");  // C. Naturales: 4.0
        // mat-004 y mat-005 sin nota — no deben contar

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        List<StudentTrimesterAverage> ranking = rankingService.obtenerRanking(period.getId());
        // Media solo de las 3 materias con nota: (5+3+4)/3 = 4.0
        assertEquals(4.0, ranking.get(0).getPromedio(), 0.01);
    }

    @Test  // Lanza error si no hay materias predeterminadas configuradas
    void cerrarTrimestre_lanzaErrorSiNoHayMateriasPredeterminadas() {
        // Reemplazar todas las materias por no-predeterminadas
        JsonFileManager.writeAll("subjects.json", List.of(
                crearMateria("mat-extra", "Teatro", false)
        ));

        Period period = rankingService.crearPeriodo("2025-01-01");

        Exception ex = assertThrows(RuntimeException.class, () -> {
            rankingService.cerrarTrimestre(period.getId());
        });
        assertEquals("No hay materias predeterminadas configuradas.", ex.getMessage());
    }

    // ── obtenerRanking ────────────────────────────────────────────────────────

    @Test  // El ranking se ordena de mayor a menor promedio
    void obtenerRanking_ordenadoDescendente() {
        // Agregar segundo estudiante
        Student ana    = JsonFileManager.readAll("students.json", Student.class).get(0);
        Student carlos = new Student();
        carlos.setId(String.valueOf(2));
        carlos.setNombre("Carlos");
        carlos.setActivo(true);
        JsonFileManager.writeAll("students.json", List.of(ana, carlos));

        // Ana: 3.0, Carlos: 5.0
        gradeService.create("1", "task-001", "mat-001", 3.0, "prof-001");
        gradeService.create("2", "task-002", "mat-001", 5.0, "prof-001");

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        List<StudentTrimesterAverage> ranking = rankingService.obtenerRanking(period.getId());
        assertTrue(ranking.get(0).getPromedio() >= ranking.get(1).getPromedio());  // Orden descendente
        assertEquals(5.0, ranking.get(0).getPromedio(), 0.01);  // Carlos primero
    }

    @Test  // Lanza error si el periodo no tiene promedios
    void obtenerRanking_lanzaErrorSiNoHayPromedios() {
        Period period = rankingService.crearPeriodo("2025-01-01");
        // No se cierra el trimestre — no hay promedios

        Exception ex = assertThrows(RuntimeException.class, () -> {
            rankingService.obtenerRanking(period.getId());
        });
        assertEquals("No hay promedios registrados para este periodo.", ex.getMessage());
    }

    // ── listarPeriodos ────────────────────────────────────────────────────────

    @Test  // listarPeriodos devuelve lista vacia si no hay periodos
    void listarPeriodos_devuelveListaVaciaSiNoHayPeriodos() {
        List<Period> periodos = rankingService.listarPeriodos();
        assertTrue(periodos.isEmpty());  // No hay periodos creados
    }

    @Test  // listarPeriodos devuelve todos los periodos creados
    void listarPeriodos_devuelveTodosLosPeriodos() {
        rankingService.crearPeriodo("2025-01-01");
        rankingService.crearPeriodo("2025-04-01");

        assertEquals(2, rankingService.listarPeriodos().size());  // Deben haber 2 periodos
    }
}