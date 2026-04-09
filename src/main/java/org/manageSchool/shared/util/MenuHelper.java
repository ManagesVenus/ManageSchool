package org.manageSchool.shared.util;

import java.util.Scanner;

public class MenuHelper {

    // Imprime el menú principal (sin sesión) y retorna la opción elegida
    public static int mostrarMenuPrincipal(Scanner scanner) {
        imprimirSeparador();
        System.out.println("  SCHOOLAPP CLI v1.0");
        imprimirSeparador();
        System.out.println("  1. Iniciar sesión");
        System.out.println("  2. Salir");
        imprimirSeparador();
        System.out.print("  Seleccione una opción: ");
        return leerOpcion(scanner);
    }

    // Imprime el menú según el rol y retorna la opción elegida
    public static int mostrarMenuPorRol(Scanner scanner, String rol) {
        switch (rol.toUpperCase()) {
            case "ADMIN" -> mostrarMenuAdmin();
            case "PROFESOR" -> mostrarMenuProfesor();
            case "ESTUDIANTE" -> mostrarMenuEstudiante();
            default -> System.out.println("Rol no reconocido: " + rol);
        }
        System.out.print("  Seleccione una opción: ");
        return leerOpcion(scanner);
    }

    private static void mostrarMenuAdmin() {
        imprimirSeparador();
        System.out.println("  MENÚ ADMINISTRADOR");
        imprimirSeparador();
        System.out.println("  1. Gestionar Estudiantes");
        System.out.println("  2. Gestionar Profesores");
        System.out.println("  3. Gestionar Materias");
        System.out.println("  4. Ver Ranking Trimestral");
        System.out.println("  5. Cerrar sesión");
        imprimirSeparador();
    }

    private static void mostrarMenuProfesor() {
        imprimirSeparador();
        System.out.println("  MENÚ PROFESOR");
        imprimirSeparador();
        System.out.println("  1. Gestionar Estudiantes");
        System.out.println("  2. Gestionar Tareas");
        System.out.println("  3. Gestionar Notas");
        System.out.println("  4. Cerrar sesión");
        imprimirSeparador();
    }

    private static void mostrarMenuEstudiante() {
        imprimirSeparador();
        System.out.println("  MENÚ ESTUDIANTE");
        imprimirSeparador();
        System.out.println("  1. Ver mis notas por tarea");
        System.out.println("  2. Ver promedio por materia");
        System.out.println("  3. Ver mi promedio general");
        System.out.println("  4. Cerrar sesión");
        imprimirSeparador();
    }

    // Lee un entero del scanner con reintentos
    public static int leerOpcion(Scanner scanner) {
        while (true) {
            try {
                String linea = scanner.nextLine().trim();
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.print("  Opción inválida, intente de nuevo: ");
            }
        }
    }

    // Limpia la consola (Windows y Unix)
    public static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Línea separadora visual
    public static void imprimirSeparador() {
        System.out.println("  ════════════════════════════════");
    }
}