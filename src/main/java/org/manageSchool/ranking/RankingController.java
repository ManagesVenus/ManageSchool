package org.manageSchool.ranking;

import org.manageSchool.student.StudentRepository;

import java.util.List;
import java.util.Scanner;

public class RankingController {  // Controlador de consola para el ranking trimestral

    private final RankingService    rankingService = new RankingService();
    private final StudentRepository studentRepo    = new StudentRepository();  // Para resolver nombre del estudiante

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

    private void verRanking(Scanner scanner) {  // CP-RANK-002 / CP-RANK-002b: muestra el ranking automático
        System.out.println("\n  ¿Qué ranking desea ver?");
        System.out.println("  1. Ranking actual (automático)");
        System.out.println("  2. Consultar trimestre específico");
        System.out.print("  Opción: ");
        String opcion = scanner.nextLine().trim();

        if (opcion.equals("2")) {  // Consulta de trimestre específico pasado
            verRankingEspecifico(scanner);
            return;
        }

        // CP-RANK-002 / CP-RANK-002b: ranking automático
        try {
            RankingService.ResultadoRanking resultado = rankingService.consultarRankingActual();

            // Mostrar aviso si el trimestre actual está en curso (CP-RANK-002b)
            if (resultado.aviso != null) {
                System.out.println("\n  ⚠  " + resultado.aviso);
            }

            System.out.println("\n  — Ranking Trimestre " + resultado.periodo.getNumero() + " —");
            imprimirTablaRanking(resultado.promedios);

        } catch (RuntimeException e) {
            System.out.println("\n  " + e.getMessage());  // Mensaje "Aún no hay trimestres cerrados..."
        }
    }

    private void verRankingEspecifico(Scanner scanner) {  // Consulta un trimestre pasado cerrado por ID
        listarPeriodos();
        System.out.print("  ID del período: ");
        String id = scanner.nextLine().trim();

        try {
            List<StudentTrimesterAverage> promedios = rankingService.obtenerRanking(id);
            System.out.println("\n  — Ranking —");
            imprimirTablaRanking(promedios);
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void imprimirTablaRanking(List<StudentTrimesterAverage> promedios) {  // Imprime tabla con posición, nombre y promedio
        System.out.printf("  %-10s %-20s %-10s%n", "Posición", "Estudiante", "Promedio");
        System.out.println("  " + "─".repeat(42));

        int posicion = 1;
        double promedioAnterior = -1;
        int posicionReal = 1;

        for (StudentTrimesterAverage avg : promedios) {  // Recorre cada promedio
            // Empate: mantiene la misma posición
            if (avg.getPromedio() != promedioAnterior) {
                posicionReal = posicion;
            }

            // Resolver nombre del estudiante desde StudentRepository
            String nombre = studentRepo.findById(avg.getEstudianteId())
                    .map(s -> s.getNombre())
                    .orElse("Estudiante " + avg.getEstudianteId());  // Fallback si no se encuentra

            System.out.printf("  %-10d %-20s %.2f%n", posicionReal, nombre, avg.getPromedio());

            promedioAnterior = avg.getPromedio();
            posicion++;
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