package org.manageSchool.student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.manageSchool.shared.util.JsonFileManager;

public class StudentRepository {

    private static final String FILE = "students.json";

    // Devuelve todos los estudiantes del archivo
    public List<Student> findAll() {
        List<Student> students = JsonFileManager.readAll(FILE, Student.class);
        return students != null ? students : new ArrayList<>();
    }

    // Busca un estudiante por su ID
    public Optional<Student> findById(String id) {
        return findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    // Busca un estudiante por correo (ignora mayúsculas)
    public Optional<Student> findByCorreo(String correo) {
        return findAll().stream()
                .filter(s -> s.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    // Busca un estudiante por nombre (ignora mayúsculas)
    public Optional<Student> findByNombre(String nombre) {
        return findAll().stream()
                .filter(s -> s.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    // Verifica si ya existe un estudiante con ese correo
    public boolean existsByCorreo(String correo) {
        return findByCorreo(correo).isPresent();
    }

    // Guarda un nuevo estudiante
    public void save(Student student) {
        List<Student> students = findAll();
        students.add(student);
        JsonFileManager.writeAll(FILE, students);
    }

    // Actualiza un estudiante existente (busca por id y reemplaza)
    public void update(Student student) {
        List<Student> students = findAll();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(student.getId())) {
                students.set(i, student);
                break;
            }
        }
        JsonFileManager.writeAll(FILE, students);
    }

    // Elimina un estudiante por id
    public void deleteById(String id) {
        List<Student> students = findAll();
        students.removeIf(s -> s.getId().equals(id));
        JsonFileManager.writeAll(FILE, students);
    }
}
