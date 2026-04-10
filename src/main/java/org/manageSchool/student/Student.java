package org.manageSchool.student;

import java.time.LocalDate;
import java.util.UUID;

public class Student {

    private String id;
    private String nombre;
    private String correo;
    private boolean activo;
    private String fechaCreacion; // formato "yyyy-MM-dd"

    // Constructor vacío obligatorio para Jackson
    public Student() {}

    // Constructor completo
    public Student(String id, String nombre, String correo, boolean activo, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    // Factory: crea un Student nuevo con ID y fecha generados automáticamente
    public static Student crear(String nombre, String correo) {
        return new Student(
                UUID.randomUUID().toString(),
                nombre,
                correo,
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

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
