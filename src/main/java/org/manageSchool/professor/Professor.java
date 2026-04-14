package org.manageSchool.professor;

import org.manageSchool.auth.User;

/**
 * POJO que encapsula un {@link User} cuyo rol es PROFESOR.
 *
 * No persiste datos por separado: es una proyección de solo-lectura
 * sobre el User subyacente almacenado en users.json.
 *
 * Cumple ISSUE-027 — Nota técnica:
 * "El modelo Professor.java es un POJO que encapsula un User filtrado por rol = PROFESOR".
 */
public class Professor {

    public static final String ROL = "PROFESOR";

    private final User user;

    /**
     * Envuelve un User existente como Professor.
     * Valida que el rol sea PROFESOR para evitar proyecciones inválidas.
     */
    public Professor(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El User no puede ser null.");
        }
        if (user.getRol() == null || !user.getRol().equalsIgnoreCase(ROL)) {
            throw new IllegalArgumentException(
                    "Solo se puede envolver un User con rol PROFESOR. Rol recibido: " + user.getRol()
            );
        }
        this.user = user;
    }

    // Acceso al User subyacente (para Repository / persistencia)
    public User getUser() { return user; }

    // Getters de proyección (delegados al User encapsulado)
    public String getId()             { return user.getId(); }
    public String getNombre()         { return user.getNombre(); }
    public String getCorreo()         { return user.getCorreo(); }
    public String getRol()            { return user.getRol(); }
    public boolean isActivo()         { return user.isActivo(); }
    public String getFechaCreacion()  { return user.getFechaCreacion(); }
}