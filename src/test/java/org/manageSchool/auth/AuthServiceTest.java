package org.manageSchool.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manageSchool.shared.AppException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthRepository repoMock;
    private AuthService service;

    @BeforeEach
    void setUp() {
        repoMock = mock(AuthRepository.class);
        service = new AuthService(repoMock);
    }

    // ===== ISSUE-003: seedDefaultAdmin =====

    @Test
    @DisplayName("CP-AUTH-001: Crea admin por defecto si no existe")
    void seedDefaultAdmin_creaAdminSiNoExiste() {
        when(repoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.empty());

        service.seedDefaultAdmin();

        verify(repoMock, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("CP-AUTH-001: No duplica el admin si ya existe")
    void seedDefaultAdmin_noDuplicaSiYaExiste() {
        User adminExistente = User.crear("Administrador", "admin@colegio.edu.co", "hash", "ADMIN");
        when(repoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.of(adminExistente));

        service.seedDefaultAdmin();

        verify(repoMock, never()).save(any(User.class));
    }

    // ===== ISSUE-003: createAccount =====

    @Test
    @DisplayName("CP-AUTH-001b: Crea cuenta de profesor exitosamente")
    void createAccount_creaProfesorExitosamente() {
        when(repoMock.existsByEmail("prof@colegio.edu.co")).thenReturn(false);

        CreateUserRequest request = new CreateUserRequest(
                "Laura Gómez", "prof@colegio.edu.co", "Temp123", "PROFESOR");

        User resultado = service.createAccount(request);

        assertNotNull(resultado);
        assertEquals("PROFESOR", resultado.getRol());
        verify(repoMock, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("CP-AUTH-002: Rechaza correo no institucional al crear cuenta")
    void createAccount_rechazaCorreoNoInstitucional() {
        CreateUserRequest request = new CreateUserRequest(
                "Juan", "juan@gmail.com", "Pass123", "ESTUDIANTE");

        AppException ex = assertThrows(AppException.class,
                () -> service.createAccount(request));

        assertEquals("El correo debe pertenecer al dominio @colegio.edu.co.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-AUTH-002: Rechaza correo duplicado")
    void createAccount_rechazaCorreoDuplicado() {
        when(repoMock.existsByEmail("prof@colegio.edu.co")).thenReturn(true);

        CreateUserRequest request = new CreateUserRequest(
                "Laura", "prof@colegio.edu.co", "Temp123", "PROFESOR");

        AppException ex = assertThrows(AppException.class,
                () -> service.createAccount(request));

        assertEquals("Ya existe una cuenta con ese correo.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-AUTH-002: Rechaza nombre vacío")
    void createAccount_rechazaNombreVacio() {
        CreateUserRequest request = new CreateUserRequest(
                "", "prof@colegio.edu.co", "Temp123", "PROFESOR");

        AppException ex = assertThrows(AppException.class,
                () -> service.createAccount(request));

        assertEquals("El nombre no puede estar vacío.", ex.getMessage());
    }

    // ===== ISSUE-004: login =====

    @Test
    @DisplayName("CP-AUTH-003: Login exitoso como Administrador")
    void login_exitosoComoAdmin() {
        String hash = BCrypt.hashpw("Admin2026", BCrypt.gensalt());
        User usuario = User.crear("Administrador", "admin@colegio.edu.co", hash, "ADMIN");
        when(repoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.of(usuario));

        User resultado = service.login("admin@colegio.edu.co", "Admin2026");

        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getRol());
        assertEquals("admin@colegio.edu.co", resultado.getCorreo());
    }

    @Test
    @DisplayName("CP-AUTH-003: Login exitoso como Profesor")
    void login_exitosoComoProfesor() {
        String hash = BCrypt.hashpw("Prof123", BCrypt.gensalt());
        User usuario = User.crear("Carlos López", "prof@colegio.edu.co", hash, "PROFESOR");
        when(repoMock.findByEmail("prof@colegio.edu.co")).thenReturn(Optional.of(usuario));

        User resultado = service.login("prof@colegio.edu.co", "Prof123");

        assertNotNull(resultado);
        assertEquals("PROFESOR", resultado.getRol());
    }

    @Test
    @DisplayName("CP-AUTH-003: Login exitoso como Estudiante")
    void login_exitosoComoEstudiante() {
        String hash = BCrypt.hashpw("Est123", BCrypt.gensalt());
        User usuario = User.crear("Ana Ruiz", "est@colegio.edu.co", hash, "ESTUDIANTE");
        when(repoMock.findByEmail("est@colegio.edu.co")).thenReturn(Optional.of(usuario));

        User resultado = service.login("est@colegio.edu.co", "Est123");

        assertNotNull(resultado);
        assertEquals("ESTUDIANTE", resultado.getRol());
    }

    @Test
    @DisplayName("CP-AUTH-002b: Login rechazado con correo no institucional")
    void login_rechazaCorreoNoInstitucional() {
        AppException ex = assertThrows(AppException.class,
                () -> service.login("jdoe@gmail.com", "cualquier"));

        assertEquals("Correo o contraseña incorrectos.", ex.getMessage());
        verify(repoMock, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("CP-AUTH-006: Login falla con usuario inexistente")
    void login_fallaConCorreoNoRegistrado() {
        when(repoMock.findByEmail("fantasma@colegio.edu.co")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.login("fantasma@colegio.edu.co", "cualquier"));

        assertEquals("Correo o contraseña incorrectos.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-AUTH-006: Login falla con contraseña incorrecta")
    void login_fallaConContrasenaIncorrecta() {
        String hash = BCrypt.hashpw("Admin2026", BCrypt.gensalt());
        User usuario = User.crear("Administrador", "admin@colegio.edu.co", hash, "ADMIN");
        when(repoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.of(usuario));

        AppException ex = assertThrows(AppException.class,
                () -> service.login("admin@colegio.edu.co", "ContraseñaWrong"));

        assertEquals("Correo o contraseña incorrectos.", ex.getMessage());
    }
}