package org.manageSchool.ranking;

import org.manageSchool.shared.util.JsonFileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RankingRepository {  // Clase que maneja la persistencia de períodos y promedios

    private static final String PERIODS_FILE  = "periods.json";   // Archivo de períodos trimestrales
    private static final String AVERAGES_FILE = "averages.json";  // Archivo de promedios por estudiante/período

    // ── Períodos ──────────────────────────────────────────────

    public List<Period> findAllPeriods() {  // Devuelve todos los períodos registrados
        List<Period> list = JsonFileManager.readAll(PERIODS_FILE, Period.class);
        return list != null ? list : new ArrayList<>();
    }

    public Optional<Period> findPeriodById(String id) {  // Busca un período por ID
        return findAllPeriods().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public void savePeriod(Period period) {  // Guarda un nuevo período
        List<Period> list = findAllPeriods();
        list.add(period);
        JsonFileManager.writeAll(PERIODS_FILE, list);
    }

    public void updatePeriod(Period period) {  // Actualiza un período existente (ej: marcarlo como cerrado)
        List<Period> list = findAllPeriods();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(period.getId())) {
                list.set(i, period);
                break;
            }
        }
        JsonFileManager.writeAll(PERIODS_FILE, list);
    }

    // ── Promedios ─────────────────────────────────────────────

    public List<StudentTrimesterAverage> findAllAverages() {  // Devuelve todos los promedios guardados
        List<StudentTrimesterAverage> list = JsonFileManager.readAll(AVERAGES_FILE, StudentTrimesterAverage.class);
        return list != null ? list : new ArrayList<>();
    }

    public List<StudentTrimesterAverage> findAveragesByPeriod(String periodId) {  // Devuelve promedios de un período
        return findAllAverages().stream()
                .filter(a -> a.getPeriodId().equals(periodId))
                .collect(Collectors.toList());
    }

    public Optional<StudentTrimesterAverage> findAverageByStudentAndPeriod(String estudianteId, String periodId) {  // Busca promedio de un estudiante en un período
        return findAllAverages().stream()
                .filter(a -> a.getEstudianteId().equals(estudianteId) && a.getPeriodId().equals(periodId))
                .findFirst();
    }

    public void saveAverage(StudentTrimesterAverage average) {  // Guarda un nuevo promedio trimestral
        List<StudentTrimesterAverage> list = findAllAverages();
        list.add(average);
        JsonFileManager.writeAll(AVERAGES_FILE, list);
    }
}