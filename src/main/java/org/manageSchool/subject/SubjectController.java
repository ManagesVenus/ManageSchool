package org.manageSchool.subject;

import java.util.Scanner;
import java.util.List;

public class SubjectController {
    private final SubjectService service = new SubjectService();  // Instancia del servicio para acceder a la logica

    // ============ ISSUE-014: Menu completo ============
    public void mostrarMenu(Scanner scanner) {  // Metodo que muestra el menu de gestion de materias
        int opcion;
        do {
            System.out.println("\n=== GESTION DE MATERIAS ===");
            System.out.println("1. Crear materia");
            System.out.println("2. Listar materias");
            System.out.println("3. Editar materia");
            System.out.println("4. Eliminar materia");
            System.out.println("5. Volver");
            System.out.print("Opcion: ");

            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    flujoCrear(scanner);
                    break;
                case 2:
                    flujoListar();
                    break;
                case 3:
                    flujoEditar(scanner);
                    break;
                case 4:
                    flujoEliminar(scanner);
                    break;
                case 5:
                    System.out.println("Volviendo...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        } while (opcion != 5);
    }

    // ============ ISSUE-013: Solo crear materia ============
    public void flujoCrear(Scanner scanner) {  // Metodo que muestra el formulario para crear materia
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

    // ============ ISSUE-014: Listar materias ============
    private void flujoListar() {  // Metodo que muestra todas las materias
        System.out.println("\n--- LISTADO DE MATERIAS ---");
        List<Subject> materias = service.listAll();  // Obtiene todas las materias

        if (materias.isEmpty()) {  // Si no hay materias
            System.out.println("No hay materias registradas.");
            return;
        }

        System.out.println("ID     | NOMBRE                       | TIPO");
        System.out.println("-------|------------------------------|------------------");
        for (Subject s : materias) {  // Recorre cada materia
            String tipo = s.isPredeterminada() ? "Predeterminada" : "Personalizada";
            String idMostrar = s.getId().length() > 4 ? s.getId().substring(0, 4) : s.getId();
            String nombreMostrar = s.getNombre().length() > 26 ?
                    s.getNombre().substring(0, 23) + "..." : s.getNombre();
            System.out.printf("%-6s | %-28s | %s\n", idMostrar, nombreMostrar, tipo);
        }
    }

    // ============ ISSUE-014: Editar materia ============
    private void flujoEditar(Scanner scanner) {  // Metodo para editar una materia
        System.out.println("\n--- EDITAR MATERIA ---");
        flujoListar();  // Muestra las materias primero

        System.out.print("Ingrese el ID de la materia a editar: ");
        String id = scanner.nextLine().trim();

        System.out.print("Nuevo nombre: ");
        String nuevoNombre = scanner.nextLine().trim();

        try {
            Subject actualizada = service.update(id, nuevoNombre);
            System.out.println("Materia actualizada exitosamente!");
            System.out.println("   Nuevo nombre: " + actualizada.getNombre());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ============ ISSUE-014: Eliminar materia ============
    private void flujoEliminar(Scanner scanner) {  // Metodo para eliminar una materia
        System.out.println("\n--- ELIMINAR MATERIA ---");
        flujoListar();  // Muestra las materias primero

        System.out.print("Ingrese el ID de la materia a eliminar: ");
        String id = scanner.nextLine().trim();

        System.out.print("Esta seguro? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (!confirmacion.equals("s")) {  // Si no confirma
            System.out.println("Operacion cancelada.");
            return;
        }

        try {
            service.delete(id);
            System.out.println("Materia eliminada exitosamente!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ============ Metodo auxiliar para leer opciones ============
    private int leerOpcion(Scanner scanner) {  // Lee un numero del scanner
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}