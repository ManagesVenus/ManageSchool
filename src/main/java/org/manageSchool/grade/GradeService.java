package org.manageSchool.grade;  // Define el paquete donde esta esta clase

import java.time.LocalDate;  // Importa LocalDate para generar la fecha actual
import java.util.UUID;  // Importa UUID para generar IDs unicos
import java.util.List;           // Para listByTask
import java.util.OptionalDouble; // Para calcularPromedioPorMateria


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

    // ============ ISSUE-018: Listar notas por tarea ============
    public List<Grade> listByTask(String tareaId) {  // Devuelve todas las notas de una tarea
        if (tareaId == null || tareaId.trim().isEmpty()) {  // Valida que no este vacio
            throw new RuntimeException("El ID de la tarea no puede estar vacio.");  // Error si esta vacio
        }
        return repo.findByTaskId(tareaId);  // Delega en el repositorio
    }

    // ============ ISSUE-018: Editar nota ============
    public Grade update(String gradeId, double nuevoValor) {  // Edita una nota existente
        if (nuevoValor < 0.0 || nuevoValor > 5.0) {  // Valida que la nota este en rango
            throw new RuntimeException("La nota debe estar entre 0.0 y 5.0");  // Error si esta fuera de rango
        }

        Grade grade = repo.findById(gradeId)  // Busca la nota por ID
                .orElseThrow(() -> new RuntimeException("Nota no encontrada."));  // Error si no existe

        grade.setValor(nuevoValor);  // Asigna el nuevo valor
        grade.setFechaRegistro(LocalDate.now().toString());  // Actualiza la fecha de registro
        repo.update(grade);  // Guarda los cambios
        return grade;  // Devuelve la nota actualizada
    }

    // ============ ISSUE-018: Eliminar nota ============
    public void delete(String gradeId) {  // Elimina una nota
        if (!repo.findById(gradeId).isPresent()) {  // Verifica si existe
            throw new RuntimeException("Nota no encontrada.");  // Error si no existe
        }
        repo.deleteById(gradeId);  // Elimina la nota
    }

    // ============ ISSUE-018: Calcular promedio por materia ============
    public OptionalDouble calcularPromedioPorMateria(String estudianteId, String materiaId) {  // Calcula el promedio de un estudiante en una materia
        List<Grade> grades = repo.findByStudentAndSubject(estudianteId, materiaId);  // Obtiene todas las notas

        if (grades.isEmpty()) {  // Si no hay notas
            return OptionalDouble.empty();  // Retorna vacio
        }

        double suma = 0.0;  // Variable para acumular la suma
        for (Grade g : grades) {  // Recorre cada nota
            suma += g.getValor();  // Suma el valor
        }
        return OptionalDouble.of(suma / grades.size());  // Retorna el promedio (suma / cantidad)
    }
}