package org.manageSchool.ranking;

import org.manageSchool.grade.GradeService;
import org.manageSchool.student.Student;
import org.manageSchool.student.StudentRepository;
import org.manageSchool.subject.Subject;
import org.manageSchool.subject.SubjectRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankingService {  // Lógica de negocio para el ranking trimestral

    private static final int DIAS_TRIMESTRE = 90;  // Duración fija de cada trimestre en días

    private final RankingRepository rankingRepo     = new RankingRepository();
    private final StudentRepository studentRepo     = new StudentRepository();
    private final SubjectRepository subjectRepo     = new SubjectRepository();
    private final GradeService      gradeService    = new GradeService();

    // ── CP-RANK-001 / CP-RANK-004: Cerrar trimestre y calcular promedios ──────

    /**
     * Cierra el período indicado y calcula el promedio trimestral de cada estudiante
     * activo usando ÚNICAMENTE las materias predeterminadas (CP-RANK-004).
     * El promedio es la media aritmética de los promedios por materia (CP-RANK-001).
     */
    public void cerrarTrimestre(String periodId) {  // Cierra el trimestre y genera promedios

        // 1. Buscar y validar el período
        Period period = rankingRepo.findPeriodById(periodId)
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado: " + periodId));

        if (period.isCerrado()) {  // No se puede cerrar un período ya cerrado
            throw new RuntimeException("El periodo ya fue cerrado.");
        }

        // 2. Obtener solo las 5 materias predeterminadas (CP-RANK-004)
        List<String> materiasPredeterminadasIds = subjectRepo.findAll().stream()
                .filter(Subject::isPredeterminada)   // Filtra: predeterminada == true
                .map(Subject::getId)
                .collect(Collectors.toList());

        if (materiasPredeterminadasIds.isEmpty()) {
            throw new RuntimeException("No hay materias predeterminadas configuradas.");
        }

        // 3. Calcular y guardar el promedio de cada estudiante activo
        List<Student> estudiantes = studentRepo.findAll().stream()
                .filter(Student::isActivo)
                .collect(Collectors.toList());

        for (Student estudiante : estudiantes) {  // Recorre cada estudiante activo
            String estudianteId = String.valueOf(estudiante.getId());

            // Calcula la media de promedios por materia (CP-RANK-001)
            OptionalDouble promedio = gradeService.calcularPromedioGeneral(
                    estudianteId, materiasPredeterminadasIds);

            if (promedio.isPresent()) {  // Solo guarda si el estudiante tiene al menos una nota
                // Redondea a 2 decimales
                double promedioRedondeado = Math.round(promedio.getAsDouble() * 100.0) / 100.0;

                StudentTrimesterAverage avg = new StudentTrimesterAverage(
                        estudianteId, periodId, promedioRedondeado);

                rankingRepo.saveAverage(avg);  // Persiste el promedio en averages.json
            }
        }

        // 4. Marcar el período como cerrado
        period.setCerrado(true);
        rankingRepo.updatePeriod(period);  // Persiste el cambio en periods.json
    }

    // ── Crear nuevo período trimestral ────────────────────────────────────────

    /**
     * Crea un período trimestral con fechaInicio dada y fechaCierre = fechaInicio + 90 días.
     */
    public Period crearPeriodo(String fechaInicio) {  // Crea un nuevo período de 90 días
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            throw new RuntimeException("La fecha de inicio no puede estar vacia.");
        }

        LocalDate inicio  = LocalDate.parse(fechaInicio);       // Parsea la fecha de inicio
        LocalDate cierre  = inicio.plusDays(DIAS_TRIMESTRE);    // Calcula fecha de cierre (+90 días)

        // Determina el número de trimestre (siguiente al último registrado)
        int numero = rankingRepo.findAllPeriods().size() + 1;

        Period period = new Period(
                UUID.randomUUID().toString(),
                numero,
                inicio.toString(),
                cierre.toString(),
                false  // Inicia abierto
        );

        rankingRepo.savePeriod(period);  // Guarda en periods.json
        return period;
    }

    // ── Consultar ranking de un período ──────────────────────────────────────

    /**
     * Devuelve la lista de promedios de un período ordenada de mayor a menor.
     */
    public List<StudentTrimesterAverage> obtenerRanking(String periodId) {  // Retorna el ranking ordenado
        List<StudentTrimesterAverage> promedios = rankingRepo.findAveragesByPeriod(periodId);

        if (promedios.isEmpty()) {
            throw new RuntimeException("No hay promedios registrados para este periodo.");
        }

        promedios.sort((a, b) -> Double.compare(b.getPromedio(), a.getPromedio()));  // Orden descendente
        return promedios;
    }

    // ── Listar períodos ───────────────────────────────────────────────────────

    public List<Period> listarPeriodos() {  // Devuelve todos los períodos registrados
        return rankingRepo.findAllPeriods();
    }
}