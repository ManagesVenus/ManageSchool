package org.manageSchool.ranking;

public class StudentTrimesterAverage {  // Clase que almacena el promedio trimestral de un estudiante

    private String estudianteId;   // ID del estudiante
    private String periodId;       // ID del período trimestral al que pertenece
    private double promedio;       // Promedio calculado (media de promedios por materia)

    // Constructor vacío obligatorio para Jackson
    public StudentTrimesterAverage() {}

    // Constructor completo
    public StudentTrimesterAverage(String estudianteId, String periodId, double promedio) {
        this.estudianteId = estudianteId;
        this.periodId = periodId;
        this.promedio = promedio;
    }

    // Getters y Setters
    public String getEstudianteId() { return estudianteId; }
    public void setEstudianteId(String estudianteId) { this.estudianteId = estudianteId; }

    public String getPeriodId() { return periodId; }
    public void setPeriodId(String periodId) { this.periodId = periodId; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }
}