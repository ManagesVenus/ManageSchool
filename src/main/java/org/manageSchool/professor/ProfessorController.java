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
        this.service = new ProfessorService(repo, authService,
                new org.manageSchool.task.TaskRepository(),
                new org.manageSchool.grade.GradeRepository());
    }

    // submenu de gestión de profesores (rol ADMIN)
    public void mostrarMenu(Scanner scanner) {
        boolean activo = true;

        while (activo) {
            System.out.println("\n  ===== GESTIONAR PROFESORES =====");
            System.out.println("  1. Crear profesor");
            System.out.println("  2. Listar profesores");
            System.out.println("  3. Editar profesor");
            System.out.println("  4. Eliminar profesor");
            System.out.println("  5. Volver");
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
                case 3 -> editarProfesor(scanner);
                case 4 -> eliminarProfesor(scanner);
                case 5 -> activo = false;
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
    // ISSUE-029 / CP-PROF-003: edita nombre y/o correo de un profesor existente.
    private void editarProfesor(Scanner scanner) {
        System.out.println("\n  ===== EDITAR PROFESOR =====");

        List<Professor> profesores = service.listAllSorted();
        if (profesores.isEmpty()) {
            System.out.println("  No hay profesores registrados para editar.");
            return;
        }

        // Mostrar lista numerada para que el admin elija
        System.out.println("  Seleccione el profesor a editar:");
        int i = 1;
        for (Professor p : profesores) {
            System.out.printf("  %d. %s | %s%n", i++, p.getNombre(), p.getCorreo());
        }
        System.out.println("  0. Cancelar");
        System.out.print("  Opción: ");

        int seleccion;
        try {
            seleccion = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Opción inválida.");
            return;
        }

        if (seleccion == 0) {
            System.out.println("  Edición cancelada.");
            return;
        }
        if (seleccion < 1 || seleccion > profesores.size()) {
            System.out.println("  Opción fuera de rango.");
            return;
        }

        Professor objetivo = profesores.get(seleccion - 1);

        System.out.println("\n  Dejando un campo vacío se conserva el valor actual.");
        System.out.printf("  Nombre actual: %s%n", objetivo.getNombre());
        System.out.print("  Nuevo nombre: ");
        String nuevoNombre = scanner.nextLine().trim();

        System.out.printf("  Correo actual: %s%n", objetivo.getCorreo());
        System.out.print("  Nuevo correo (@colegio.edu.co): ");
        String nuevoCorreo = scanner.nextLine().trim();

        try {
            Professor actualizado = service.update(objetivo.getId(), nuevoNombre, nuevoCorreo);
            System.out.println("  Profesor actualizado correctamente.");
            System.out.printf("    Nombre: %s%n", actualizado.getNombre());
            System.out.printf("    Correo: %s%n", actualizado.getCorreo());
        } catch (AppException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }
    // ISSUE-030 / CP-PROF-004, CP-PROF-005: elimina un profesor con confirmación.
    private void eliminarProfesor(Scanner scanner) {
        System.out.println("\n  ===== ELIMINAR PROFESOR =====");

        List<Professor> profesores = service.listAllSorted();
        if (profesores.isEmpty()) {
            System.out.println("  No hay profesores registrados para eliminar.");
            return;
        }

        // Mostrar lista numerada
        System.out.println("  Seleccione el profesor a eliminar:");
        int i = 1;
        for (Professor p : profesores) {
            System.out.printf("  %d. %s | %s%n", i++, p.getNombre(), p.getCorreo());
        }
        System.out.println("  0. Cancelar");
        System.out.print("  Opción: ");

        int seleccion;
        try {
            seleccion = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Opción inválida.");
            return;
        }

        if (seleccion == 0) {
            System.out.println("  Eliminación cancelada.");
            return;
        }
        if (seleccion < 1 || seleccion > profesores.size()) {
            System.out.println("  Opción fuera de rango.");
            return;
        }

        Professor objetivo = profesores.get(seleccion - 1);

        try {
            // Obtener info de tareas y notas asociadas
            int[] info = service.getDeletionInfo(objetivo.getId());
            int tareas = info[0];
            int notas  = info[1];

            // CP-PROF-004: aviso si tiene tareas asociadas
            if (tareas > 0) {
                System.out.printf(
                        "  Este profesor tiene %d tarea(s) y %d nota(s) asociadas.%n",
                        tareas, notas);
                System.out.println("  Al eliminarlo, quedarán huérfanas. ¿Desea continuar?");
            } else {
                System.out.printf("  ¿Está seguro de eliminar a %s?%n", objetivo.getNombre());
            }

            System.out.print("  Escriba 'SI' para confirmar: ");
            String confirmacion = scanner.nextLine().trim();

            if (!confirmacion.equalsIgnoreCase("SI")) {
                System.out.println("  Eliminación cancelada.");
                return;
            }

            // Eliminar (tareas y notas se conservan intactas - RN-10)
            service.delete(objetivo.getId());
            System.out.println("  Profesor eliminado correctamente.");

        } catch (AppException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }
}
