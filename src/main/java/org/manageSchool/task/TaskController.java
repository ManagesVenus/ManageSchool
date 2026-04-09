package org.manageSchool.task;

import org.manageSchool.shared.AppException;
import org.manageSchool.subject.SubjectService;
import org.manageSchool.subject.Subject;
import org.manageSchool.task.Task;
import org.manageSchool.task.TaskService;

import java.util.List;
import java.util.Scanner;


public class TaskController {

    private final TaskService    service        = new TaskService();
    private final SubjectService subjectService = new SubjectService();


    public void mostrarMenu(Scanner scanner, String profesorId) {
        boolean volver = false;

        while (!volver) {
            imprimirMenuTareas();
            int opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1 -> flujoCrear(scanner, profesorId);
                case 2 -> flujoListar(scanner, profesorId);
                case 3 -> flujoEditar(scanner, profesorId);
                case 4 -> flujoEliminar(scanner, profesorId);
                case 5 -> volver = true;
                default -> System.out.println("  ⚠ Opción inválida. Ingrese un número del 1 al 5.");
            }
        }
    }



    private void flujoCrear(Scanner scanner, String profesorId) {
        System.out.println("\n--- CREAR NUEVA TAREA ---");

        List<Subject> materias = subjectService.listAll();
        if (materias.isEmpty()) {
            System.out.println("  ⚠ No hay materias disponibles en el sistema.");
            return;
        }

        imprimirMaterias(materias);
        System.out.print("  Seleccione el número de la materia: ");
        int seleccion = leerOpcion(scanner);

        if (seleccion < 1 || seleccion > materias.size()) {
            System.out.println("  ⚠ Selección inválida.");
            return;
        }

        Subject materiaElegida = materias.get(seleccion - 1);
        System.out.println("  Materia seleccionada: " + materiaElegida.getNombre());

        System.out.print("  Título de la tarea *: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("  Descripción (Enter para omitir): ");
        String descripcion = scanner.nextLine().trim();
        if (descripcion.isEmpty()) descripcion = null;

        System.out.print("  Fecha límite yyyy-MM-dd (Enter para omitir): ");
        String fechaLimite = scanner.nextLine().trim();
        if (fechaLimite.isEmpty()) fechaLimite = null;

        try {
            Task creada = service.create(
                    titulo, descripcion, fechaLimite,
                    materiaElegida.getId(),   // ID real desde subjects.json
                    profesorId
            );
            System.out.println("\n  ✅ Tarea creada exitosamente.");
            imprimirDetalleTarea(creada, materiaElegida.getNombre());
        } catch (AppException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
    }

    private void flujoListar(Scanner scanner, String profesorId) {
        System.out.println("\n--- VER TAREAS POR MATERIA ---");

        List<Subject> materias = subjectService.listAll();
        if (materias.isEmpty()) {
            System.out.println("  ⚠ No hay materias disponibles.");
            return;
        }

        imprimirMaterias(materias);
        System.out.print("  Seleccione el número de la materia: ");
        int seleccion = leerOpcion(scanner);

        if (seleccion < 1 || seleccion > materias.size()) {
            System.out.println("  ⚠ Selección inválida.");
            return;
        }

        Subject materiaElegida = materias.get(seleccion - 1);
        List<Task> tareas = service.listBySubject(materiaElegida.getId(), profesorId);

        System.out.println("\n  Tareas de: " + materiaElegida.getNombre());
        imprimirSeparador();

        if (tareas.isEmpty()) {
            System.out.println("  (No tienes tareas en esta materia)");
        } else {
            imprimirCabeceraTareas();
            tareas.forEach(this::imprimirFilaTarea);
        }

        imprimirSeparador();
    }

    private void flujoEditar(Scanner scanner, String profesorId) {
        System.out.println("\n--- EDITAR TAREA ---");
        System.out.println("  (Usa 'Ver tareas por materia' para consultar los IDs)");
        System.out.print("  ID de la tarea a editar: ");
        String taskId = scanner.nextLine().trim();

        if (taskId.isEmpty()) {
            System.out.println("  ⚠ Operación cancelada.");
            return;
        }

        System.out.print("  Nuevo título *: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("  Nueva descripción (Enter para dejar en blanco): ");
        String descripcion = scanner.nextLine().trim();
        if (descripcion.isEmpty()) descripcion = null;

        System.out.print("  Nueva fecha límite yyyy-MM-dd (Enter para dejar en blanco): ");
        String fechaLimite = scanner.nextLine().trim();
        if (fechaLimite.isEmpty()) fechaLimite = null;

        try {
            service.update(taskId, titulo, descripcion, fechaLimite, profesorId);
            // Mensaje exacto del CP-TASK-003
            System.out.println("\n  ✅ Tarea actualizada correctamente.");
        } catch (AppException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
    }

    private void flujoEliminar(Scanner scanner, String profesorId) {
        System.out.println("\n--- ELIMINAR TAREA ---");
        System.out.println("  (Usa 'Ver tareas por materia' para consultar los IDs)");
        System.out.print("  ID de la tarea a eliminar: ");
        String taskId = scanner.nextLine().trim();

        if (taskId.isEmpty()) {
            System.out.println("  ⚠ Operación cancelada.");
            return;
        }

        int cantidadNotas = service.countGradesByTask(taskId);

        if (cantidadNotas > 0) {
            System.out.printf(
                    "%n  ⚠ Esta tarea tiene %d nota(s). Se eliminarán también. ¿Continuar? (s/n): ",
                    cantidadNotas
            );
        } else {
            System.out.print("  ¿Confirmar eliminación? (s/n): ");
        }

        String respuesta = scanner.nextLine().trim().toLowerCase();
        boolean confirmar = respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");

        if (!confirmar) {
            System.out.println("  Operación cancelada.");
            return;
        }

        try {
            service.delete(taskId, profesorId);
            System.out.println("  ✅ Tarea eliminada correctamente.");
        } catch (AppException e) {
            System.out.println("  ❌ " + e.getMessage());
        }
    }

    private void imprimirMenuTareas() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║     GESTIÓN DE TAREAS        ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. Crear tarea              ║");
        System.out.println("║  2. Ver tareas por materia   ║");
        System.out.println("║  3. Editar tarea             ║");
        System.out.println("║  4. Eliminar tarea           ║");
        System.out.println("║  5. Volver                   ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("  Opción: ");
    }
    
    private void imprimirMaterias(List<Subject> materias) {
        System.out.println();
        for (int i = 0; i < materias.size(); i++) {
            String tipo = materias.get(i).isPredeterminada() ? " (predeterminada)" : "";
            System.out.printf("  %d. %s%s%n", i + 1, materias.get(i).getNombre(), tipo);
        }
        System.out.println();
    }

    private void imprimirCabeceraTareas() {
        System.out.printf("  %-36s %-30s %-12s%n", "ID", "TÍTULO", "FECHA LÍMITE");
        System.out.println("  " + "-".repeat(80));
    }

    private void imprimirFilaTarea(Task t) {
        String fecha  = t.getFechaLimite() != null ? t.getFechaLimite() : "(sin fecha)";
        String titulo = t.getTitulo().length() > 28
                ? t.getTitulo().substring(0, 25) + "..."
                : t.getTitulo();
        System.out.printf("  %-36s %-30s %-12s%n", t.getId(), titulo, fecha);
    }

    private void imprimirDetalleTarea(Task t, String materiaNombre) {
        System.out.println("  ─────────────────────────────────────");
        System.out.println("  ID          : " + t.getId());
        System.out.println("  Título      : " + t.getTitulo());
        System.out.println("  Materia     : " + materiaNombre);
        System.out.println("  Descripción : " + (t.getDescripcion() != null ? t.getDescripcion() : "(sin descripción)"));
        System.out.println("  Fecha límite: " + (t.getFechaLimite() != null ? t.getFechaLimite() : "(sin fecha)"));
        System.out.println("  ─────────────────────────────────────");
    }

    private void imprimirSeparador() {
        System.out.println("  " + "─".repeat(80));
    }

    private int leerOpcion(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}