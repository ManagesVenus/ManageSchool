package org.manageSchool;

import org.manageSchool.auth.AuthController;
import org.manageSchool.auth.AuthRepository;
import org.manageSchool.auth.AuthService;
import org.manageSchool.auth.User;
import org.manageSchool.shared.util.MenuHelper;
import org.manageSchool.student.StudentController;
import org.manageSchool.professor.ProfessorController;
import org.manageSchool.subject.SubjectService;
import org.manageSchool.subject.SubjectController;
import org.manageSchool.ranking.RankingController;
import org.manageSchool.task.TaskController;
import org.manageSchool.grade.GradeController;

import java.util.Scanner;
import java.util.List;
import java.util.stream.Collectors;

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
        StudentController studentController = new StudentController();
        ProfessorController professorController = new ProfessorController();
        SubjectController subjectController = new SubjectController();
        RankingController rankingController = new RankingController();
        TaskController taskController = new TaskController();
        GradeController gradeController = new GradeController();

        while (sesionActiva) {
            int opcion = MenuHelper.mostrarMenuPorRol(scanner, usuario.getRol());

            switch (usuario.getRol().toUpperCase()) {
                case "ADMIN" -> {
                    switch (opcion) {
                        case 1 -> studentController.mostrarMenu(scanner);
                        case 2 -> professorController.mostrarMenu(scanner);
                        case 3 -> subjectController.mostrarMenu(scanner);
                        case 4 -> rankingController.mostrarMenu(scanner);
                        case 5 -> sesionActiva = false; // Cerrar sesión (ISSUE-005)
                        default -> System.out.println("  Opción inválida.");
                    }
                }
                case "PROFESOR" -> {
                    switch (opcion) {
                        case 1 -> taskController.mostrarMenu(scanner, usuario.getCorreo());
                        case 2 -> gradeController.mostrarMenuRegistrar(scanner, usuario.getCorreo());
                        case 3 -> gradeController.mostrarMenuListarPorTarea(scanner);
                        case 4 -> {
                            // Ver promedios: pide ID del estudiante y usa las materias predeterminadas
                            System.out.print("  ID del estudiante: ");
                            String estId = scanner.nextLine().trim();
                            List<org.manageSchool.subject.Subject> materias =
                                    new org.manageSchool.subject.SubjectService().listAll().stream()
                                            .filter(org.manageSchool.subject.Subject::isPredeterminada)
                                            .collect(java.util.stream.Collectors.toList());
                            List<String> ids = materias.stream()
                                    .map(org.manageSchool.subject.Subject::getId)
                                    .collect(java.util.stream.Collectors.toList());
                            List<String> nombres = materias.stream()
                                    .map(org.manageSchool.subject.Subject::getNombre)
                                    .collect(java.util.stream.Collectors.toList());
                            gradeController.mostrarMenuPromedioPorMateria(scanner, estId, ids, nombres);
                        }
                        case 5 -> sesionActiva = false;
                        default -> System.out.println("  Opción inválida.");
                    }
                }
                case "ESTUDIANTE" -> {
                    String estId = usuario.getCorreo(); // el correo actúa como identificador
                    List<org.manageSchool.subject.Subject> materias =
                            new org.manageSchool.subject.SubjectService().listAll().stream()
                                    .filter(org.manageSchool.subject.Subject::isPredeterminada)
                                    .collect(java.util.stream.Collectors.toList());
                    List<String> ids = materias.stream()
                            .map(org.manageSchool.subject.Subject::getId)
                            .collect(java.util.stream.Collectors.toList());
                    List<String> nombres = materias.stream()
                            .map(org.manageSchool.subject.Subject::getNombre)
                            .collect(java.util.stream.Collectors.toList());
                    switch (opcion) {
                        case 1 -> gradeController.mostrarMenuVerNotasPorTarea(scanner, estId);
                        case 2 -> gradeController.mostrarMenuPromedioPorMateria(scanner, estId, ids, nombres);
                        case 3 -> gradeController.mostrarMenuPromedioGeneral(scanner, estId, ids);
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