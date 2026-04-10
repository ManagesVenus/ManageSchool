package org.manageSchool.student;

import java.util.List;
import java.util.Scanner;

import org.manageSchool.shared.AppException;

public class StudentController {

    private final StudentService service = new StudentService(new StudentRepository());

    // Muestra el submenú de gestión de estudiantes y despacha las opciones.
    public void mostrarMenu(Scanner scanner) {
        boolean activo = true;

        while (activo) {
            System.out.println("\n  ===== GESTIONAR ESTUDIANTES =====");
            System.out.println("  1. Crear estudiante");
            System.out.println("  2. Listar estudiantes");
            System.out.println("  3. Volver");
            System.out.print("  Seleccione una opción: ");

            String linea = scanner.nextLine().trim();
            int opcion;
            try {
                opcion = Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("  Opción inválida.");
                continue;
            }

            switch (opcion) {
                case 1 -> crearEstudiante(scanner);
                case 2 -> listarEstudiantes();
                case 3 -> activo = false;
                default -> System.out.println("  Opción inválida.");
            }
        }
    }

    // Solicita los datos por consola y crea un nuevo estudiante.
    private void crearEstudiante(Scanner scanner) {
        System.out.println("\n  ===== CREAR ESTUDIANTE =====");

        System.out.print("  Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Correo (@colegio.edu.co): ");
        String correo = scanner.nextLine().trim();

        try {
            Student student = service.create(nombre, correo);
            System.out.println("  Estudiante creado: " + student.getNombre() + " (" + student.getCorreo() + ")");
        } catch (AppException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Imprime la lista de estudiantes registrados en el sistema.
    private void listarEstudiantes() {
        List<Student> students = service.listAll();

        if (students.isEmpty()) {
            System.out.println("  No hay estudiantes registrados.");
            return;
        }

        System.out.println("\n  ===== LISTA DE ESTUDIANTES =====");
        for (Student s : students) {
            String estado = s.isActivo() ? "Activo" : "Inactivo";
            System.out.println("  - " + s.getNombre() + " | " + s.getCorreo() + " | " + estado);
        }
    }
}
