package org.manageSchool.grade;  // Define el paquete donde esta esta clase

import java.util.Scanner;  // Importa Scanner para leer entrada del usuario
import java.util.List;  // Para mostrarMenuListarPorTarea

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

    // ============ ISSUE-018: Listar notas por tarea ============
    public void mostrarMenuListarPorTarea(Scanner scanner) {  // Muestra todas las notas de una tarea
        System.out.println("\n--- LISTAR NOTAS POR TAREA ---");  // Titulo de la seccion

        System.out.print("ID de la tarea: ");  // Solicita ID de la tarea
        String tareaId = scanner.nextLine().trim();  // Lee el ID

        try {  // Intenta ejecutar el listado
            List<Grade> notas = service.listByTask(tareaId);  // Obtiene las notas

            if (notas.isEmpty()) {  // Si no hay notas
                System.out.println("No hay notas registradas para esta tarea.");  // Mensaje informativo
                return;  // Sale del metodo
            }

            // Cabecera de la tabla
            System.out.println("\nID     | ESTUDIANTE ID | NOTA | FECHA");
            System.out.println("-------|---------------|------|----------");
            for (Grade g : notas) {  // Recorre cada nota
                String idMostrar = g.getId().length() > 6 ? g.getId().substring(0, 6) : g.getId();  // Acorta ID
                String estIdMostrar = g.getEstudianteId().length() > 13 ?
                        g.getEstudianteId().substring(0, 13) : g.getEstudianteId();  // Acorta ID estudiante
                System.out.printf("%-6s | %-13s | %-4.1f | %s\n",  // Imprime fila formateada
                        idMostrar, estIdMostrar, g.getValor(), g.getFechaRegistro());
            }
        } catch (Exception e) {  // Captura cualquier error
            System.out.println("Error: " + e.getMessage());  // Muestra el mensaje de error
        }
    }

    // ============ ISSUE-018: Editar nota ============
    public void mostrarMenuEditar(Scanner scanner) {  // Edita una nota existente
        System.out.println("\n--- EDITAR NOTA ---");  // Titulo de la seccion

        System.out.print("ID de la nota a editar: ");  // Solicita ID de la nota
        String gradeId = scanner.nextLine().trim();  // Lee el ID

        System.out.print("Nuevo valor (0.0 a 5.0): ");  // Solicita el nuevo valor
        double nuevoValor;  // Variable para guardar el valor
        try {  // Intenta convertir a double
            nuevoValor = Double.parseDouble(scanner.nextLine().trim());  // Convierte el valor
        } catch (NumberFormatException e) {  // Si no es un numero valido
            System.out.println("Error: Debe ingresar un numero valido.");  // Mensaje de error
            return;  // Sale del metodo
        }

        try {  // Intenta ejecutar la edicion
            Grade actualizada = service.update(gradeId, nuevoValor);  // Llama al servicio
            System.out.println("Nota actualizada exitosamente!");  // Mensaje de exito
            System.out.println("   Nuevo valor: " + actualizada.getValor());  // Muestra el nuevo valor
            System.out.println("   Fecha de actualizacion: " + actualizada.getFechaRegistro());  // Muestra la fecha
        } catch (Exception e) {  // Captura cualquier error
            System.out.println("Error: " + e.getMessage());  // Muestra el mensaje de error
        }
    }

    // ============ ISSUE-018: Eliminar nota ============
    public void mostrarMenuEliminar(Scanner scanner) {  // Elimina una nota
        System.out.println("\n--- ELIMINAR NOTA ---");  // Titulo de la seccion

        System.out.print("ID de la nota a eliminar: ");  // Solicita ID de la nota
        String gradeId = scanner.nextLine().trim();  // Lee el ID

        System.out.print("Esta seguro? (s/n): ");  // Solicita confirmacion
        String confirmacion = scanner.nextLine().trim().toLowerCase();  // Lee y convierte a minusculas

        if (!confirmacion.equals("s")) {  // Si no confirma
            System.out.println("Operacion cancelada.");  // Mensaje de cancelacion
            return;  // Sale del metodo
        }

        try {  // Intenta ejecutar la eliminacion
            service.delete(gradeId);  // Llama al servicio
            System.out.println("Nota eliminada exitosamente!");  // Mensaje de exito
        } catch (Exception e) {  // Captura cualquier error
            System.out.println("Error: " + e.getMessage());  // Muestra el mensaje de error
        }
    }

    // ============ ISSUE-019: Estudiante ve sus notas por tarea ============
    public void mostrarMenuVerNotasPorTarea(Scanner scanner, String estudianteId) {  // Muestra las notas del estudiante
        System.out.println("\n--- MIS NOTAS POR TAREA ---");  // Titulo de la seccion

        try {  // Intenta ejecutar el listado
            List<Grade> notas = service.listByStudent(estudianteId);  // Obtiene las notas del estudiante

            if (notas.isEmpty()) {  // Si no hay notas
                System.out.println("Aun no tienes notas registradas.");  // Mensaje informativo
                return;  // Sale del metodo
            }

            // Cabecera de la tabla
            System.out.println("\nMATERIA     | TAREA ID | NOTA | FECHA");
            System.out.println("------------|----------|------|----------");
            for (Grade g : notas) {  // Recorre cada nota
                String materiaId = g.getMateriaId().length() > 10 ?
                        g.getMateriaId().substring(0, 10) : g.getMateriaId();  // Acorta ID materia
                String tareaId = g.getTareaId().length() > 8 ?
                        g.getTareaId().substring(0, 8) : g.getTareaId();  // Acorta ID tarea
                System.out.printf("%-10s | %-8s | %-4.1f | %s\n",  // Imprime fila formateada
                        materiaId, tareaId, g.getValor(), g.getFechaRegistro());
            }
        } catch (Exception e) {  // Captura cualquier error
            System.out.println("Error: " + e.getMessage());  // Muestra el mensaje de error
        }
    }
}