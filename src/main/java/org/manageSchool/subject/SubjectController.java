package org.manageSchool.subject;

import java.util.Scanner;

public class SubjectController {
    private final SubjectService service = new SubjectService();  // Instancia del servicio para acceder a la logica

    // ============ ISSUE-013: Solo crear materia ============
    public void mostrarMenuCrear(Scanner scanner) {  // Metodo que muestra el formulario para crear materia
        System.out.println("\n--- CREAR NUEVA MATERIA ---");  // Titulo de la seccion
        System.out.print("Nombre de la materia: ");  // Solicita el nombre al usuario
        String nombre = scanner.nextLine().trim();  // Lee el nombre y elimina espacios al inicio y final

        try {  // Intenta ejecutar la creacion
            Subject nueva = service.create(nombre);  // Llama al servicio para crear la materia
            System.out.println("Materia creada exitosamente!");  // Mensaje de exito
            System.out.println("   ID: " + nueva.getId());  // Muestra el ID generado
            System.out.println("   Nombre: " + nueva.getNombre());  // Muestra el nombre de la materia creada
        } catch (Exception e) {  // Captura cualquier error
            System.out.println("Error: " + e.getMessage());  // Muestra el mensaje de error
        }
    }
}