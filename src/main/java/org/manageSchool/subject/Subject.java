package org.manageSchool.subject;

public class Subject {
    private String id;
    private String nombre;
    private boolean predeterminada;
    private boolean activa;

    // Constructor vacío (obligatorio para Jackson)
    public Subject() {}

    // Constructor completo
    public Subject(String id, String nombre, boolean predeterminada, boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.predeterminada = predeterminada;
        this.activa = activa;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isPredeterminada() { return predeterminada; }
    public void setPredeterminada(boolean predeterminada) { this.predeterminada = predeterminada; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}