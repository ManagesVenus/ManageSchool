package test;

import org.junit.jupiter.api.BeforeEach;  // Se ejecuta antes de cada prueba
import org.junit.jupiter.api.Test;  // Marca un metodo como prueba unitaria
import org.manageSchool.subject.Subject;  // Importa el modelo Subject
import org.manageSchool.subject.SubjectRepository;  // Importa el repositorio para verificar datos
import org.manageSchool.subject.SubjectService;  // Importa el servicio que vamos a probar

import static org.junit.jupiter.api.Assertions.*;  // Importa las verificaciones (assertEquals, assertTrue, etc.)

class SubjectServiceTest {  // Clase que contiene todas las pruebas de SubjectService
    private SubjectService subjectService;  // Declara el servicio que vamos a probar

    @BeforeEach  // Esto se ejecuta ANTES de cada prueba (se repite para cada @Test)
    void setUp() {  // Metodo de configuración inicial
        subjectService = new SubjectService();  // Crea una instancia nueva del servicio para cada prueba
    }

    @Test  // Prueba 1: Verifica que se crean exactamente 5 materias
    void seedDefaultSubjects_creaLas5MateriasPredeterminadas() {  // Nombre descriptivo del test
        subjectService.seedDefaultSubjects();  // Ejecuta el metodo que queremos probar

        SubjectRepository repo = new SubjectRepository();  // Crea repositorio para leer el archivo
        assertEquals(5, repo.findAll().size(), "Deben crearse exactamente 5 materias");  // Verifica que hay 5 materias
    }

    @Test  // Prueba 2: Verifica que al ejecutarse dos veces no se duplican
    void seedDefaultSubjects_noDuplicaMaterias() {  // Nombre descriptivo del test
        subjectService.seedDefaultSubjects();  // Primera ejecución: crea las 5 materias
        subjectService.seedDefaultSubjects();  // Segunda ejecución: no debería crear duplicados

        SubjectRepository repo = new SubjectRepository();  // Crea repositorio para leer el archivo
        assertEquals(5, repo.findAll().size(), "No debe duplicar materias al ejecutarse dos veces");  // Sigue habiendo 5
    }

    @Test  // Prueba 3: Verifica que las materias creadas son las correctas
    void seedDefaultSubjects_materiasCorrectas() {  // Nombre descriptivo del test
        subjectService.seedDefaultSubjects();  // Ejecuta el metodo que queremos probar

        SubjectRepository repo = new SubjectRepository();  // Crea repositorio para leer el archivo
        assertTrue(repo.existsByNombre("Matemáticas"));  // Verifica que existe Matemáticas
        assertTrue(repo.existsByNombre("Español"));  // Verifica que existe Español
        assertTrue(repo.existsByNombre("Ciencias Naturales"));  // Verifica que existe Ciencias Naturales
        assertTrue(repo.existsByNombre("Ciencias Sociales"));  // Verifica que existe Ciencias Sociales
        assertTrue(repo.existsByNombre("Inglés"));  // Verifica que existe Inglés
    }

    @Test  // Prueba 4: Verifica que todas las materias tienen predeterminada = true
    void seedDefaultSubjects_materiasSonPredeterminadas() {  // Nombre descriptivo del test
        subjectService.seedDefaultSubjects();  // Ejecuta el metodo que queremos probar

        SubjectRepository repo = new SubjectRepository();  // Crea repositorio para leer el archivo
        for (Subject s : repo.findAll()) {  // Recorre cada materia creada
            assertTrue(s.isPredeterminada(), s.getNombre() + " debe ser predeterminada");  // Verifica que sea predeterminada
        }
    }
}