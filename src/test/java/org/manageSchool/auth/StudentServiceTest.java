package org.manageSchool.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manageSchool.shared.AppException;
import org.manageSchool.student.Student;
import org.manageSchool.student.StudentRepository;
import org.manageSchool.student.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    private StudentRepository repoMock;
    private StudentService service;

    @BeforeEach
    void setUp() {
        repoMock = mock(StudentRepository.class);
        service = new StudentService(repoMock);
    }

    // =========================================================
    // CP-STU-001 — Crear estudiante
    // =========================================================

    @Test
    @DisplayName("CP-STU-001: Creación exitosa con datos válidos")
    void create_creaEstudianteExitosamente() {
        when(repoMock.existsByCorreo("nuevo@colegio.edu.co")).thenReturn(false);
        when(repoMock.findAll()).thenReturn(new ArrayList<>());

        Student resultado = service.create("Ana García", "nuevo@colegio.edu.co");

        assertNotNull(resultado);
        assertEquals("Ana García", resultado.getNombre());
        assertEquals("nuevo@colegio.edu.co", resultado.getCorreo());
        assertTrue(resultado.isActivo());
        assertNotNull(resultado.getFechaCreacion());
        verify(repoMock, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("CP-STU-001: El ID asignado es lista.size() + 1")
    void create_asignaIdCorrectoSegunTamanioLista() {
        List<Student> existentes = List.of(
                new Student(1, "Pedro", "pedro@colegio.edu.co", true, "2025-01-01"),
                new Student(2, "Laura", "laura@colegio.edu.co", true, "2025-01-02")
        );
        when(repoMock.existsByCorreo("nuevo@colegio.edu.co")).thenReturn(false);
        when(repoMock.findAll()).thenReturn(new ArrayList<>(existentes));

        Student resultado = service.create("Nuevo", "nuevo@colegio.edu.co");

        assertEquals(3, resultado.getId());
    }

    @Test
    @DisplayName("CP-STU-001: Falla si el nombre está vacío")
    void create_rechazaNombreVacio() {
        AppException ex = assertThrows(AppException.class,
                () -> service.create("", "nuevo@colegio.edu.co"));

        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
        verify(repoMock, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("CP-STU-001: Falla si el nombre es nulo")
    void create_rechazaNombreNulo() {
        AppException ex = assertThrows(AppException.class,
                () -> service.create(null, "nuevo@colegio.edu.co"));

        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
        verify(repoMock, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("CP-STU-001: Falla si el correo está vacío")
    void create_rechazaCorreoVacio() {
        AppException ex = assertThrows(AppException.class,
                () -> service.create("Ana García", ""));

        assertEquals("El correo no puede estar vacío.", ex.getMessage());
        verify(repoMock, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("CP-STU-001: Falla si el correo no es del dominio institucional")
    void create_rechazaCorreoNoInstitucional() {
        AppException ex = assertThrows(AppException.class,
                () -> service.create("Ana García", "ana@gmail.com"));

        assertEquals("El correo debe pertenecer al dominio @colegio.edu.co.", ex.getMessage());
        verify(repoMock, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("CP-STU-001: Falla si el correo ya está registrado")
    void create_rechazaCorreoDuplicado() {
        when(repoMock.existsByCorreo("duplicado@colegio.edu.co")).thenReturn(true);

        AppException ex = assertThrows(AppException.class,
                () -> service.create("Juan", "duplicado@colegio.edu.co"));

        assertEquals("Ya existe un estudiante con ese correo.", ex.getMessage());
        verify(repoMock, never()).save(any(Student.class));
    }

    // =========================================================
    // CP-STU-002 / CP-STU-007 — Listar estudiantes
    // =========================================================

    @Test
    @DisplayName("CP-STU-002: Retorna todos los estudiantes registrados")
    void listAll_retornaListaCompleta() {
        List<Student> estudiantes = List.of(
                new Student(1, "Ana", "ana@colegio.edu.co", true, "2025-01-01"),
                new Student(2, "Luis", "luis@colegio.edu.co", true, "2025-01-02"),
                new Student(3, "María", "maria@colegio.edu.co", true, "2025-01-03")
        );
        when(repoMock.findAll()).thenReturn(estudiantes);

        List<Student> resultado = service.listAll();

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("CP-STU-002: Retorna lista vacía si no hay estudiantes")
    void listAll_retornaListaVaciaSiNoHayEstudiantes() {
        when(repoMock.findAll()).thenReturn(new ArrayList<>());

        List<Student> resultado = service.listAll();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP-STU-007: Cada estudiante de la lista tiene nombre y correo")
    void listAll_contieneNombreYCorreo() {
        when(repoMock.findAll()).thenReturn(List.of(
                new Student(1, "Ana García", "ana@colegio.edu.co", true, "2025-01-01")
        ));

        Student s = service.listAll().get(0);

        assertEquals("Ana García", s.getNombre());
        assertEquals("ana@colegio.edu.co", s.getCorreo());
    }

    // =========================================================
    // CP-STU-003 / ISSUE-008 — Editar estudiante
    // =========================================================

    @Test
    @DisplayName("CP-STU-003 / ISSUE-008: Edición exitosa de nombre")
    void update_editaNombreExitosamente() {
        Student existente = new Student(3, "Carlos", "carlos@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(3)).thenReturn(Optional.of(existente));

        service.update(3, "Carlos Alberto", "");

        verify(repoMock).update(argThat(s ->
                s.getNombre().equals("Carlos Alberto") &&
                        s.getCorreo().equals("carlos@colegio.edu.co")
        ));
    }

    @Test
    @DisplayName("CP-STU-003 / ISSUE-008: Edición exitosa de correo")
    void update_editaCorreoExitosamente() {
        Student existente = new Student(3, "Carlos", "carlos@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(3)).thenReturn(Optional.of(existente));

        service.update(3, "", "carlosn@colegio.edu.co");

        verify(repoMock).update(argThat(s ->
                s.getCorreo().equals("carlosn@colegio.edu.co")
        ));
    }

    @Test
    @DisplayName("CP-STU-003 / ISSUE-008: No modifica nada si ambos campos están vacíos")
    void update_sinCambiosSiCamposVacios() {
        Student existente = new Student(3, "Carlos", "carlos@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(3)).thenReturn(Optional.of(existente));

        service.update(3, "", "");

        verify(repoMock).update(argThat(s ->
                s.getNombre().equals("Carlos") &&
                        s.getCorreo().equals("carlos@colegio.edu.co")
        ));
    }

    @Test
    @DisplayName("CP-STU-003 / ISSUE-008: Falla si el estudiante no existe")
    void update_estudianteNoEncontrado() {
        when(repoMock.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.update(99, "Nombre", ""));

        assertEquals("Estudiante no encontrado.", ex.getMessage());
        verify(repoMock, never()).update(any(Student.class));
    }

    @Test
    @DisplayName("CP-STU-003 / ISSUE-008: Falla si el nuevo correo tiene dominio inválido")
    void update_rechazaCorreoInvalido() {
        Student existente = new Student(3, "Carlos", "carlos@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(3)).thenReturn(Optional.of(existente));

        AppException ex = assertThrows(AppException.class,
                () -> service.update(3, "", "carlos@hotmail.com"));

        assertEquals("Correo inválido.", ex.getMessage());
        verify(repoMock, never()).update(any(Student.class));
    }

    // =========================================================
    // CP-STU-004 / ISSUE-009 — Eliminar estudiante
    // =========================================================

    @Test
    @DisplayName("CP-STU-004 / ISSUE-009: Eliminación exitosa")
    void deleteById_eliminaEstudianteExitosamente() {
        Student existente = new Student(1, "María", "salida@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(1)).thenReturn(Optional.of(existente));

        service.deleteById(1);

        verify(repoMock, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("CP-STU-004 / ISSUE-009: Falla si el estudiante no existe")
    void deleteById_estudianteNoEncontrado() {
        when(repoMock.findById(99)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.deleteById(99));

        assertEquals("Estudiante no encontrado.", ex.getMessage());
        verify(repoMock, never()).deleteById(anyInt());
    }

    // =========================================================
    // CP-STU-005 / ISSUE-009 — Notas huérfanas al eliminar
    // =========================================================

    @Test
    @DisplayName("CP-STU-005 / ISSUE-009: Al eliminar no se toca ningún repositorio de notas")
    void deleteById_noInteractúaConRepoDeNotas() {
        Student existente = new Student(3, "Luis", "luis@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(3)).thenReturn(Optional.of(existente));

        service.deleteById(3);

        // Solo debe llamar findById y deleteById, nada más
        verify(repoMock).findById(3);
        verify(repoMock).deleteById(3);
        verifyNoMoreInteractions(repoMock);
    }

    // =========================================================
    // findById — usado internamente por update y deleteById
    // =========================================================

    @Test
    @DisplayName("findById: retorna el estudiante cuando el ID existe")
    void findById_retornaEstudianteSiExiste() {
        Student existente = new Student(1, "Ana", "ana@colegio.edu.co", true, "2025-01-01");
        when(repoMock.findById(1)).thenReturn(Optional.of(existente));

        Student resultado = service.findById(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Ana", resultado.getNombre());
    }

    @Test
    @DisplayName("findById: lanza AppException si el ID no existe")
    void findById_lanzaExcepcionSiNoExiste() {
        when(repoMock.findById(404)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.findById(404));

        assertEquals("Estudiante no encontrado.", ex.getMessage());
    }
}