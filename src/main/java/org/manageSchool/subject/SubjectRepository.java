package org.manageSchool.subject;  // Define el paquete donde está esta clase

import org.manageSchool.shared.util.JsonFileManager;  // Importa la clase para leer/escribir JSON
import java.util.List;  // Importa List para manejar listas de materias
import java.util.Optional;  // Importa Optional para evitar nulls
import java.util.ArrayList;  // Importa ArrayList para crear lista vacía

public class SubjectRepository {  // Clase que maneja el acceso a datos de materias
    private static final String FILE = "subjects.json";  // Nombre del archivo JSON donde se guardan las materias

    public List<Subject> findAll() {  // Metodo que devuelve todas las materias
        List<Subject> subjects = JsonFileManager.readAll(FILE, Subject.class);  // Lee el archivo JSON y lo convierte en lista de materias
        return subjects != null ? subjects : new ArrayList<>();  // Si es null devuelve lista vacía, si no devuelve la lista leída
    }

    public Optional<Subject> findById(String id) {  // Metodo que busca una materia por su ID
        return findAll().stream()  // Convierte la lista en un flujo para procesarla
                .filter(s -> s.getId().equals(id))  // Filtra solo la materia cuyo ID coincida
                .findFirst();  // Devuelve la primera materia encontrada (como Optional)
    }

    public boolean existsByNombre(String nombre) {  // Metodo que verifica si ya existe una materia con ese nombre
        return findAll().stream()  // Convierte la lista en un flujo para procesarla
                .anyMatch(s -> s.getNombre().equalsIgnoreCase(nombre));  // Devuelve true si alguna materia tiene el mismo nombre (ignora mayúsculas)
    }

    public void save(Subject subject) {  // Metodo que guarda una nueva materia
        List<Subject> subjects = findAll();  // Obtiene todas las materias actuales del archivo
        subjects.add(subject);  // Agrega la nueva materia a la lista
        JsonFileManager.writeAll(FILE, subjects);  // Guarda la lista completa en el archivo (sobrescribe)
    }
}