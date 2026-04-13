package org.manageSchool.professor;

import org.manageSchool.auth.User;
import java.util.UUID;

public class Professor {

    // Atributos de la clase
    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private boolean activo;
    private String fechaCreacion;

    public Professor () {}

    // constructor de la clase
    public Professor ( String id, String nombre, String correo, String contrasena, boolean activo, String fechaCreacion ){
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = "PROFESOR";
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    // Factory method - Parecido al User.crear()
    public static Professor crear( String nombre, String correo, String contrasena ){
        return new Professor(
                UUID.randomUUID().toString(),
                nombre,
                correo,
                contrasena,
                true,
                java.time.LocalDate.now().toString()
        );
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId( String id ) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre( String nombre ) { this.nombre = nombre; }
    public String getCorreo()  { return correo; }
    public void setCorreo( String correo ) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena( String correo ) { this.contrasena = contrasena; }
    public String getRol() { return rol; }
    public void setRol( String rol ) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo( boolean activo ) { this.activo = activo; }
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion( String fechaCreacion ) { this.fechaCreacion = fechaCreacion; }
}
