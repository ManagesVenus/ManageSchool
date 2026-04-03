package org.manageSchool.subject;  // Define el paquete donde está esta clase

import java.util.List;  // Importa List para manejar la lista de materias predeterminadas
import java.util.UUID;  // Importa UUID para generar IDs únicos

public class SubjectService {  // Clase que contiene la lógica de negocio para materias
    private final SubjectRepository repo = new SubjectRepository();  // Instancia del repositorio para acceder a datos

    private static final List<String> DEFAULT_SUBJECTS = List.of(  // Lista de las 5 materias predeterminadas
            "Matemáticas",  // Primera materia
            "Español",  // Segunda materia
            "Ciencias Naturales",  // Tercera materia
            "Ciencias Sociales",  // Cuarta materia
            "Inglés"  // Quinta materia
    );

    // ============ ISSUE-012: Carga automática de materias predeterminadas ============

    public void seedDefaultSubjects() {  // Metodo que carga las 5 materias predeterminadas al iniciar el sistema
        for (String nombre : DEFAULT_SUBJECTS) {  // Recorre cada nombre de materia predeterminada
            if (!repo.existsByNombre(nombre)) {  // Si la materia NO existe aún en el archivo
                Subject subject = new Subject(  // Crea un nuevo objeto Subject
                        UUID.randomUUID().toString(),  // Genera un ID único universal
                        nombre,  // Asigna el nombre de la materia
                        true,  // predeterminada = true (es una materia base del sistema)
                        true  // activa = true (está habilitada para usar)
                );
                repo.save(subject);  // Guarda la materia en el archivo subjects.json
                System.out.println("Materia predeterminada creada: " + nombre);  // Mensaje de confirmación
            }
        }
    }
}