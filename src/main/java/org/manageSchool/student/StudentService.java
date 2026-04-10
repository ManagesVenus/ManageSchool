package org.manageSchool.student;

import java.util.List;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.Validator;

public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // Crea un nuevo estudiante. Valida correo institucional y que no exista duplicado.
    public Student create(String nombre, String correo) {
        if (!Validator.isNotEmpty(nombre)) {
            throw new AppException("El nombre no puede estar vacío.");
        }

        if (!Validator.isNotEmpty(correo)) {
            throw new AppException("El correo no puede estar vacío.");
        }

        if (!Validator.isValidEmail(correo)) {
            throw new AppException("El correo debe pertenecer al dominio @colegio.edu.co.");
        }

        if (repo.existsByCorreo(correo)) {
            throw new AppException("Ya existe un estudiante con ese correo.");
        }

        Student student = Student.crear(nombre, correo);
        repo.save(student);
        return student;
    }

    // Devuelve todos los estudiantes registrados.
    public List<Student> listAll() {
        return repo.findAll();
    }

    // Busca un estudiante por su ID. Lanza AppException si no existe.
    public Student findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new AppException("Estudiante no encontrado."));
    }

    // Busca un estudiante por nombre. Lanza AppException si no existe.
    public Student findByNombre(String nombre) {
        return repo.findByNombre(nombre)
                .orElseThrow(() -> new AppException("Estudiante no encontrado."));
    }

    // Desactiva un estudiante por ID (baja lógica).
    public void deactivate(String id) {
        Student student = findById(id);
        student.setActivo(false);
        repo.update(student);
    }
}
