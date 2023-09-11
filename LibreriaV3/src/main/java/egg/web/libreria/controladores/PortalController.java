package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.entidades.Editorial;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.entidades.Zona;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ZonaRepositorio;
import egg.web.libreria.servicios.AutorServicio;
import egg.web.libreria.servicios.EditorialServicio;
import egg.web.libreria.servicios.LibroServicio;
import egg.web.libreria.servicios.UsuarioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lucas
 */
@Controller
@RequestMapping("/")
public class PortalController {

    @Autowired
    private ZonaRepositorio zonaRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    @GetMapping("/")
    public String index(ModelMap modelo, String idAutor, String idEditorial) {

        List<Libro> libros = libroServicio.listarLibrosActivos();
        modelo.put("libros", libros);

        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
        if (error != null) {
            model.put("error", "Email o contraseña incorrectos.");
        }
        //No funciona el mensaje resolver
        if (logout != null) {
            model.put("logout", "Ha salido correctamente de la plataforma.");
        }

        return "login.html";
    }

    @GetMapping("/registro")
    public String registro(ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);
        modelo.put("sexos", Sexo.values());

        return "registro.html";
    }

    @PostMapping("/registrar")
    public String registro(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String documento, @RequestParam String telefono, @RequestParam Sexo sexo, @RequestParam String idZona, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2) {
        try {
            usuarioServicio.registar(archivo, nombre, apellido, documento, telefono, sexo, idZona, mail, clave, clave2);
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("sexos", Sexo.values());

            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("documento", documento);
            modelo.put("telefono", telefono);
            modelo.put("idZona", idZona);
            modelo.put("mail", mail);

            return "registro.html";
        }
        modelo.put("titulo", "Bienvenido a la Libreria!");
        modelo.put("descripcion", "Tu usuario fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    @GetMapping("/autores")
    public String autores(ModelMap modelo, String idAutor) {

        List<Autor> listaActivos = autorServicio.listaAutoresActivos();
        modelo.addAttribute("autoresA", listaActivos);

        List<Autor> autores = autorServicio.listarAutores();
        modelo.addAttribute("autores", autores);

        modelo.addAttribute("autorSelected", null);

        List<Libro> libros = libroServicio.buscarLibrosPorAutor(idAutor);
        modelo.addAttribute("libros", libros);

        return "autores.html";
    }

    @GetMapping("/editoriales")
    public String editoriales(ModelMap modelo, String idEditorial) {

        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("editoriales", listaEditoriales);

        List<Editorial> listaActivos = editorialServicio.listaEditorialesActivas();
        modelo.addAttribute("editorialesA", listaActivos);

        modelo.addAttribute("editorialSelected", null);

        List<Libro> libros = libroServicio.buscarLibrosPorEditorial(idEditorial);
        modelo.addAttribute("libros", libros);

        return "editoriales.html";
    }
}
