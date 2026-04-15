package org.manageSchool.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manageSchool.student.Student;
import org.manageSchool.student.StudentRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest {

    // JsonFileManager resuelve la ruta como: user.dir/data/students.json
    // Apuntamos user.dir a un directorio temporal para no tocar datos reales
    private File tempDir;
    private File dataDir;
    private StudentRepository repo;

    @BeforeEach
    void setUp() throws IOException {
        // Crear directorio temporal y subdirectorio data/
        tempDir = Files.createTempDirectory("school_test").toFile();
        dataDir = new File(tempDir, "data");
        dataDir.mkdirs();

        // Redirigir user.dir al directorio temporal
        System.setProperty("user.dir", tempDir.getAbsolutePath());

        repo = new StudentRepository();
    }

    @AfterEach
    void tearDown() {
        // Limpiar archivos temporales
        deleteDir(tempDir);
    }

    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) deleteDir(f);
        }
        dir.delete();
    }

    // Escribe directamente el JSON en el archivo para preparar el estado inicial
    private void escribirStudentsJson(String json) throws IOException {
        File studentsFile = new File(dataDir, "students.json");
        Files.writeString(studentsFile.toPath(), json);
    }

    // =========================================================
    // findAll
    // =========================================================

    @Test
    @DisplayName("findAll: retorna lista con todos los estudiantes del archivo")
    void findAll_retornaListaCompleta() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"},
                  {"id":2,"nombre":"Luis","correo":"luis@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-02"}
                ]
                """);

        List<Student> resultado = repo.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
        assertEquals("Luis", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("findAll: retorna lista vacía si el archivo no existe")
    void findAll_retornaListaVaciaSiNoExisteArchivo() {
        // No se crea el archivo, findAll debe retornar lista vacía
        List<Student> resultado = repo.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll: retorna lista vacía si el JSON contiene array vacío")
    void findAll_retornaListaVaciaSiArchivoVacio() throws IOException {
        escribirStudentsJson("[]");

        List<Student> resultado = repo.findAll();

        assertTrue(resultado.isEmpty());
    }

    // =========================================================
    // findById
    // =========================================================

    @Test
    @DisplayName("findById: retorna Optional con el estudiante si el ID existe")
    void findById_retornaEstudianteSiExiste() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"},
                  {"id":2,"nombre":"Luis","correo":"luis@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-02"}
                ]
                """);

        Optional<Student> resultado = repo.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Ana", resultado.get().getNombre());
    }

    @Test
    @DisplayName("findById: retorna Optional vacío si el ID no existe")
    void findById_retornaVacioSiNoExiste() throws IOException {
        escribirStudentsJson("[]");

        Optional<Student> resultado = repo.findById(99);

        assertTrue(resultado.isEmpty());
    }

    // =========================================================
    // findByCorreo
    // =========================================================

    @Test
    @DisplayName("findByCorreo: retorna estudiante si el correo existe")
    void findByCorreo_retornaEstudianteSiExiste() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        Optional<Student> resultado = repo.findByCorreo("ana@colegio.edu.co");

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
    }

    @Test
    @DisplayName("findByCorreo: búsqueda es case-insensitive")
    void findByCorreo_esCaseInsensitive() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        Optional<Student> resultado = repo.findByCorreo("ANA@COLEGIO.EDU.CO");

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("findByCorreo: retorna vacío si el correo no existe")
    void findByCorreo_retornaVacioSiNoExiste() throws IOException {
        escribirStudentsJson("[]");

        Optional<Student> resultado = repo.findByCorreo("nadie@colegio.edu.co");

        assertTrue(resultado.isEmpty());
    }

    // =========================================================
    // existsByCorreo
    // =========================================================

    @Test
    @DisplayName("existsByCorreo: retorna true si el correo está registrado")
    void existsByCorreo_retornaTrueSiExiste() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        assertTrue(repo.existsByCorreo("ana@colegio.edu.co"));
    }

    @Test
    @DisplayName("existsByCorreo: retorna false si el correo no está registrado")
    void existsByCorreo_retornaFalseSiNoExiste() throws IOException {
        escribirStudentsJson("[]");

        assertFalse(repo.existsByCorreo("nadie@colegio.edu.co"));
    }

    // =========================================================
    // save
    // =========================================================

    @Test
    @DisplayName("save: agrega el estudiante y persiste en el archivo")
    void save_agregaEstudianteYPersiste() throws IOException {
        escribirStudentsJson("[]");

        Student nuevo = new Student(1, "Ana", "ana@colegio.edu.co", true, "2025-01-01");
        repo.save(nuevo);

        List<Student> resultado = repo.findAll();
        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("save: agrega al final sin eliminar los existentes")
    void save_noEliminaEstudiantesExistentes() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        Student nuevo = new Student(2, "Luis", "luis@colegio.edu.co", true, "2025-01-02");
        repo.save(nuevo);

        List<Student> resultado = repo.findAll();
        assertEquals(2, resultado.size());
    }

    // =========================================================
    // update
    // =========================================================

    @Test
    @DisplayName("update: reemplaza solo el estudiante con el ID correspondiente")
    void update_reemplazaEstudianteCorrecto() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"},
                  {"id":2,"nombre":"Luis","correo":"luis@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-02"}
                ]
                """);

        Student modificado = new Student(1, "Ana García", "ana@colegio.edu.co", true, "2025-01-01");
        repo.update(modificado);

        List<Student> resultado = repo.findAll();
        assertEquals("Ana García", resultado.get(0).getNombre());
        // El otro estudiante no debe haber cambiado
        assertEquals("Luis", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("update: no cambia nada si el ID no existe en la lista")
    void update_noHaceCambiosSiIdNoExiste() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        Student fantasma = new Student(99, "Fantasma", "fantasma@colegio.edu.co", true, "2025-01-01");
        repo.update(fantasma);

        List<Student> resultado = repo.findAll();
        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
    }

    // =========================================================
    // deleteById
    // =========================================================

    @Test
    @DisplayName("deleteById: elimina únicamente el estudiante con ese ID")
    void deleteById_eliminaEstudianteCorrecto() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"},
                  {"id":2,"nombre":"Luis","correo":"luis@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-02"}
                ]
                """);

        repo.deleteById(1);

        List<Student> resultado = repo.findAll();
        assertEquals(1, resultado.size());
        assertEquals("Luis", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("deleteById: no lanza error si el ID no existe")
    void deleteById_noLanzaErrorSiIdNoExiste() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":1,"nombre":"Ana","correo":"ana@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        // No debe lanzar excepción
        assertDoesNotThrow(() -> repo.deleteById(99));

        // La lista original permanece intacta
        assertEquals(1, repo.findAll().size());
    }

    @Test
    @DisplayName("CP-STU-005 / ISSUE-009: deleteById no modifica el archivo de notas")
    void deleteById_noTocaArchivoDeNotas() throws IOException {
        escribirStudentsJson("""
                [
                  {"id":3,"nombre":"Luis","correo":"luis@colegio.edu.co","activo":true,"fechaCreacion":"2025-01-01"}
                ]
                """);

        // Crear un grades.json ficticio para verificar que no se toca
        File gradesFile = new File(dataDir, "grades.json");
        String contenidoOriginal = "[{\"id\":1,\"estudianteId\":3,\"valor\":4.5}]";
        Files.writeString(gradesFile.toPath(), contenidoOriginal);

        repo.deleteById(3);

        // El archivo de notas debe permanecer exactamente igual
        String contenidoDespues = Files.readString(gradesFile.toPath());
        assertEquals(contenidoOriginal, contenidoDespues);
    }
}