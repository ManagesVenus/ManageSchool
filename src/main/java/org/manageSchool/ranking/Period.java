package org.manageSchool.ranking;

public class Period {

    private String id;               // Identificador único del período
    private int numero;              // Número del trimestre (1, 2, 3...)
    private String fechaInicio;      // Fecha de inicio del trimestre (yyyy-MM-dd)
    private String fechaCierre;      // Fecha de cierre del trimestre (yyyy-MM-dd)
    private boolean cerrado;         // true si el trimestre ya fue cerrado

    // Constructor vacío obligatorio para Jackson
    public Period() {}

    // Constructor completo
    public Period(String id, int numero, String fechaInicio, String fechaCierre, boolean cerrado) {
        this.id = id;
        this.numero = numero;
        this.fechaInicio = fechaInicio;
        this.fechaCierre = fechaCierre;
        this.cerrado = cerrado;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(String fechaCierre) { this.fechaCierre = fechaCierre; }

    public boolean isCerrado() { return cerrado; }
    public void setCerrado(boolean cerrado) { this.cerrado = cerrado; }
}