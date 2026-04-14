package org.manageSchool.professor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manageSchool.auth.AuthService;
import org.manageSchool.auth.CreateUserRequest;
import org.manageSchool.auth.User;
import org.manageSchool.shared.AppException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProfessorServiceTest {

    private ProfessorRepository repoMock;
    private AuthService authServiceMock;
    private ProfessorService service;

    @BeforeEach
    void setUp() {
        repoMock = mock(ProfessorRepository.class);
        authServiceMock = mock(AuthService.class);
        service = new ProfessorService(repoMock, authServiceMock);
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
}
