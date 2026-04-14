package org.manageSchool.professor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manageSchool.auth.AuthRepository;
import org.manageSchool.auth.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfessorRepositoryTest {

    private AuthRepository authRepoMock;
    private ProfessorRepository repo;

    @BeforeEach
    void setUp() {
        authRepoMock = mock(AuthRepository.class);
        repo = new ProfessorRepository(authRepoMock);
    }

    @Test
    @DisplayName("CP-PROF-002: findAll filtra únicamente usuarios con rol = PROFESOR")
    void findAll_filtraSoloProfesores() {
        User profesor1 = User.crear("Laura", "laura@colegio.edu.co", "h", "PROFESOR");
        User profesor2 = User.crear("Carlos", "carlos@colegio.edu.co", "h", "PROFESOR");
        when(authRepoMock.findByRole("PROFESOR")).thenReturn(List.of(profesor1, profesor2));

        List<Professor> resultado = repo.findAll();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> "PROFESOR".equals(p.getRol())));
        verify(authRepoMock, times(1)).findByRole("PROFESOR");
    }

    @Test
    @DisplayName("CP-PROF-002: findAll devuelve lista vacía si no hay profesores")
    void findAll_devuelveListaVaciaSiNoHayProfesores() {
        when(authRepoMock.findByRole("PROFESOR")).thenReturn(List.of());

        List<Professor> resultado = repo.findAll();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findByCorreo devuelve profesor si existe y tiene rol PROFESOR")
    void findByCorreo_devuelveProfesorSiExiste() {
        User profesor = User.crear("Laura", "laura@colegio.edu.co", "h", "PROFESOR");
        when(authRepoMock.findByEmail("laura@colegio.edu.co")).thenReturn(Optional.of(profesor));

        Optional<Professor> resultado = repo.findByCorreo("laura@colegio.edu.co");

        assertTrue(resultado.isPresent());
        assertEquals("Laura", resultado.get().getNombre());
    }

    @Test
    @DisplayName("findByCorreo devuelve vacío si el usuario existe pero no es profesor")
    void findByCorreo_devuelveVacioSiNoEsProfesor() {
        User admin = User.crear("Admin", "admin@colegio.edu.co", "h", "ADMIN");
        when(authRepoMock.findByEmail("admin@colegio.edu.co")).thenReturn(Optional.of(admin));

        Optional<Professor> resultado = repo.findByCorreo("admin@colegio.edu.co");

        assertTrue(resultado.isEmpty());
    }
}