package org.manageSchool.grade;  // Define el paquete donde esta esta clase

import java.util.Scanner;  // Importa Scanner para leer entrada del usuario

public class GradeController {  // Clase que maneja la interaccion con el usuario para notas
    private final GradeService service = new GradeService();  // Instancia del servicio

    public void mostrarMenuRegistrar(Scanner scanner, String profesorId) {  // Metodo para registrar una nota
        System.out.println("\n--- REGISTRAR NOTA ---");  // Titulo de la seccion

        // Solicitar datos al profesor
        System.out.print("ID del estudiante: ");  // Solicita ID del estudiante
        String estudianteId = scanner.nextLine().trim();  // Lee el ID

        System.out.print("ID de la tarea: ");  // Solicita ID de la tarea
        String tareaId = scanner.nextLine().trim();  // Lee el ID

        System.out.print("ID de la materia: ");  // Solicita ID de la materia
        String materiaId = scanner.nextLine().trim();  // Lee el ID

        System.out.print("Valor de la nota (0.0 a 5.0): ");  // Solicita el valor
        double valor;  // Variable para guardar el valor
        try {  // Intenta convertir a double
            valor = Double.parseDouble(scanner.nextLine().trim());  // Convierte el valor
        } catch (NumberFormatException e) {  // Si no es un numero valido
            System.out.println("Error: Debe ingresar un numero valido.");  // Mensaje de error
            return;  // Sale del metodo
        }

        // Intentar registrar la nota
        try {  // Intenta ejecutar el registro
            Grade nueva = service.create(estudianteId, tareaId, materiaId, valor, profesorId);  // Llama al servicio
            System.out.println("Nota registrada exitosamente!");  // Mensaje de exito
            System.out.println("   ID: " + nueva.getId());  // Muestra el ID de la nota
            System.out.println("   Estudiante: " + nueva.getEstudianteId());  // Muestra ID del estudiante
            System.out.println("   Tarea: " + nueva.getTareaId());  // Muestra ID de la tarea
            System.out.println("   Nota: " + nueva.getValor());  // Muestra el valor
            System.out.println("   Fecha: " + nueva.getFechaRegistro());  // Muestra la fecha
        } catch (Exception e) {  // Captura cualquier error
            System.out.println("Error: " + e.getMessage());  // Muestra el mensaje de error
        }
    }
}