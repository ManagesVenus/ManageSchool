package org.manageSchool.ranking;

import java.util.List;
import java.util.Scanner;

public class RankingController {  // Controlador de consola para el ranking trimestral

    private final RankingService rankingService = new RankingService();

    public void mostrarMenu(Scanner scanner) {  // Muestra el menú principal de ranking
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      RANKING TRIMESTRAL      ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Crear período trimestral ║");
            System.out.println("║  2. Cerrar trimestre         ║");
            System.out.println("║  3. Ver ranking              ║");
            System.out.println("║  4. Listar períodos          ║");
            System.out.println("║  0. Volver                   ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("  Opción: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> crearPeriodo(scanner);
                case "2" -> cerrarTrimestre(scanner);
                case "3" -> verRanking(scanner);
                case "4" -> listarPeriodos();
                case "0" -> continuar = false;
                default  -> System.out.println("  Opción inválida.");
            }
        }
    }

    private void crearPeriodo(Scanner scanner) {  // Flujo para crear un nuevo período
        System.out.print("  Fecha de inicio (yyyy-MM-dd): ");
        String fecha = scanner.nextLine().trim();

        try {
            Period period = rankingService.crearPeriodo(fecha);
            System.out.println("  Período " + period.getNumero() + " creado.");
            System.out.println("  Inicio:  " + period.getFechaInicio());
            System.out.println("  Cierre:  " + period.getFechaCierre());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void cerrarTrimestre(Scanner scanner) {  // Flujo para cerrar un trimestre y calcular promedios
        listarPeriodos();
        System.out.print("  ID del período a cerrar: ");
        String id = scanner.nextLine().trim();

        try {
            rankingService.cerrarTrimestre(id);
            System.out.println("  Trimestre cerrado. Promedios calculados y guardados.");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void verRanking(Scanner scanner) {  // Flujo para mostrar el ranking de un período
        listarPeriodos();
        System.out.print("  ID del período: ");
        String id = scanner.nextLine().trim();

        try {
            List<StudentTrimesterAverage> ranking = rankingService.obtenerRanking(id);
            System.out.println("\n  — Ranking —");
            int posicion = 1;
            for (StudentTrimesterAverage avg : ranking) {  // Muestra cada posición
                System.out.printf("  %d. Estudiante %-10s  Promedio: %.2f%n",
                        posicion++, avg.getEstudianteId(), avg.getPromedio());
            }
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void listarPeriodos() {  // Muestra todos los períodos registrados
        List<Period> periodos = rankingService.listarPeriodos();
        if (periodos.isEmpty()) {
            System.out.println("  No hay períodos registrados.");
            return;
        }
        System.out.println("\n  — Períodos —");
        for (Period p : periodos) {
            String estado = p.isCerrado() ? "CERRADO" : "ABIERTO";
            System.out.printf("  [%s] Trimestre %d  %s → %s  (%s)%n",
                    p.getId(), p.getNumero(), p.getFechaInicio(), p.getFechaCierre(), estado);
        }
    }
}