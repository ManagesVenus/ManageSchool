package org.manageSchool.ranking;  // Tests para CP-RANK-002 y CP-RANK-002b

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.manageSchool.grade.GradeService;
import org.manageSchool.shared.util.JsonFileManager;
import org.manageSchool.student.Student;
import org.manageSchool.subject.Subject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RankingActualServiceTest {  // Tests de consultarRankingActual() — CP-RANK-002 y CP-RANK-002b

    private RankingService rankingService;  // Servicio bajo prueba
    private GradeService   gradeService;   // Para registrar notas en los tests

    @BeforeEach  // Se ejecuta antes de CADA prueba
    void setUp() {  // Limpia todos los archivos JSON y prepara datos base
        rankingService = new RankingService();
        gradeService   = new GradeService();

        JsonFileManager.writeAll("periods.json",  new ArrayList<>());
        JsonFileManager.writeAll("averages.json", new ArrayList<>());
        JsonFileManager.writeAll("grades.json",   new ArrayList<>());
        JsonFileManager.writeAll("students.json", new ArrayList<>());
        JsonFileManager.writeAll("subjects.json", new ArrayList<>());

        // 5 materias predeterminadas requeridas por el sistema
        List<Subject> materias = List.of(
                crearMateria("mat-001", "Matematicas",        true),
                crearMateria("mat-002", "Espanol",            true),
                crearMateria("mat-003", "Ciencias Naturales", true),
                crearMateria("mat-004", "Ciencias Sociales",  true),
                crearMateria("mat-005", "Ingles",             true)
        );
        JsonFileManager.writeAll("subjects.json", materias);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Subject crearMateria(String id, String nombre, boolean predeterminada) {
        Subject s = new Subject();
        s.setId(id);
        s.setNombre(nombre);
        s.setPredeterminada(predeterminada);
        s.setActiva(true);
        return s;
    }

    private Student crearEstudiante(String id, String nombre) {
        Student e = new Student();
        e.setId(id);
        e.setNombre(nombre);
        e.setActivo(true);
        return e;
    }

    /** Registra UNA nota en una materia para un estudiante. */
    private void registrarNota(String estudianteId, String taskId, String materiaId, double nota) {
        gradeService.create(estudianteId, taskId, materiaId, nota, "prof-001");
    }

    // ── CP-RANK-002b: nunca ha cerrado un trimestre ───────────────────────────

    @Test  // CP-RANK-002b: sin trimestres — lanza el mensaje de "sin datos suficientes"
    void consultarRanking_sinTrimestresRegistrados_lanzaMensajeSinDatos() {
        // No hay ningún período creado

        Exception ex = assertThrows(RuntimeException.class,
                () -> rankingService.consultarRankingActual());

        assertEquals(
                "Aun no hay trimestres cerrados con datos suficientes para generar el ranking",
                ex.getMessage()
        );
    }

    @Test  // CP-RANK-002b: período abierto sin previo cerrado — lanza el mensaje de "sin datos suficientes"
    void consultarRanking_soloTrimestreAbierto_sinCerrado_lanzaMensajeSinDatos() {
        // Hay un período abierto pero nunca se cerró ninguno
        rankingService.crearPeriodo("2025-01-01");

        Exception ex = assertThrows(RuntimeException.class,
                () -> rankingService.consultarRankingActual());

        assertEquals(
                "Aun no hay trimestres cerrados con datos suficientes para generar el ranking",
                ex.getMessage()
        );
    }

    @Test  // CP-RANK-002b: período cerrado sin promedios — se trata igual que "sin datos suficientes"
    void consultarRanking_trimestreCerradoSinPromedios_lanzaMensajeSinDatos() {
        // Crear y cerrar un período sin que ningún estudiante tenga notas
        // (sin estudiantes activos, cerrarTrimestre no guarda promedios)
        JsonFileManager.writeAll("students.json", new ArrayList<>());

        Period period = rankingService.crearPeriodo("2025-01-01");
        // No se puede cerrar sin materias con notas; simulamos el estado
        // guardando el período como cerrado directamente sin promedios
        period.setCerrado(true);
        JsonFileManager.writeAll("periods.json", List.of(period));
        // averages.json sigue vacío → ese período no cuenta como "con datos"

        Exception ex = assertThrows(RuntimeException.class,
                () -> rankingService.consultarRankingActual());

        assertEquals(
                "Aun no hay trimestres cerrados con datos suficientes para generar el ranking",
                ex.getMessage()
        );
    }

    // ── CP-RANK-002: trimestre cerrado — retorna el ranking correctamente ─────

    @Test  // CP-RANK-002: ranking muestra los 3 primeros con nombre y promedio correcto
    void consultarRanking_trimestreCerrado_muestraTop3ConPromediosCorrectos() {
        // Escenario del CP-RANK-002: Ana=4.8, Luis=4.5, María=4.2, Juan=3.9
        List<Student> estudiantes = List.of(
                crearEstudiante("1", "Ana"),
                crearEstudiante("2", "Luis"),
                crearEstudiante("3", "Maria"),
                crearEstudiante("4", "Juan")
        );
        JsonFileManager.writeAll("students.json", estudiantes);

        registrarNota("1", "t-001", "mat-001", 4.8);
        registrarNota("2", "t-002", "mat-001", 4.5);
        registrarNota("3", "t-003", "mat-001", 4.2);
        registrarNota("4", "t-004", "mat-001", 3.9);

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        List<StudentTrimesterAverage> ranking = resultado.promedios;
        assertTrue(ranking.size() >= 3);                                          // Al menos 3 puestos
        assertEquals(4.8, ranking.get(0).getPromedio(), 0.01);                    // 1er lugar: Ana
        assertEquals(4.5, ranking.get(1).getPromedio(), 0.01);                    // 2do lugar: Luis
        assertEquals(4.2, ranking.get(2).getPromedio(), 0.01);                    // 3er lugar: María
    }

    @Test  // CP-RANK-002: el ranking se ordena de mayor a menor promedio
    void consultarRanking_trimestreCerrado_ordenadoDescendente() {
        List<Student> estudiantes = List.of(
                crearEstudiante("1", "Ana"),
                crearEstudiante("2", "Luis")
        );
        JsonFileManager.writeAll("students.json", estudiantes);

        registrarNota("1", "t-001", "mat-001", 3.0);  // Ana: 3.0
        registrarNota("2", "t-002", "mat-001", 5.0);  // Luis: 5.0

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();
        List<StudentTrimesterAverage> ranking = resultado.promedios;

        // El de mayor promedio debe ir primero
        assertTrue(ranking.get(0).getPromedio() >= ranking.get(1).getPromedio());
        assertEquals(5.0, ranking.get(0).getPromedio(), 0.01);
    }

    @Test  // CP-RANK-002: el resultado incluye el período al que pertenece el ranking
    void consultarRanking_trimestreCerrado_incluyePeriodoCorrecto() {
        JsonFileManager.writeAll("students.json", List.of(crearEstudiante("1", "Ana")));
        registrarNota("1", "t-001", "mat-001", 4.0);

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        assertEquals(period.getId(), resultado.periodo.getId());  // Período correcto
    }

    @Test  // CP-RANK-002: cuando el trimestre ya cerró, no hay aviso
    void consultarRanking_trimestreCerrado_sinTrimestreEnCurso_noHayAviso() {
        JsonFileManager.writeAll("students.json", List.of(crearEstudiante("1", "Ana")));
        registrarNota("1", "t-001", "mat-001", 4.0);

        Period period = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(period.getId());
        // No hay período abierto después

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        assertNull(resultado.aviso);  // Sin aviso porque no hay trimestre en curso
    }

    // ── CP-RANK-002b: trimestre en curso con uno cerrado anterior ─────────────

    @Test  // CP-RANK-002b: trimestre en curso → muestra ranking del último cerrado
    void consultarRanking_trimestreEnCurso_muestraUltimoCerrado() {
        JsonFileManager.writeAll("students.json", List.of(crearEstudiante("1", "Ana")));
        registrarNota("1", "t-001", "mat-001", 4.0);

        // Trimestre 1: cerrado con datos
        Period trimestre1 = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(trimestre1.getId());

        // Trimestre 2: abierto (en curso)
        rankingService.crearPeriodo("2025-04-01");

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        // Debe retornar los datos del trimestre 1 (el cerrado)
        assertEquals(trimestre1.getId(), resultado.periodo.getId());
        assertFalse(resultado.promedios.isEmpty());
    }

    @Test  // CP-RANK-002b: trimestre en curso → muestra el aviso correspondiente
    void consultarRanking_trimestreEnCurso_muestraAvisoTrimestresAnterior() {
        JsonFileManager.writeAll("students.json", List.of(crearEstudiante("1", "Ana")));
        registrarNota("1", "t-001", "mat-001", 4.0);

        Period trimestre1 = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(trimestre1.getId());

        // Trimestre 2 en curso
        rankingService.crearPeriodo("2025-04-01");

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        // El aviso debe estar presente y contener el mensaje exacto
        assertNotNull(resultado.aviso);
        assertEquals(
                "Mostrando ranking del trimestre anterior. El trimestre actual aun esta en curso.",
                resultado.aviso
        );
    }

    @Test  // CP-RANK-002b: con múltiples cerrados y uno en curso, muestra el último cerrado
    void consultarRanking_variosTrimestresCerrados_muestraElUltimo() {
        List<Student> estudiantes = List.of(
                crearEstudiante("1", "Ana"),
                crearEstudiante("2", "Luis")
        );
        JsonFileManager.writeAll("students.json", estudiantes);

        // Trimestre 1: Ana=3.0
        registrarNota("1", "t-001", "mat-001", 3.0);
        Period trimestre1 = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(trimestre1.getId());

        // Trimestre 2: Luis=5.0 (el más reciente cerrado)
        registrarNota("2", "t-002", "mat-001", 5.0);
        Period trimestre2 = rankingService.crearPeriodo("2025-04-01");
        rankingService.cerrarTrimestre(trimestre2.getId());

        // Trimestre 3: en curso
        rankingService.crearPeriodo("2025-07-01");

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        // Debe mostrar el trimestre 2 (último cerrado con datos, número más alto)
        assertEquals(trimestre2.getId(), resultado.periodo.getId());
        assertEquals(2, resultado.periodo.getNumero());
    }

    @Test  // CP-RANK-002b: con trimestre en curso, el aviso no aparece si no hay ninguno abierto después
    void consultarRanking_sinTrimestreEnCurso_noMuestraAviso() {
        JsonFileManager.writeAll("students.json", List.of(crearEstudiante("1", "Ana")));
        registrarNota("1", "t-001", "mat-001", 4.5);

        Period trimestre1 = rankingService.crearPeriodo("2025-01-01");
        rankingService.cerrarTrimestre(trimestre1.getId());
        // No se crea un segundo período abierto

        RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

        assertNull(resultado.aviso);  // No hay trimestre en curso → sin aviso
    }

    @Test  // CP-RANK-002b: varios períodos abiertos y sin cerrados — lanza error sin datos
    void consultarRanking_variosAbiertosNingunCerrado_lanzaErrorSinDatos() {
        rankingService.crearPeriodo("2025-01-01");
        rankingService.crearPeriodo("2025-04-01");

        Exception ex = assertThrows(RuntimeException.class,
                () -> rankingService.consultarRankingActual());

        assertEquals(
                "Aun no hay trimestres cerrados con datos suficientes para generar el ranking",
                ex.getMessage()
        );
    }
}