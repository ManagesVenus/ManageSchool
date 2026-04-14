package org.manageSchool.professor;

import org.manageSchool.auth.AuthRepository;
import org.manageSchool.auth.AuthService;
import org.manageSchool.shared.AppException;

import java.util.List;
import java.util.Scanner;

public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController() {
        AuthRepository authRepo = new AuthRepository();
        AuthService authService = new AuthService(authRepo);
        ProfessorRepository repo = new ProfessorRepository(authRepo);
        this.service = new ProfessorService(repo, authService);
    }

    // submenu de gestión de profesores (rol ADMIN)
    public void mostrarMenu(Scanner scanner) {
        boolean activo = true;

        while (activo) {
            System.out.println("\n  ===== GESTIONAR PROFESORES =====");
            System.out.println("  1. Crear profesor");
            System.out.println("  2. Listar profesores");
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
                case 1 -> crearProfesor(scanner);
                case 2 -> listarProfesores();
                case 3 -> activo = false;
                default -> System.out.println("  Opción inválida.");
            }
        }
    }

    // CP-PROF-001: solicita nombre, correo y contraseña temporal y crea el profesor
    private void crearProfesor(Scanner scanner) {
        System.out.println("\n  ===== CREAR PROFESOR =====");

        System.out.print("  Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Correo (@colegio.edu.co): ");
        String correo = scanner.nextLine().trim();

        System.out.print("  Contraseña temporal: ");
        String contrasena = scanner.nextLine().trim();

        try {
            Professor profesor = service.create(nombre, correo, contrasena);
            System.out.println("  Profesor creado exitosamente:");
            System.out.println("    Nombre: " + profesor.getNombre());
            System.out.println("    Correo: " + profesor.getCorreo());
            System.out.println("    Rol:    " + profesor.getRol());
        } catch (AppException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // ISSUE-028 / CP-PROF-002: muestra la lista de profesores registrados.
    private void listarProfesores() {
        List<Professor> profesores = service.listAllSorted();

        if (profesores.isEmpty()) {
            System.out.println("  No hay profesores registrados.");
            return;
        }

        System.out.println("\n  ===== LISTA DE PROFESORES =====");
        System.out.printf("  Total: %d profesor(es)%n%n", profesores.size());
        int i = 1;
        for (Professor p : profesores) {
            String estado = p.isActivo() ? "Activo" : "Inactivo";
            System.out.printf("  %d. %s | %s | %s%n",
                    i++, p.getNombre(), p.getCorreo(), estado);
        }
    }
}
