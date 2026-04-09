package org.manageSchool.auth;

import java.time.LocalDate;
import java.util.UUID;

public class User {

    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;          // "ADMIN" | "PROFESOR" | "ESTUDIANTE"
    private boolean activo;
    private String fechaCreacion; // formato "yyyy-MM-dd"

    // Constructor vacío obligatorio para Jackson
    public User() {}

    // Constructor completo
    public User(String id, String nombre, String correo,
                String contrasena, String rol, boolean activo, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    // Factory: crea un User nuevo con ID y fecha generados automáticamente
    public static User crear(String nombre, String correo, String contrasena, String rol) {
        return new User(
                UUID.randomUUID().toString(),
                nombre,
                correo,
                contrasena,
                rol,
                true,
                LocalDate.now().toString()
        );
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}