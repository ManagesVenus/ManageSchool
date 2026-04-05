package org.manageSchool.task;

import java.time.LocalDate;
import java.util.UUID;

/**
 * POJO que representa una Tarea académica en el sistema SchoolApp CLI.
 *
 * Una tarea es creada por un Profesor, pertenece a una Materia,
 * y sobre ella los profesores registran Notas a cada estudiante.
 *
 * ¿Por qué String para fechaLimite y no LocalDate?
 * ------------------------------------------------
 * Jackson (la librería que lee/escribe JSON) no serializa LocalDate
 * directamente sin configuración adicional. Para mantener la implementación
 * simple en v1.0, la fecha se almacena como String "yyyy-MM-dd".
 * El método estático buildFechaLimite() ayuda a construirla desde LocalDate.
 *
 * Persistencia:
 * -------------
 * Los objetos Task se guardan en data/tasks.json como array JSON.
 * Jackson necesita el constructor vacío para deserializar (convertir
 * JSON → objeto Java). Sin él, Jackson lanza una excepción en tiempo
 * de ejecución al leer el archivo.
 *
 * Relaciones:
 *   Task → Subject  (via materiaId)
 *   Task → User     (via profesorId, rol = PROFESOR)
 *   Task → Grade[]  (una tarea tiene muchas notas, una por estudiante)
 */
public class Task {

    // -------------------------------------------------------------------------
    // Campos
    // -------------------------------------------------------------------------

    /** Identificador único. Se genera con UUID al crear la tarea. */
    private String id;

    /** Título de la tarea. Campo obligatorio — no puede ser null ni vacío. */
    private String titulo;

    /**
     * Descripción o instrucciones de la tarea.
     * Campo opcional — puede ser null o vacío.
     */
    private String descripcion;

    /**
     * Fecha límite de entrega en formato "yyyy-MM-dd".
     * Campo opcional — puede ser null si el profesor no especifica fecha.
     * Ejemplo: "2025-03-01"
     */
    private String fechaLimite;

    /**
     * ID de la materia a la que pertenece esta tarea.
     * Referencia al campo id de Subject en subjects.json.
     * No es una FK de base de datos — es una referencia lógica.
     */
    private String materiaId;

    /**
     * ID del profesor que creó esta tarea.
     * Referencia al campo id de User (con rol = PROFESOR) en users.json.
     * Se usa para:
     *  - Mostrar solo las tareas del profesor autenticado (RNF-06)
     *  - Verificar autoría antes de editar o eliminar
     */
    private String profesorId;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Constructor vacío requerido por Jackson para deserializar JSON → Task.
     * No debe usarse directamente en el código de producción.
     */
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

    // -------------------------------------------------------------------------
    // Métodos de fábrica estáticos
    // -------------------------------------------------------------------------

    /**
     * Crea una nueva Task generando automáticamente el ID con UUID.
     *
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
     * Convierte un LocalDate a String en formato "yyyy-MM-dd".
     * Utilidad para cuando el Controller recibe una fecha del usuario
     * y necesita convertirla para pasarla al Service.
     *
     * @param date Fecha (puede ser null si el campo es opcional)
     * @return String "yyyy-MM-dd" o null si la fecha es null
     */
    public static String buildFechaLimite(LocalDate date) {
        return date != null ? date.toString() : null;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters (Java 8 style — requeridos por Jackson)
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // toString — útil para debugging y logs
    // -------------------------------------------------------------------------

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