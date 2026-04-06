package org.manageSchool.grade;  // Define el paquete donde esta esta clase

public class Grade {  // Clase que representa una nota
    private String id;  // Identificador unico de la nota
    private String estudianteId;  // ID del estudiante que recibe la nota
    private String tareaId;  // ID de la tarea a la que pertenece la nota
    private String materiaId;  // ID de la materia de la tarea
    private double valor;  // Valor de la nota (0.0 a 5.0)
    private String fechaRegistro;  // Fecha en que se registro la nota (yyyy-MM-dd)
    private String profesorId;  // ID del profesor que registro la nota

    // Constructor vacio (obligatorio para Jackson)
    public Grade() {}

    // Constructor completo
    public Grade(String id, String estudianteId, String tareaId, String materiaId,
                 double valor, String fechaRegistro, String profesorId) {
        this.id = id;  // Asigna el ID
        this.estudianteId = estudianteId;  // Asigna el ID del estudiante
        this.tareaId = tareaId;  // Asigna el ID de la tarea
        this.materiaId = materiaId;  // Asigna el ID de la materia
        this.valor = valor;  // Asigna el valor de la nota
        this.fechaRegistro = fechaRegistro;  // Asigna la fecha de registro
        this.profesorId = profesorId;  // Asigna el ID del profesor
    }

    // Getters y Setters
    public String getId() { return id; }  // Retorna el ID
    public void setId(String id) { this.id = id; }  // Asigna el ID

    public String getEstudianteId() { return estudianteId; }  // Retorna el ID del estudiante
    public void setEstudianteId(String estudianteId) { this.estudianteId = estudianteId; }  // Asigna ID del estudiante

    public String getTareaId() { return tareaId; }  // Retorna el ID de la tarea
    public void setTareaId(String tareaId) { this.tareaId = tareaId; }  // Asigna ID de la tarea

    public String getMateriaId() { return materiaId; }  // Retorna el ID de la materia
    public void setMateriaId(String materiaId) { this.materiaId = materiaId; }  // Asigna ID de la materia

    public double getValor() { return valor; }  // Retorna el valor de la nota
    public void setValor(double valor) { this.valor = valor; }  // Asigna el valor de la nota

    public String getFechaRegistro() { return fechaRegistro; }  // Retorna la fecha de registro
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }  // Asigna la fecha

    public String getProfesorId() { return profesorId; }  // Retorna el ID del profesor
    public void setProfesorId(String profesorId) { this.profesorId = profesorId; }  // Asigna ID del profesor
}