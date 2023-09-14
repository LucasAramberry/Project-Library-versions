package egg.web.libreria.controladores;

import egg.web.libreria.entidades.*;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ZonaRepositorio;
import egg.web.libreria.servicios.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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
    @Autowired
    private PrestamoServicio prestamoServicio;

    @GetMapping("/")
    public String index(ModelMap modelo) {
        return "index.html";
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

//    @GetMapping("/libros")
//    public String libros(ModelMap modelo) {
//
//        List<Libro> librosActivos = libroServicio.listarLibrosActivos();
//        modelo.put("librosA", librosActivos);
//
//        List<Libro> libros = libroServicio.listarLibros();
//        modelo.addAttribute("libros", libros);
//
//        return "libros.html";
//    }
    @GetMapping("/libros")
    public String libros(ModelMap modelo, @RequestParam(required = false) String idAutor, @RequestParam(required = false) String idEditorial) {

        List<Libro> librosActivos = libroServicio.listarLibrosActivos();
        modelo.put("librosA", librosActivos);

        List<Libro> libros = libroServicio.listarLibros();
        modelo.addAttribute("libros", libros);

//        //autores
//        List<Autor> listaActivos = autorServicio.listaAutoresActivos();
//        modelo.addAttribute("autoresA", listaActivos);
//
//        List<Autor> autores = autorServicio.listarAutores();
//        modelo.addAttribute("autores", autores);
//
//        modelo.addAttribute("autorSelected", null);
//
//        //editoriales
//        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();
//        modelo.addAttribute("editoriales", listaEditoriales);
//
//        List<Editorial> listaEditorialesActivas = editorialServicio.listaEditorialesActivas();
//        modelo.addAttribute("editorialesA", listaEditorialesActivas);
//
//        modelo.addAttribute("editorialSelected", null);
//
//        if (idAutor != null) {
//            List<Libro> librosPorAutor = libroServicio.buscarLibrosPorAutor(idAutor);
//            modelo.addAttribute("libros", librosPorAutor);
//        } else if (idEditorial != null) {
//            List<Libro> librosPorEditorial = libroServicio.buscarLibrosPorEditorial(idEditorial);
//            modelo.addAttribute("libros", librosPorEditorial);
//        }
        return "libros.html";
    }

    @GetMapping("/autores")
    public String autores(ModelMap modelo, @RequestParam(required = false) String idAutor) {

        List<Autor> listaActivos = autorServicio.listaAutoresActivos();
        modelo.addAttribute("autoresA", listaActivos);

        List<Autor> autores = autorServicio.listarAutores();
        modelo.addAttribute("autores", autores);

        modelo.addAttribute("autorSelected", null);

        if (idAutor != null) {
            try {
                List<Libro> libros = libroServicio.buscarLibrosPorAutor(idAutor);
                modelo.addAttribute("libros", libros);
                
                Autor autor = autorServicio.buscarAutorPorId(idAutor);
                modelo.addAttribute("autores", autor);
            } catch (ErrorServicio ex) {
                
            }
        }

        return "autores.html";
    }

    @GetMapping("/editoriales")
    public String editoriales(ModelMap modelo, @RequestParam(required = false) String idEditorial) {

        List<Editorial> listaEditoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("editoriales", listaEditoriales);

        List<Editorial> listaActivos = editorialServicio.listaEditorialesActivas();
        modelo.addAttribute("editorialesA", listaActivos);

        modelo.addAttribute("editorialSelected", null);

        List<Libro> libros = libroServicio.buscarLibrosPorEditorial(idEditorial);
        modelo.addAttribute("libros", libros);

        return "editoriales.html";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/prestamos")
    public String mostrar(ModelMap model, @RequestParam(required = false) String idUsuario) {
        List<Usuario> listaActivos = usuarioServicio.findAll();
        model.addAttribute("usuarios", listaActivos);

        model.addAttribute("usuarioSelected", null);

        if (idUsuario != null) {
            List<Prestamo> prestamosUsuario = prestamoServicio.buscarPrestamosPorUsuario(idUsuario);
            model.put("prestamos", prestamosUsuario);
        } else {
            List<Prestamo> prestamos = prestamoServicio.listaPrestamos();
            model.put("prestamos", prestamos);
        }
        return "prestamos.html";
    }
}
