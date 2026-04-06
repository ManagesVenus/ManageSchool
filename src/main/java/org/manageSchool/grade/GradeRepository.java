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
}