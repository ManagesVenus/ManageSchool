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

    // ===== seedDefaultAdmin =====

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

    // ===== login =====

    @Test
    @DisplayName("CP-AUTH-003: Login exitoso con credenciales válidas")
    void login_exitosoConCredencialesValidas() {
        String hash = BCrypt.hashpw("Admin2026", BCrypt.gensalt());
        User usuario = User.crear("Administrador", "admin@colegio.edu.co", hash, "ADMIN");
        when(repoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.of(usuario));

        User resultado = service.login("admin@colegio.edu.co", "Admin2026");

        assertNotNull(resultado);
        assertEquals("admin@colegio.edu.co", resultado.getCorreo());
    }

    @Test
    @DisplayName("CP-AUTH-006: Login falla con correo no registrado")
    void login_fallaConCorreoNoRegistrado() {
        when(repoMock.findByEmail("noexiste@colegio.edu.co")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> service.login("noexiste@colegio.edu.co", "cualquier"));

        assertEquals("Correo no registrado en el sistema.", ex.getMessage());
    }

    @Test
    @DisplayName("CP-AUTH-006: Login falla con contraseña incorrecta")
    void login_fallaConContrasenaIncorrecta() {
        String hash = BCrypt.hashpw("Admin2026", BCrypt.gensalt());
        User usuario = User.crear("Administrador", "admin@colegio.edu.co", hash, "ADMIN");
        when(repoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.of(usuario));

        AppException ex = assertThrows(AppException.class,
                () -> service.login("admin@colegio.edu.co", "ContraseñaWrong"));

        assertEquals("Contraseña incorrecta.", ex.getMessage());
    }

    // ===== createAccount =====

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
    @DisplayName("CP-AUTH-002: Rechaza correo no institucional")
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
}