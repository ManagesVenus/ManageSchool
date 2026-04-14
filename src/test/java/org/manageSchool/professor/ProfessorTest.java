package org.manageSchool.professor;

import org.junit.jupiter.api.Test;
import org.manageSchool.auth.User;

import static org.junit.jupiter.api.Assertions.*;

public class ProfessorTest {

    @Test
    void constructor_envuelveUserConRolProfesor() {
        User user = User.crear("Laura","laura@colegio.edu.co", "hash", "PROFESOR");

        Professor profesor = new Professor(user);

        assertEquals("Laura", profesor.getNombre());
        assertEquals("PROFESOR", profesor.getRol());
        assertSame(user, profesor.getUser());
    }

    @Test
    void constructor_rechazaUserNull() {
        assertThrows(IllegalArgumentException.class, () -> new Professor(null));
    }

    @Test
    void constructor_rechazaUserConRolDistintoAProfesor() {
        User estudiante = User.crear("Ana", "ana@colegio.edu.co", "hash", "ESTUDIANTE");

        assertThrows(IllegalArgumentException.class, () -> new Professor(estudiante));
    }
}
