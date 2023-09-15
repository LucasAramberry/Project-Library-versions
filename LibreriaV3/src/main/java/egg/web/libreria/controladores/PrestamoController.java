package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.entidades.Prestamo;
import egg.web.libreria.enumeraciones.Rol;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.UsuarioRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import egg.web.libreria.servicios.UsuarioServicio;
import egg.web.libreria.servicios.LibroServicio;
import egg.web.libreria.servicios.PrestamoServicio;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Lucas
 */
@PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoRepositorio prestamoRepositorio;
    @Autowired
    private PrestamoServicio prestamoServicio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO')")
    @GetMapping("/mis-prestamos")
    public String misPrestamos(HttpSession session, ModelMap model) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/";
        }
        List<Prestamo> misPrestamos = prestamoRepositorio.buscarPrestamoPorUsuario(login.getId());
        model.put("prestamos", misPrestamos);

        return "prestamos.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @GetMapping("/registro")
    public String registroPrestamo(HttpSession session, ModelMap model) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";
        }

        //Verificamos si es un administrador para pasarle la lista de usuarios
        if (login.getRol().equals(Rol.ADMIN)) {
            List<Usuario> usuarios = usuarioServicio.findAll();
            model.put("usuarios", usuarios);
        }
        
        model.addAttribute("fechaPrestamo", LocalDate.now());
        List<Libro> libros = libroRepositorio.findAll();
        model.put("libros", libros);

        return "registro-prestamo.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @PostMapping("/registrar")
    public String registrarPrestamo(ModelMap modelo, HttpSession session, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam(required = false) String idUsuario) {
        //Usamos el usuario logueado para obtener el id de la sesion del usuario y
        //no mandarlo a travez del input hidden del html
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/";
        }
        try {
            //verificamos si es usuario y sacamos el id de la sesion y si es admin mandamos lista de usuarios
            if (login.getRol().equals(Rol.USUARIO)) {
                Libro libro = libroServicio.buscarLibroPorId(idLibro);
                //Si buscamos el usuario asi tenemos q habilitar el input hidden del html y 
                //colocar el atributo como requerido y comentar el login ya q no haria falta
//            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
                Usuario usuario = usuarioServicio.buscarUsuarioPorId(login.getId());
                prestamoServicio.registrar(fechaPrestamo, fechaDevolucion, libro, usuario);
                return "redirect:/prestamos/mis-prestamos"; //cambiar return
            } else {
                Libro libro = libroServicio.buscarLibroPorId(idLibro);
                Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
                prestamoServicio.registrar(fechaPrestamo, fechaDevolucion, libro, usuario);
                return "redirect:/prestamos";
            }
        } catch (ErrorServicio e) {
            return "redirect:/";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar")
    public String modificarPrestamo(ModelMap modelo, @RequestParam Integer id) {

        List<Libro> libros = libroRepositorio.findAll();
        modelo.put("libros", libros);

        List<Usuario> usuarios = usuarioRepositorio.findAll();
        modelo.put("usuarios", usuarios);

        try {
            Prestamo prestamo = prestamoServicio.buscarPrestamoPorId(id);
            modelo.addAttribute("prestamo", prestamo);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "modificar-prestamo.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/actualizar")
    public String modificarPrestamo(ModelMap modelo, @RequestParam Integer id, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam String idUsuario) {
        Prestamo prestamo = null;
        try {
            prestamo = prestamoServicio.buscarPrestamoPorId(id);
            Libro libro = libroServicio.buscarLibroPorId(idLibro);
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
            prestamoServicio.modificar(id, fechaPrestamo, fechaDevolucion, libro, usuario);
            return "redirect:/prestamos";
        } catch (ErrorServicio ex) {
            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);

            List<Usuario> usuarios = usuarioRepositorio.findAll();
            modelo.put("usuarios", usuarios);

            modelo.put("prestamo", prestamo);

            modelo.put("error", ex.getMessage());

            return "modificar-prestamo.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja")
    public String baja(ModelMap modelo, @RequestParam Integer id) {
        try {
            prestamoServicio.deshabilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta")
    public String alta(ModelMap modelo, @RequestParam Integer id) {
        try {
            prestamoServicio.habilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar")
    public String eliminar(ModelMap modelo, @RequestParam Integer id) {
        try {
            prestamoServicio.eliminar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/prestamos";
    }
}
