package org.manageSchool.professor;


import org.manageSchool.task.TaskRepository;
import org.manageSchool.task.Task;
import org.manageSchool.grade.GradeRepository;
import org.manageSchool.grade.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manageSchool.auth.AuthRepository;
import org.manageSchool.auth.AuthService;
import org.manageSchool.auth.CreateUserRequest;
import org.manageSchool.auth.User;
import org.manageSchool.shared.AppException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProfessorServiceTest {

    private AuthRepository authRepoMock;
    private ProfessorRepository repoMock;
    private AuthService authServiceMock;
    private ProfessorService service;
    private TaskRepository taskRepoMock;
    private GradeRepository gradeRepoMock;

    @BeforeEach
    void setUp() {
        repoMock = mock(ProfessorRepository.class);
        authServiceMock = mock(AuthService.class);
        authRepoMock = mock(AuthRepository.class);
        taskRepoMock = mock(TaskRepository.class);
        gradeRepoMock = mock(GradeRepository.class);
        when(repoMock.getAuthRepository()).thenReturn(authRepoMock);
        service = new ProfessorService(repoMock, authServiceMock, taskRepoMock, gradeRepoMock);
    }

    // ISSUE-027 / CP-PROF-001: crear profesor

    @Test
    @DisplayName("CP-PROF-001: crea profesor exitosamente y delega en AuthService")
    void create_creaProfesorExitosamente() {
        User userCreado = User.crear(
          "Laura Gomez", "profe@colegio.edu.co", "hash-bcrypt", Professor.ROL);
        when(authServiceMock.createAccount(any(CreateUserRequest.class)))
                .thenReturn(userCreado);

        Professor resultado = service.create(
                "Laura Gomez", "profe@colegio.edu.co", "Temp2025");

        assertNotNull(resultado);
        assertEquals("Laura Gomez", resultado.getNombre());
        assertEquals("profe@colegio.edu.co", resultado.getCorreo());
        assertEquals(Professor.ROL, resultado.getRol());
        verify(authServiceMock, times(1)).createAccount(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("CP-PROF-001: Forzar siempre rol PROFESOR al delegar en AuthService")
    void create_siempreEnviaRolProfesor() {
        User userCreado = User.crear(
                "Carlos", "carlos@colegio.edu.co","hash", Professor.ROL);
        when(authServiceMock.createAccount(any(CreateUserRequest.class)))
                .thenReturn(userCreado);

        service.create("Carlos", "carlos@colegio.edu.co", "Temp2025");

        // capturamos el request enviado a AuthService y verificamos el rol
        org.mockito.ArgumentCaptor<CreateUserRequest> captor =
                org.mockito.ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(authServiceMock).createAccount(captor.capture());
        assertEquals(Professor.ROL, captor.getValue().rol());
    }

    @Test
    @DisplayName("CP-PROF-001: Propaga AppException si AuthService rechaza correo no institucional")
    void create_propagaErrorCorreoNoInstitucional() {
        when(authServiceMock.createAccount(any(CreateUserRequest.class)))
                .thenThrow(new AppException(
                        "El correo debe pertenecer al dominio @colegio.edu.co"));

        AppException ex = assertThrows(AppException.class,
                () -> service.create("Laura", "laura@gmail.com", "Temp2025"));

        assertEquals("El correo debe pertenecer al dominio @colegio.edu.co", ex.getMessage());
    }

    @Test
    @DisplayName("CP-PROF-001: Proponga AppExecption si el correo ya esta registrado")
    void create_propongaErrorCorreoDuplicado() {
        when(authServiceMock.createAccount(any(CreateUserRequest.class)))
                .thenThrow(new AppException("Ya existe una cuenta con ese correo"));

        AppException ex = assertThrows(AppException.class,
                () -> service.create("Laura", "duplicado@colegio.edu.co", "Tempo2025"));

        assertEquals("Ya existe una cuenta con ese correo", ex.getMessage());
    }

    @Test
    @DisplayName("CP-PROF-001: Propaga AppException si el nombre está vacío")
    void create_propagaErrorNombreVacio() {
        when(authServiceMock.createAccount(any(CreateUserRequest.class)))
                .thenThrow(new AppException("El nombre no puede estar vacío."));

        AppException ex = assertThrows(AppException.class,
                () -> service.create("", "profe@colegio.edu.co", "Temp2025"));

        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
    }

    // ===== ISSUE-028 / CP-PROF-002: Listar profesores =====

    @Test
    @DisplayName("CP-PROF-002: Lista con profesores registrados devuelve todos ordenados por nombre")
    void listAllSorted_devuelveProfesoresOrdenadosPorNombre() {
        User u1 = User.crear("Zulema Ríos", "zulema@colegio.edu.co", "h", Professor.ROL);
        User u2 = User.crear("Ana López", "ana@colegio.edu.co", "h", Professor.ROL);
        User u3 = User.crear("Marta Díaz", "marta@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findAll()).thenReturn(List.of(
                new Professor(u1), new Professor(u2), new Professor(u3)));

        List<Professor> resultado = service.listAllSorted();

        assertEquals(3, resultado.size());
        assertEquals("Ana López",   resultado.get(0).getNombre());
        assertEquals("Marta Díaz",  resultado.get(1).getNombre());
        assertEquals("Zulema Ríos", resultado.get(2).getNombre());
    }

    @Test
    @DisplayName("CP-PROF-002: Lista vacía de profesores devuelve colección vacía")
    void listAllSorted_devuelveListaVaciaSiNoHayProfesores() {
        when(repoMock.findAll()).thenReturn(List.of());

        List<Professor> resultado = service.listAllSorted();

        assertTrue(resultado.isEmpty());
        verify(repoMock, times(1)).findAll();
    }

    @Test
    @DisplayName("CP-PROF-002: Ordenamiento es case-insensitive")
    void listAllSorted_ordenaIgnorandoMayusculas() {
        User u1 = User.crear("ZULEMA", "zulema@colegio.edu.co", "h", Professor.ROL);
        User u2 = User.crear("ana", "ana@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findAll()).thenReturn(List.of(new Professor(u1), new Professor(u2)));

        List<Professor> resultado = service.listAllSorted();

        assertEquals("ana", resultado.get(0).getNombre());
        assertEquals("ZULEMA", resultado.get(1).getNombre());
    }

    // ===== ISSUE-029 / CP-PROF-003: Editar profesor =====

    @Test
    @DisplayName("CP-PROF-003: Edita nombre exitosamente")
    void update_editaNombreExitosamente() {
        User user = User.crear("Pedro Ruiz", "pedro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId())).thenReturn(java.util.Optional.of(new Professor(user)));

        Professor actualizado = service.update(user.getId(), "Pedro Ruiz Montoya", "");

        assertEquals("Pedro Ruiz Montoya", actualizado.getNombre());
        assertEquals("pedro@colegio.edu.co", actualizado.getCorreo()); // correo no cambia
        verify(authRepoMock, times(1)).update(user);
    }

    @Test
    @DisplayName("CP-PROF-003: Edita correo exitosamente cuando no está en uso")
    void update_editaCorreoExitosamente() {
        User user = User.crear("Pedro", "pedro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId())).thenReturn(java.util.Optional.of(new Professor(user)));
        when(authRepoMock.findByEmail("pedro.nuevo@colegio.edu.co"))
                .thenReturn(java.util.Optional.empty());

        Professor actualizado = service.update(user.getId(), "", "pedro.nuevo@colegio.edu.co");

        assertEquals("pedro.nuevo@colegio.edu.co", actualizado.getCorreo());
        verify(authRepoMock, times(1)).update(user);
    }

    @Test
    @DisplayName("CP-PROF-003: Rechaza cambio de correo a uno ya registrado por otro usuario")
    void update_rechazaCorreoDuplicadoDeOtroUsuario() {
        User profesor = User.crear("Pedro", "pedro@colegio.edu.co", "h", Professor.ROL);
        User otroUsuario = User.crear("Otro", "otro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(profesor.getId()))
                .thenReturn(java.util.Optional.of(new Professor(profesor)));
        when(authRepoMock.findByEmail("otro@colegio.edu.co"))
                .thenReturn(java.util.Optional.of(otroUsuario));

        AppException ex = assertThrows(AppException.class,
                () -> service.update(profesor.getId(), "", "otro@colegio.edu.co"));

        assertEquals("El correo ya está en uso por otro usuario.", ex.getMessage());
        verify(authRepoMock, never()).update(any(User.class));
    }

    @Test
    @DisplayName("CP-PROF-003: Permite mantener el mismo correo (no es duplicado consigo mismo)")
    void update_permiteMismoCorreo() {
        User user = User.crear("Pedro", "pedro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId())).thenReturn(java.util.Optional.of(new Professor(user)));

        assertDoesNotThrow(() ->
                service.update(user.getId(), "Pedro Nuevo", "pedro@colegio.edu.co"));
    }

    @Test
    @DisplayName("CP-PROF-003: Rechaza correo no institucional")
    void update_rechazaCorreoNoInstitucional() {
        User user = User.crear("Pedro", "pedro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId())).thenReturn(java.util.Optional.of(new Professor(user)));

        AppException ex = assertThrows(AppException.class,
                () -> service.update(user.getId(), "", "pedro@gmail.com"));

        assertEquals("El correo debe pertenecer al dominio @colegio.edu.co.", ex.getMessage());
        verify(authRepoMock, never()).update(any(User.class));
    }

    @Test
    @DisplayName("CP-PROF-003: Lanza error si el profesor no existe")
    void update_lanzaErrorSiProfesorNoExiste() {
        when(repoMock.findById("no-existe")).thenReturn(java.util.Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.update("no-existe", "Nuevo", ""));

        assertEquals("Profesor no encontrado.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-PROF-003: Si ambos campos están vacíos, no cambia nada pero persiste")
    void update_camposVaciosConservanValoresActuales() {
        User user = User.crear("Pedro", "pedro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId())).thenReturn(java.util.Optional.of(new Professor(user)));

        Professor actualizado = service.update(user.getId(), "", "");

        assertEquals("Pedro", actualizado.getNombre());
        assertEquals("pedro@colegio.edu.co", actualizado.getCorreo());
        verify(authRepoMock, times(1)).update(user);
    }

    // ===== ISSUE-030 / CP-PROF-004: Eliminar profesor =====

    @Test
    @DisplayName("CP-PROF-004: getDeletionInfo retorna conteo de tareas y notas")
    void getDeletionInfo_retornaConteoCorrectoDeTareasYNotas() {
        User user = User.crear("Laura", "laura@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId()))
                .thenReturn(java.util.Optional.of(new Professor(user)));

        Task t1 = Task.create("Tarea 1", "desc", null, "m1", user.getId());
        Task t2 = Task.create("Tarea 2", "desc", null, "m1", user.getId());
        when(taskRepoMock.findByProfessorId(user.getId())).thenReturn(List.of(t1, t2));

        Grade g1 = new Grade("g1", "est1", t1.getId(), "m1", 4.0, "2026-04-01", user.getId());
        Grade g2 = new Grade("g2", "est2", t1.getId(), "m1", 3.5, "2026-04-01", user.getId());
        Grade g3 = new Grade("g3", "est1", t2.getId(), "m1", 5.0, "2026-04-01", user.getId());
        when(gradeRepoMock.findByTaskId(t1.getId())).thenReturn(List.of(g1, g2));
        when(gradeRepoMock.findByTaskId(t2.getId())).thenReturn(List.of(g3));

        int[] info = service.getDeletionInfo(user.getId());

        assertEquals(2, info[0], "Debe reportar 2 tareas");
        assertEquals(3, info[1], "Debe reportar 3 notas");
    }

    @Test
    @DisplayName("CP-PROF-004: getDeletionInfo retorna [0,0] si no tiene tareas")
    void getDeletionInfo_retornaCerosSiNoHayTareas() {
        User user = User.crear("Pedro", "pedro@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId()))
                .thenReturn(java.util.Optional.of(new Professor(user)));
        when(taskRepoMock.findByProfessorId(user.getId())).thenReturn(List.of());

        int[] info = service.getDeletionInfo(user.getId());

        assertEquals(0, info[0]);
        assertEquals(0, info[1]);
    }

    @Test
    @DisplayName("CP-PROF-004: delete elimina al profesor de users.json")
    void delete_eliminaProfesorDelSistema() {
        User user = User.crear("Laura", "laura@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId()))
                .thenReturn(java.util.Optional.of(new Professor(user)));

        service.delete(user.getId());

        verify(authRepoMock, times(1)).deleteById(user.getId());
    }

    @Test
    @DisplayName("CP-PROF-004: delete lanza error si el profesor no existe")
    void delete_lanzaErrorSiProfesorNoExiste() {
        when(repoMock.findById("no-existe")).thenReturn(java.util.Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.delete("no-existe"));

        assertEquals("Profesor no encontrado.", ex.getMessage());
    }

    // ===== ISSUE-030 / CP-PROF-005: Tareas y notas huérfanas =====

    @Test
    @DisplayName("CP-PROF-005: delete NO elimina tareas del profesor (quedan huérfanas)")
    void delete_noEliminaTareasDelProfesor() {
        User user = User.crear("Laura", "laura@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId()))
                .thenReturn(java.util.Optional.of(new Professor(user)));

        service.delete(user.getId());

        // Verificar que NUNCA se llamó a eliminar tareas
        verify(taskRepoMock, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("CP-PROF-005: delete NO elimina notas asociadas a tareas del profesor")
    void delete_noEliminaNotasDelProfesor() {
        User user = User.crear("Laura", "laura@colegio.edu.co", "h", Professor.ROL);
        when(repoMock.findById(user.getId()))
                .thenReturn(java.util.Optional.of(new Professor(user)));

        service.delete(user.getId());

        // Verificar que NUNCA se llamó a eliminar notas
        verify(gradeRepoMock, never()).deleteById(anyString());
    }
}
