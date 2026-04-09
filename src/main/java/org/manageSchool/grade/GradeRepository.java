package org.manageSchool.grade;  // Define el paquete donde esta esta clase

import org.manageSchool.shared.util.JsonFileManager;  // Importa el manejador de JSON
import java.util.List;  // Importa List para manejar listas
import java.util.Optional;  // Importa Optional para evitar nulls
import java.util.ArrayList;  // Importa ArrayList para lista vacia
import java.util.stream.Collectors;  // Importa Collectors para filtrar listas

public class GradeRepository {  // Clase que maneja el acceso a datos de notas
    private static final String FILE = "grades.json";  // Nombre del archivo JSON donde se guardan las notas

    public List<Grade> findAll() {  // Devuelve todas las notas
        List<Grade> grades = JsonFileManager.readAll(FILE, Grade.class);  // Lee el archivo JSON
        return grades != null ? grades : new ArrayList<>();  // Si es null devuelve lista vacia
    }

    public Optional<Grade> findById(String id) {  // Busca una nota por ID
        return findAll().stream()  // Convierte la lista en flujo
                .filter(g -> g.getId().equals(id))  // Filtra por ID
                .findFirst();  // Devuelve la primera encontrada
    }

    public List<Grade> findByTaskId(String tareaId) {  // Busca notas por ID de tarea
        return findAll().stream()  // Convierte la lista en flujo
                .filter(g -> g.getTareaId().equals(tareaId))  // Filtra por tareaId
                .collect(Collectors.toList());  // Recoge los resultados en una lista
    }

    public boolean existsByStudentAndTask(String estudianteId, String tareaId) {  // Verifica si ya existe nota para ese estudiante en esa tarea
        return findAll().stream()  // Convierte la lista en flujo
                .anyMatch(g -> g.getEstudianteId().equals(estudianteId) && g.getTareaId().equals(tareaId));  // True si existe
    }

    public void save(Grade grade) {  // Guarda una nueva nota
        List<Grade> grades = findAll();  // Obtiene todas las notas actuales
        grades.add(grade);  // Agrega la nueva nota
        JsonFileManager.writeAll(FILE, grades);  // Guarda la lista completa en el archivo
    }

    // ============ ISSUE-018: Buscar notas por estudiante y materia ============
    public List<Grade> findByStudentAndSubject(String estudianteId, String materiaId) {  // Busca notas por estudiante y materia
        return findAll().stream()  // Convierte la lista en flujo
                .filter(g -> g.getEstudianteId().equals(estudianteId) && g.getMateriaId().equals(materiaId))  // Filtra por estudiante y materia
                .collect(Collectors.toList());  // Recoge los resultados en una lista
    }

    // ============ ISSUE-018: Actualizar nota ============
    public void update(Grade grade) {  // Actualiza una nota existente
        List<Grade> grades = findAll();  // Obtiene todas las notas actuales
        for (int i = 0; i < grades.size(); i++) {  // Recorre la lista
            if (grades.get(i).getId().equals(grade.getId())) {  // Busca por ID
                grades.set(i, grade);  // Reemplaza la nota en esa posicion
                break;  // Sale del ciclo
            }
        }
        JsonFileManager.writeAll(FILE, grades);  // Guarda la lista actualizada
    }

    // ============ ISSUE-018: Eliminar nota ============
    public void deleteById(String id) {  // Elimina una nota por ID
        List<Grade> grades = findAll();  // Obtiene todas las notas actuales
        grades.removeIf(g -> g.getId().equals(id));  // Elimina la nota que coincide con el ID
        JsonFileManager.writeAll(FILE, grades);  // Guarda la lista actualizada
    }

    // ============ ISSUE-019: Buscar notas por ID de estudiante ============
    public List<Grade> findByStudentId(String estudianteId) {  // Busca todas las notas de un estudiante
        return findAll().stream()  // Convierte la lista en flujo
                .filter(g -> g.getEstudianteId().equals(estudianteId))  // Filtra por ID del estudiante
                .collect(Collectors.toList());  // Recoge los resultados en una lista
    }
}