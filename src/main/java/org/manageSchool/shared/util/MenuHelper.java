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

    /**
     * Limpia la consola de forma compatible con Windows y Unix.
     *
     * En Windows intenta ejecutar "cls" vía ProcessBuilder; si falla
     * (p. ej. en IDEs con consola integrada), cae a escapes ANSI.
     * En Unix/Mac usa escapes ANSI directamente.
     */
    public static void limpiarPantalla() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback: si ProcessBuilder falla (IDE, Docker, etc.), usar ANSI
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    // Línea separadora visual
    public static void imprimirSeparador() {
        System.out.println("  ════════════════════════════════");
    }

    /**
     * Muestra una tabla simple en consola con columnas alineadas.
     *
     * Calcula el ancho de cada columna como el máximo entre la longitud del
     * encabezado y la longitud del valor más largo de esa columna.
     *
     * @param headers Encabezados de columna (no puede ser null ni vacío)
     * @param rows    Filas, cada una con el mismo número de columnas que headers
     *                (valores null se muestran como cadena vacía)
     */
    public static void mostrarTabla(String[] headers, java.util.List<String[]> rows) {
        if (headers == null || headers.length == 0) {
            System.out.println("  Tabla vacía: no hay encabezados.");
            return;
        }
        if (rows == null) {
            rows = java.util.List.of();
        }

        int cols = headers.length;
        int[] anchos = new int[cols];

        // Ancho inicial = longitud del encabezado
        for (int c = 0; c < cols; c++) {
            anchos[c] = headers[c] == null ? 0 : headers[c].length();
        }

        // Ajustar según cada fila
        for (String[] row : rows) {
            if (row == null) continue;
            for (int c = 0; c < cols && c < row.length; c++) {
                String val = row[c] == null ? "" : row[c];
                if (val.length() > anchos[c]) {
                    anchos[c] = val.length();
                }
            }
        }

        // Imprimir encabezado y separador
        System.out.println("  " + formatearFila(headers, anchos));
        System.out.println("  " + lineaSeparadoraTabla(anchos));

        // Imprimir filas
        if (rows.isEmpty()) {
            System.out.println("  (sin datos)");
            return;
        }
        for (String[] row : rows) {
            String[] safe = new String[cols];
            for (int c = 0; c < cols; c++) {
                safe[c] = (row != null && c < row.length && row[c] != null) ? row[c] : "";
            }
            System.out.println("  " + formatearFila(safe, anchos));
        }
    }

    private static String formatearFila(String[] valores, int[] anchos) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < valores.length; c++) {
            sb.append(String.format("%-" + anchos[c] + "s",
                    valores[c] == null ? "" : valores[c]));
            if (c < valores.length - 1) sb.append(" | ");
        }
        return sb.toString();
    }

    private static String lineaSeparadoraTabla(int[] anchos) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < anchos.length; c++) {
            sb.append("-".repeat(anchos[c]));
            if (c < anchos.length - 1) sb.append("-+-");
        }
        return sb.toString();
    }
}