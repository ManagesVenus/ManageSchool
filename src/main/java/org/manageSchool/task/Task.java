package org.manageSchool.task;

import java.time.LocalDate;
import java.util.UUID;


public class Task {

    private String id;
    private String titulo;
    private String descripcion;
    private String fechaLimite;
    private String materiaId;
    private String profesorId;

    public Task() {}

    /**
     * Constructor completo para crear una nueva tarea con todos sus campos.
     *
     * @param id          Identificador único (UUID)
     * @param titulo      Título de la tarea (obligatorio)
     * @param descripcion Descripción opcional
     * @param fechaLimite Fecha límite en formato "yyyy-MM-dd" (opcional, puede ser null)
     * @param materiaId   ID de la materia asociada
     * @param profesorId  ID del profesor creador
     */
    public Task(String id, String titulo, String descripcion,
                String fechaLimite, String materiaId, String profesorId) {
        this.id          = id;
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.materiaId   = materiaId;
        this.profesorId  = profesorId;
    }
    /**
     * Crea una nueva Task generando automáticamente el ID con UUID.
     * Uso típico desde TaskService:
     *   Task t = Task.create("Taller de álgebra", "Cap. 5", "2025-03-01", "m001", "u002");
     *
     * @param titulo      Título de la tarea
     * @param descripcion Descripción (puede ser null o vacío)
     * @param fechaLimite Fecha en "yyyy-MM-dd" (puede ser null)
     * @param materiaId   ID de la materia
     * @param profesorId  ID del profesor
     * @return Nueva instancia de Task con ID generado
     */
    public static Task create(String titulo, String descripcion,
                              String fechaLimite, String materiaId, String profesorId) {
        return new Task(
                UUID.randomUUID().toString(),
                titulo,
                descripcion,
                fechaLimite,
                materiaId,
                profesorId
        );
    }

    /**
     * @param date Fecha (puede ser null si el campo es opcional)
     * @return String "yyyy-MM-dd" o null si la fecha es null
     */
    public static String buildFechaLimite(LocalDate date) {
        return date != null ? date.toString() : null;
    }



    public String getId()          { return id; }
    public String getTitulo()      { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFechaLimite() { return fechaLimite; }
    public String getMateriaId()   { return materiaId; }
    public String getProfesorId()  { return profesorId; }

    public void setId(String id)                   { this.id = id; }
    public void setTitulo(String titulo)           { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaLimite(String fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setMateriaId(String materiaId)     { this.materiaId = materiaId; }
    public void setProfesorId(String profesorId)   { this.profesorId = profesorId; }


    @Override
    public String toString() {
        return "Task{" +
                "id='"          + id          + '\'' +
                ", titulo='"    + titulo      + '\'' +
                ", materiaId='" + materiaId   + '\'' +
                ", profesorId='"+ profesorId  + '\'' +
                ", fechaLimite='"+ fechaLimite + '\'' +
                '}';
    }
}