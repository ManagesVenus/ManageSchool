package test;  // Define el paquete donde esta esta clase de prueba
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;  // Importa la anotacion que se ejecuta antes de cada prueba
import org.junit.jupiter.api.Test;  // Importa la anotacion que marca un metodo como prueba
import org.manageSchool.subject.Subject;  // Importa el modelo Subject para usarlo en las pruebas
import org.manageSchool.subject.SubjectRepository;  // Importa el repositorio para verificar datos
import org.manageSchool.subject.SubjectService;  // Importa el servicio que vamos a probar
import org.manageSchool.shared.util.JsonFileManager;
import static org.junit.jupiter.api.Assertions.*;  // Importa todos los metodos de verificacion

class SubjectServiceTest {  // Clase que contiene todas las pruebas de SubjectService
    private SubjectService subjectService;  // Declara el servicio que vamos a probar
    private SubjectRepository repo;  // Declara el repositorio para verificar datos en JSON

    @BeforeEach  // Esta anotacion hace que el metodo se ejecute ANTES de cada prueba
    void setUp() {  // Metodo de configuracion inicial
        subjectService = new SubjectService();  // Crea una instancia nueva del servicio
        repo = new SubjectRepository();  // Crea una instancia nueva del repositorio

        // Limpiar todos los datos existentes antes de cada prueba
        JsonFileManager.writeAll("subjects.json", new ArrayList<Subject>());

        // Cargar materias predeterminadas desde cero
        subjectService.seedDefaultSubjects();  // Crea las 5 materias base del sistema
    }

    // ============ TESTS PARA ISSUE-012 ============

    @Test  // Marca este metodo como una prueba unitaria
    void seedDefaultSubjects_creaLas5MateriasPredeterminadas() {  // Prueba que se crean exactamente 5 materias
        assertEquals(5, repo.findAll().size(), "Deben crearse exactamente 5 materias");  // Verifica que hay 5 materias
    }

    @Test  // Marca este metodo como una prueba unitaria
    void seedDefaultSubjects_noDuplicaMaterias() {  // Prueba que al ejecutarse dos veces no se duplican
        subjectService.seedDefaultSubjects();  // Ejecuta seed por segunda vez
        assertEquals(5, repo.findAll().size(), "No debe duplicar materias al ejecutarse dos veces");  // Verifica que siguen siendo 5
    }

    @Test  // Marca este metodo como una prueba unitaria
    void seedDefaultSubjects_materiasCorrectas() {  // Prueba que las materias creadas son las correctas
        assertTrue(repo.existsByNombre("Matemáticas"));  // Verifica que existe Matematicas
        assertTrue(repo.existsByNombre("Español"));  // Verifica que existe Espanol
        assertTrue(repo.existsByNombre("Ciencias Naturales"));  // Verifica que existe Ciencias Naturales
        assertTrue(repo.existsByNombre("Ciencias Sociales"));  // Verifica que existe Ciencias Sociales
        assertTrue(repo.existsByNombre("Inglés"));  // Verifica que existe Ingles
    }

    @Test  // Marca este metodo como una prueba unitaria
    void seedDefaultSubjects_materiasSonPredeterminadas() {  // Prueba que todas tienen predeterminada = true
        for (Subject s : repo.findAll()) {  // Recorre cada materia creada
            assertTrue(s.isPredeterminada(), s.getNombre() + " debe ser predeterminada");  // Verifica que sea predeterminada
        }
    }

    // ============ TESTS PARA ISSUE-013 ============

    @Test  // Marca este metodo como una prueba unitaria
    void create_creaMateriaPersonalizadaExitosamente() {  // Prueba crear materia personalizada con exito
        Subject nueva = subjectService.create("Arte");  // Llama al metodo create para crear "Arte"

        assertNotNull(nueva.getId());  // Verifica que el ID no sea nulo
        assertEquals("Arte", nueva.getNombre());  // Verifica que el nombre sea "Arte"
        assertFalse(nueva.isPredeterminada());  // Verifica que NO sea predeterminada
        assertTrue(nueva.isActiva());  // Verifica que este activa
        assertTrue(repo.existsByNombre("Arte"));  // Verifica que existe en el repositorio
    }

    @Test  // Marca este metodo como una prueba unitaria
    void create_lanzaErrorSiNombreVacio() {  // Prueba que no permite crear materia con nombre vacio
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera que lance una excepcion
            subjectService.create("");  // Intenta crear con nombre vacio
        });
        assertEquals("El nombre de la materia no puede estar vacio.", exception.getMessage());  // Verifica el mensaje de error
    }

    @Test  // Marca este metodo como una prueba unitaria
    void create_lanzaErrorSiNombreDuplicado() {  // Prueba que no permite crear materia con nombre duplicado
        subjectService.create("Musica");  // Crea una materia llamada "Musica" primero
        Exception exception = assertThrows(RuntimeException.class, () -> {  // Espera que lance una excepcion
            subjectService.create("Musica");  // Intenta crear otra materia con el mismo nombre
        });
        assertEquals("Ya existe una materia con ese nombre.", exception.getMessage());  // Verifica el mensaje de error
    }

    // ============ TESTS PARA ISSUE-014 ============

    @Test
    void listAll_devuelveTodasLasMaterias() {  // Prueba que listAll devuelve todas las materias
        assertEquals(5, subjectService.listAll().size());  // Verifica que hay 5 materias
    }

    @Test
    void update_editaMateriaPersonalizadaExitosamente() {  // Prueba que edita materia personalizada
        Subject creada = subjectService.create("Fisica");  // Crea una materia personalizada
        Subject actualizada = subjectService.update(creada.getId(), "Fisica Moderna");  // La edita

        assertEquals("Fisica Moderna", actualizada.getNombre());  // Verifica el nuevo nombre
        assertTrue(repo.existsByNombre("Fisica Moderna"));  // Verifica que existe con el nuevo nombre
        assertFalse(repo.existsByNombre("Fisica"));  // Verifica que ya no existe el viejo nombre
    }

    @Test
    void update_lanzaErrorSiEditaMateriaPredeterminada() {  // Prueba que no permite editar predeterminada
        Subject predeterminada = repo.findAll().stream()
                .filter(Subject::isPredeterminada)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay materias predeterminadas"));  // Si no hay, error

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.update(predeterminada.getId(), "Nuevo Nombre");
        });
        assertEquals("Las materias predeterminadas no pueden editarse.", exception.getMessage());
    }

    @Test
    void update_lanzaErrorSiNombreDuplicado() {  // Prueba que no permite editar con nombre duplicado
        subjectService.create("Quimica");  // Crea una materia
        subjectService.create("Biologia");  // Crea otra materia

        Subject quimica = repo.findByNombre("Quimica")
                .orElseThrow(() -> new RuntimeException("Materia Quimica no encontrada"));  // Si no existe, error

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.update(quimica.getId(), "Biologia");  // Intenta cambiar a nombre que ya existe
        });
        assertEquals("Ya existe una materia con ese nombre.", exception.getMessage());
    }

    @Test
    void delete_eliminaMateriaPersonalizadaExitosamente() {  // Prueba que elimina materia personalizada
        Subject creada = subjectService.create("Historia");  // Crea una materia
        assertEquals(6, repo.findAll().size());  // Verifica que hay 6 materias (5 + 1 nueva)

        subjectService.delete(creada.getId());  // La elimina
        assertEquals(5, repo.findAll().size());  // Verifica que vuelven a ser 5
        assertFalse(repo.existsByNombre("Historia"));  // Verifica que ya no existe
    }

    @Test
    void delete_lanzaErrorSiEliminaMateriaPredeterminada() {  // Prueba que no permite eliminar predeterminada
        Subject predeterminada = repo.findAll().stream()
                .filter(Subject::isPredeterminada)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay materias predeterminadas"));  // Si no hay, error

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.delete(predeterminada.getId());
        });
        assertEquals("Las materias predeterminadas no pueden eliminarse en v1.0.", exception.getMessage());
    }

    @Test
    void delete_lanzaErrorSiMateriaNoExiste() {  // Prueba que no permite eliminar materia inexistente
        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.delete("id-inexistente");
        });
        assertEquals("Materia no encontrada.", exception.getMessage());
    }
}