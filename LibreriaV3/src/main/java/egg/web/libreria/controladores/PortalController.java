package egg.web.libreria.controladores;

import egg.web.libreria.entidades.*;
import egg.web.libreria.enumeraciones.Rol;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ZonaRepositorio;
import egg.web.libreria.servicios.*;
import java.util.List;
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
            modelo.put("sexo", sexo);
            modelo.put("idZona", idZona);
            modelo.put("mail", mail);

            return "registro.html";
        }
        modelo.put("titulo", "¡Bienvenido a la Libreria!");
        modelo.put("descripcion", "Tu usuario fue registrado de manera satisfactoria.");
        return "exito.html";
    }

    @GetMapping("/libros")
    public String libros(ModelMap modelo, @RequestParam(required = false) String idLibro, @RequestParam(required = false) String idAutor, @RequestParam(required = false) String idEditorial) {
        try {
            List<Libro> librosActivos = libroServicio.listarLibrosActivos();
            modelo.put("librosA", librosActivos);

            List<Libro> libros = libroServicio.listarLibros();
            modelo.addAttribute("libros", libros);

            if (idLibro != null) {
                Libro libroPorTitulo = libroServicio.buscarLibroPorId(idLibro);
                modelo.addAttribute("libros", libroPorTitulo);
            }
            return "libros.html";
        } catch (ErrorServicio ex) {
            return "libros.html";
        }
    }

    @GetMapping("/autores")
    public String autores(ModelMap modelo, @RequestParam(required = false) String idAutor) {

        List<Autor> listaActivos = autorServicio.listaAutoresActivos();
        modelo.addAttribute("autoresA", listaActivos);

        List<Autor> autores = autorServicio.listarAutores();
        modelo.addAttribute("autores", autores);

        if (idAutor != null) {
            try {
                List<Libro> libros = libroServicio.buscarLibrosPorAutor(idAutor);
                modelo.addAttribute("libros", libros);

                Autor autor = autorServicio.buscarAutorPorId(idAutor);
                modelo.addAttribute("autores", autor);
            } catch (ErrorServicio ex) {
                return "redirect:/autores";
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

        if (idEditorial != null) {
            try {
                List<Libro> libros = libroServicio.buscarLibrosPorEditorial(idEditorial);
                modelo.addAttribute("libros", libros);

                Editorial editorial = editorialServicio.buscarEditorialPorId(idEditorial);
                modelo.addAttribute("editoriales", editorial);
            } catch (ErrorServicio ex) {
                return "redirect:/editoriales";
            }
        }

        return "editoriales.html";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/prestamos")
    public String prestamos(ModelMap model, @RequestParam(required = false) String idUsuario) {
        List<Usuario> listaActivos = usuarioServicio.findAll();
        model.addAttribute("usuarios", listaActivos);

        model.addAttribute("usuarioSelected", null);

        if (idUsuario != null) {
            List<Prestamo> prestamosUsuario = prestamoServicio.buscarPrestamosPorUsuario(idUsuario);
            model.put("prestamos", prestamosUsuario);
            try {
                model.addAttribute("usuarioSelected", usuarioServicio.buscarUsuarioPorId(idUsuario));
            } catch (ErrorServicio ex) {
                return "prestamos.html";
            }
        } else {
            List<Prestamo> prestamos = prestamoServicio.listaPrestamos();
            model.put("prestamos", prestamos);
        }
        return "prestamos.html";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/usuarios")
    public String usuarios(ModelMap model, @RequestParam(required = false) String idUsuario) {
        model.put("rolAdmin", Rol.ADMIN);
        model.put("rolUsuario", Rol.USUARIO);

        List<Usuario> listaBuscarUsuarios = usuarioServicio.findAll();
        model.addAttribute("usuariosLista", listaBuscarUsuarios);

        List<Usuario> listaUsuarios = usuarioServicio.findAll();
        model.addAttribute("usuarios", listaUsuarios);
        
        model.addAttribute("usuarioSelected", null);

        if (idUsuario != null) {
            Usuario usuario;
            try {
                usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
                model.put("usuarios", usuario);
                model.addAttribute("usuarioSelected", usuario);
            } catch (ErrorServicio ex) {
                return "usuarios.html";
            }
        }
        return "usuarios.html";
    }
}
