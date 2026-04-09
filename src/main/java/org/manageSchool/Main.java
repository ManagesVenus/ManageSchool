package org.manageSchool;

import org.manageSchool.auth.AuthController;
import org.manageSchool.auth.AuthRepository;
import org.manageSchool.auth.AuthService;
import org.manageSchool.auth.User;
import org.manageSchool.shared.util.MenuHelper;
import org.manageSchool.subject.SubjectService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Inicialización
        SubjectService subjectService = new SubjectService();
        subjectService.seedDefaultSubjects();

        AuthService authService = new AuthService(new AuthRepository());
        authService.seedDefaultAdmin();

        Scanner scanner = new Scanner(System.in);
        AuthController authController = new AuthController();

        System.out.println("Sistema inicializado correctamente.");

        // Loop principal del sistema
        while (true) {
            int opcion = MenuHelper.mostrarMenuPrincipal(scanner);

            switch (opcion) {
                case 1 -> {
                    User usuario = authController.mostrarLogin(scanner);
                    if (usuario != null) {
                        // CP-AUTH-003: mensaje de bienvenida con rol
                        String rolLabel = switch (usuario.getRol().toUpperCase()) {
                            case "ADMIN" -> "Administrador";
                            case "PROFESOR" -> "Profesor";
                            case "ESTUDIANTE" -> "Estudiante";
                            default -> usuario.getRol();
                        };
                        System.out.println("\n  Bienvenido, " + rolLabel + " (" + usuario.getCorreo() + ")");

                        // Loop de menú por rol
                        manejarSesion(scanner, usuario, authController);
                    }
                }
                case 2 -> {
                    System.out.println("¡Hasta luego!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("  Opción inválida. Intente de nuevo.");
            }
        }
    }

    // Maneja la sesión del usuario autenticado según su rol
    private static void manejarSesion(Scanner scanner, User usuario, AuthController authController) {
        boolean sesionActiva = true;

        while (sesionActiva) {
            int opcion = MenuHelper.mostrarMenuPorRol(scanner, usuario.getRol());

            switch (usuario.getRol().toUpperCase()) {
                case "ADMIN" -> {
                    switch (opcion) {
                        case 1 -> System.out.println("  [Gestionar Estudiantes — pendiente de implementar]");
                        case 2 -> System.out.println("  [Gestionar Profesores — pendiente de implementar]");
                        case 3 -> System.out.println("  [Gestionar Materias — pendiente de implementar]");
                        case 4 -> System.out.println("  [Ver Ranking Trimestral — pendiente de implementar]");
                        case 5 -> sesionActiva = false; // Cerrar sesión (ISSUE-005)
                        default -> System.out.println("  Opción inválida.");
                    }
                }
                case "PROFESOR" -> {
                    switch (opcion) {
                        case 1 -> System.out.println("  [Gestionar Estudiantes — pendiente de implementar]");
                        case 2 -> System.out.println("  [Gestionar Tareas — pendiente de implementar]");
                        case 3 -> System.out.println("  [Gestionar Notas — pendiente de implementar]");
                        case 4 -> sesionActiva = false;
                        default -> System.out.println("  Opción inválida.");
                    }
                }
                case "ESTUDIANTE" -> {
                    switch (opcion) {
                        case 1 -> System.out.println("  [Ver mis notas por tarea — pendiente de implementar]");
                        case 2 -> System.out.println("  [Ver promedio por materia — pendiente de implementar]");
                        case 3 -> System.out.println("  [Ver mi promedio general — pendiente de implementar]");
                        case 4 -> sesionActiva = false;
                        default -> System.out.println("  Opción inválida.");
                    }
                }
            }
        }

        // ISSUE-005: limpiar sesión y redirigir
        System.out.println("  Sesión cerrada correctamente.");
    }
}