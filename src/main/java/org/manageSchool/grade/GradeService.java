package org.manageSchool.grade;  // Define el paquete donde esta esta clase

import java.time.LocalDate;  // Importa LocalDate para generar la fecha actual
import java.util.UUID;  // Importa UUID para generar IDs unicos

public class GradeService {  // Clase que contiene la logica de negocio para notas
    private final GradeRepository repo = new GradeRepository();  // Instancia del repositorio

    public Grade create(String estudianteId, String tareaId, String materiaId,
                        double valor, String profesorId) {  // Metodo para registrar una nota
        // Validacion 1: La nota debe estar entre 0.0 y 5.0
        if (valor < 0.0 || valor > 5.0) {  // Si esta fuera del rango
            throw new RuntimeException("La nota debe estar entre 0.0 y 5.0");  // Lanza error
        }

        // Validacion 2: No puede existir otra nota para el mismo estudiante en la misma tarea
        if (repo.existsByStudentAndTask(estudianteId, tareaId)) {  // Si ya existe
            throw new RuntimeException("Ya existe una nota para este estudiante en esta tarea. Use editar.");  // Lanza error
        }

        // Validacion 3: Los IDs no pueden ser nulos o vacios
        if (estudianteId == null || estudianteId.trim().isEmpty()) {  // Si estudianteId es invalido
            throw new RuntimeException("El ID del estudiante no puede estar vacio.");  // Lanza error
        }
        if (tareaId == null || tareaId.trim().isEmpty()) {  // Si tareaId es invalido
            throw new RuntimeException("El ID de la tarea no puede estar vacio.");  // Lanza error
        }
        if (profesorId == null || profesorId.trim().isEmpty()) {  // Si profesorId es invalido
            throw new RuntimeException("El ID del profesor no puede estar vacio.");  // Lanza error
        }

        // Crear la nueva nota
        Grade grade = new Grade(  // Crea un nuevo objeto Grade
                UUID.randomUUID().toString(),  // Genera un ID unico universal
                estudianteId,  // Asigna el ID del estudiante
                tareaId,  // Asigna el ID de la tarea
                materiaId,  // Asigna el ID de la materia
                valor,  // Asigna el valor de la nota
                LocalDate.now().toString(),  // Genera la fecha actual en formato yyyy-MM-dd
                profesorId  // Asigna el ID del profesor
        );

        repo.save(grade);  // Guarda la nota en el archivo grades.json
        return grade;  // Devuelve la nota creada
    }
}