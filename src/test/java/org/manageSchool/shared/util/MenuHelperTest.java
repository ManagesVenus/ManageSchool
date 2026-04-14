package org.manageSchool.shared.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MenuHelperTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn  = System.in;
    private ByteArrayOutputStream outBuffer;

    @BeforeEach
    void capturarSalida() {
        outBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuffer));
    }

    @AfterEach
    void restaurarIO() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // ===== leerOpcion =====

    @Test
    @DisplayName("leerOpcion: devuelve el entero ingresado cuando la entrada es válida")
    void leerOpcion_devuelveEnteroValido() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("3\n".getBytes()));

        int resultado = MenuHelper.leerOpcion(scanner);

        assertEquals(3, resultado);
    }

    @Test
    @DisplayName("leerOpcion: reintenta cuando el usuario ingresa texto, no cierra el programa")
    void leerOpcion_reintentaCuandoNoEsNumero() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("abc\n7\n".getBytes()));

        int resultado = MenuHelper.leerOpcion(scanner);

        assertEquals(7, resultado);
        assertTrue(outBuffer.toString().contains("Opción inválida"),
                "Debe mostrar mensaje en español al recibir entrada inválida");
    }

    // ===== mostrarTabla =====

    @Test
    @DisplayName("mostrarTabla: imprime encabezados, separador y filas alineadas")
    void mostrarTabla_imprimeEstructuraBasica() {
        String[] headers = {"Nombre", "Correo"};
        List<String[]> rows = List.of(
                new String[]{"Ana", "ana@colegio.edu.co"},
                new String[]{"Pedro", "pedro@colegio.edu.co"}
        );

        MenuHelper.mostrarTabla(headers, rows);

        String salida = outBuffer.toString();
        assertTrue(salida.contains("Nombre"),  "Debe incluir encabezado Nombre");
        assertTrue(salida.contains("Correo"),  "Debe incluir encabezado Correo");
        assertTrue(salida.contains("ana@colegio.edu.co"), "Debe incluir fila Ana");
        assertTrue(salida.contains("Pedro"),   "Debe incluir fila Pedro");
        assertTrue(salida.contains("-+-"),     "Debe incluir separador entre columnas");
    }

    @Test
    @DisplayName("mostrarTabla: muestra '(sin datos)' si no hay filas")
    void mostrarTabla_indicaCuandoNoHayFilas() {
        String[] headers = {"ID", "Nombre"};

        MenuHelper.mostrarTabla(headers, List.of());

        assertTrue(outBuffer.toString().contains("(sin datos)"));
    }

    @Test
    @DisplayName("mostrarTabla: maneja valores null como cadena vacía sin lanzar excepción")
    void mostrarTabla_manejaValoresNull() {
        String[] headers = {"A", "B"};
        List<String[]> rows = List.of(
                new String[]{"x", null},
                new String[]{null, "y"}
        );

        assertDoesNotThrow(() -> MenuHelper.mostrarTabla(headers, rows));
    }

    @Test
    @DisplayName("mostrarTabla: indica tabla vacía si no hay encabezados")
    void mostrarTabla_rechazaHeadersVacios() {
        MenuHelper.mostrarTabla(new String[]{}, List.of());

        assertTrue(outBuffer.toString().contains("Tabla vacía"));
    }

    // ===== limpiarPantalla =====

    @Test
    @DisplayName("limpiarPantalla: no lanza excepción en ningún SO")
    void limpiarPantalla_noLanzaExcepcion() {
        assertDoesNotThrow(() -> MenuHelper.limpiarPantalla());
    }
}