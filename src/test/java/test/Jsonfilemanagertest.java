package test;

import org.manageSchool.shared.AppException;
import org.manageSchool.shared.util.JsonFileManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class JsonFileManagerTest {


    private Path tempDir;

    private String originalUserDir;


    static class TestItem {
        private String id;
        private String nombre;
        private int valor;


        public TestItem() {}

        public TestItem(String id, String nombre, int valor) {
            this.id = id;
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getId()     { return id; }
        public String getNombre() { return nombre; }
        public int getValor()     { return valor; }

        public void setId(String id)         { this.id = id; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public void setValor(int valor)      { this.valor = valor; }
    }

    @BeforeEach
    void setUp() throws IOException {
        // Guardar el directorio de trabajo original
        originalUserDir = System.getProperty("user.dir");

        // Crear un directorio temporal único para este test
        tempDir = Files.createTempDirectory("schoolapp-test-");

        // Cambiar el directorio de trabajo para que JsonFileManager
        // cree su carpeta data/ dentro del directorio temporal
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Restaurar el directorio de trabajo original
        System.setProperty("user.dir", originalUserDir);

        // Eliminar todos los archivos del directorio temporal
        deleteDirectory(tempDir.toFile());
    }

    // TESTS: readAll


    @Test
    @DisplayName("readAll devuelve lista vacía si el archivo no existe")
    void readAll_returnsEmptyListWhenFileDoesNotExist() {

        List<TestItem> result = JsonFileManager.readAll("inexistente.json", TestItem.class);


        assertNotNull(result, "La lista no debe ser null");
        assertTrue(result.isEmpty(), "La lista debe estar vacía cuando el archivo no existe");
    }

    @Test
    @DisplayName("readAll lee correctamente una lista de objetos desde JSON")
    void readAll_readsObjectsFromJsonFile() throws IOException {
        // Preparar: crear manualmente el archivo JSON con datos de prueba
        createDataDir();
        String json = """
                [
                  { "id": "001", "nombre": "Matemáticas", "valor": 10 },
                  { "id": "002", "nombre": "Español",     "valor": 20 }
                ]
                """;
        writeFile("materias.json", json);


        List<TestItem> result = JsonFileManager.readAll("materias.json", TestItem.class);

        assertEquals(2, result.size(), "Debe leer exactamente 2 elementos");
        assertEquals("001",          result.get(0).getId());
        assertEquals("Matemáticas",  result.get(0).getNombre());
        assertEquals(10,             result.get(0).getValor());
        assertEquals("002",          result.get(1).getId());
        assertEquals("Español",      result.get(1).getNombre());
        assertEquals(20,             result.get(1).getValor());
    }

    @Test
    @DisplayName("readAll devuelve lista vacía para un JSON con array vacío []")
    void readAll_returnsEmptyListForEmptyJsonArray() throws IOException {
        createDataDir();
        writeFile("vacio.json", "[]");

        List<TestItem> result = JsonFileManager.readAll("vacio.json", TestItem.class);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Un JSON con [] debe producir una lista vacía");
    }

    @Test
    @DisplayName("readAll lanza AppException si el JSON está malformado")
    void readAll_throwsAppExceptionOnMalformedJson() throws IOException {
        createDataDir();

        writeFile("corrupto.json", "[{ \"id\": \"001\", \"nombre\": mal }]");


        assertThrows(AppException.class,
                () -> JsonFileManager.readAll("corrupto.json", TestItem.class),
                "JSON malformado debe lanzar AppException"
        );
    }


    // TESTS: writeAll


    @Test
    @DisplayName("writeAll crea el archivo y el directorio data/ si no existen")
    void writeAll_createsFileAndDirectoryIfAbsent() {
        // Verificar que el directorio data/ NO existe aún
        File dataDir = new File(tempDir.toFile(), "data");
        assertFalse(dataDir.exists(), "data/ no debe existir antes del test");

        List<TestItem> items = List.of(new TestItem("x1", "Inglés", 5));

        // writeAll debe crear data/ y el archivo automáticamente
        assertDoesNotThrow(() -> JsonFileManager.writeAll("nuevo.json", items));

        assertTrue(dataDir.exists(), "writeAll debe crear el directorio data/");
        assertTrue(new File(dataDir, "nuevo.json").exists(), "El archivo debe existir tras writeAll");
    }

    @Test
    @DisplayName("writeAll persiste los datos y readAll los recupera correctamente (ciclo completo)")
    void writeAll_and_readAll_completeCycle() {
        // Preparar datos
        List<TestItem> original = List.of(
                new TestItem("001", "Física",   100),
                new TestItem("002", "Química",  200),
                new TestItem("003", "Historia", 300)
        );


        JsonFileManager.writeAll("ciclo.json", original);


        List<TestItem> recuperados = JsonFileManager.readAll("ciclo.json", TestItem.class);


        assertEquals(3, recuperados.size(), "Deben recuperarse 3 elementos");
        assertEquals("001",     recuperados.get(0).getId());
        assertEquals("Física",  recuperados.get(0).getNombre());
        assertEquals(100,       recuperados.get(0).getValor());
        assertEquals("003",     recuperados.get(2).getId());
        assertEquals("Historia",recuperados.get(2).getNombre());
    }

    @Test
    @DisplayName("writeAll sobreescribe el contenido anterior del archivo")
    void writeAll_overwritesPreviousContent() {

        List<TestItem> primero = List.of(new TestItem("v1", "Versión 1", 1));
        JsonFileManager.writeAll("reemplazar.json", primero);


        List<TestItem> segundo = List.of(
                new TestItem("v2a", "Versión 2a", 2),
                new TestItem("v2b", "Versión 2b", 3)
        );
        JsonFileManager.writeAll("reemplazar.json", segundo);

        // Al leer, debe haber 2 elementos (los de la segunda escritura), no 3
        List<TestItem> resultado = JsonFileManager.readAll("reemplazar.json", TestItem.class);
        assertEquals(2, resultado.size(), "El archivo debe tener solo los datos de la segunda escritura");
        assertEquals("v2a", resultado.get(0).getId());
        assertEquals("v2b", resultado.get(1).getId());
    }

    @Test
    @DisplayName("writeAll persiste lista vacía como JSON array vacío []")
    void writeAll_writesEmptyListAsEmptyJsonArray() {
        JsonFileManager.writeAll("lista-vacia.json", List.of());

        List<TestItem> resultado = JsonFileManager.readAll("lista-vacia.json", TestItem.class);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "Una lista vacía debe persistirse y recuperarse como vacía");
    }

    @Test
    @DisplayName("El JSON generado contiene indentación (INDENT_OUTPUT activo)")
    void writeAll_generatesIndentedJson() throws IOException {
        List<TestItem> items = List.of(new TestItem("001", "Test", 42));
        JsonFileManager.writeAll("indentado.json", items);

        // Leer el contenido crudo del archivo
        File file = new File(tempDir.toFile(), "data/indentado.json");
        String contenido = Files.readString(file.toPath());

        // Un JSON con INDENT_OUTPUT siempre tiene saltos de línea
        assertTrue(contenido.contains("\n"),
                "El JSON debe estar indentado (con saltos de línea) para ser legible");
    }


    // HELPERS PRIVADOS DEL TEST


    private void createDataDir() throws IOException {
        Files.createDirectories(tempDir.resolve("data"));
    }


    private void writeFile(String fileName, String content) throws IOException {
        File f = tempDir.resolve("data/" + fileName).toFile();
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(content);
        }
    }

    private void deleteDirectory(File dir) throws IOException {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                deleteDirectory(child);
            }
        }
        dir.delete();
    }
}