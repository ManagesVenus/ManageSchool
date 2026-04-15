package org.manageSchool.student;

import java.util.List;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.Validator;

public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // Crea un nuevo estudiante
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

        int newId = repo.findAll().size() + 1;

        Student student = new Student(
                newId,
                nombre,
                correo,
                true,
                java.time.LocalDate.now().toString()
        );

        repo.save(student);
        return student;
    }

    // Listar
    public List<Student> listAll() {
        return repo.findAll();
    }

    // Buscar por ID
    public Student findById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new AppException("Estudiante no encontrado."));
    }

    // Eliminar
    public void deleteById(int id) {
        Student student = findById(id);
        repo.deleteById(id);
    }

    // Editar
    public void update(int id, String nombre, String correo) {
        Student student = findById(id);

        if (!nombre.isEmpty()) {
            student.setNombre(nombre);
        }

        if (!correo.isEmpty()) {
            if (!Validator.isValidEmail(correo)) {
                throw new AppException("Correo inválido.");
            }
            student.setCorreo(correo);
        }

        repo.update(student);
    }
}