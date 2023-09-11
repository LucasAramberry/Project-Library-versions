package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.entidades.Prestamo;
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
@PreAuthorize("hasAnyRole('ROLE_USUARIO')")
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

    @GetMapping("/mostrar")
    public String mostrar(ModelMap model) {

        List<Prestamo> prestamos = prestamoRepositorio.findAll();
        model.put("prestamos", prestamos);

        return "prestamos.html";
    }

    @GetMapping("/mis-prestamos")
    public String misPrestamos(HttpSession session, ModelMap model) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";
        }
        List<Prestamo> misPrestamos = prestamoRepositorio.buscarPrestamoPorUsuario(login.getId());
        model.put("prestamos", misPrestamos);

        return "prestamos.html";
    }

    @GetMapping("/registro-prestamo")
    public String editarPerfil(HttpSession session, ModelMap model) {

//        Usuario login = (Usuario) session.getAttribute("usuariosession");
//        if (login == null) {
//            return "redirect:/inicio";
//        }
        model.addAttribute("fechaPrestamo", LocalDate.now());
        List<Libro> libros = libroRepositorio.findAll();
        model.put("libros", libros);

        return "add-prestamo.html";
    }

    @PostMapping("/registrar-prestamo")
    public String actualizar(ModelMap modelo, HttpSession session, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam(required = false) String idUsuario) {
        //Usamos el usuario logueado para obtener el id de la sesion del usuario y
        //no mandarlo a travez del input hidden del html
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";
        }
        try {
            Libro libro = libroServicio.buscarLibroPorId(idLibro);
            //Si buscamos el usuario asi tenemos q habilitar el input hidden del html y 
            //colocar el atributo como requerido y comentar el login ya q no haria falta
//            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(login.getId());
            prestamoServicio.registrar(fechaPrestamo, fechaDevolucion, libro, usuario);
            return "redirect:/inicio"; //cambiar return
        } catch (ErrorServicio e) {

            return "inicio.html";
        }
    }

//    @GetMapping("/registro-prestamo")
//    public String registroPrestamo(HttpSession session, ModelMap model) {
//        //colocamos la fecha actual para al crear el prestamo nos setee la fecha del dia en q se hizo
//        model.addAttribute("fechaPrestamo", LocalDate.now());
//
//        List<Libro> libros = libroRepositorio.findAll();
//        model.put("libros", libros);
//
//        return "add-prestamo.html";
//    }
//
//    @PostMapping("/registrar-prestamo")
//    public String registrarPrestamo(ModelMap modelo, HttpSession session, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam String idUsuario) {
//        try {
//            Libro libro = libroServicio.buscarLibroPorId(idLibro);
//            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
//            prestamoServicio.registrar(fechaPrestamo, fechaDevolucion, libro, usuario);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//            List<Libro> libros = libroRepositorio.findAll();
//            modelo.put("libros", libros);
//            modelo.put("fechaPrestamo", fechaPrestamo);
//            modelo.put("fechaDevolucion", fechaDevolucion);
//            modelo.put("idLibro", idLibro);
//            modelo.put("idUsuario", idUsuario);
//
//            return "add-prestamo.html";
//        }
//        modelo.put("titulo", "Registro exitoso!");
//        modelo.put("descripcion", "El prestamo ingresado fue registrado correctamente.");
//        return "exito.html";
//    }
    @GetMapping("/modificar-prestamo")
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

    @PostMapping("/actualizar-prestamo")
    public String modificarPrestamo(ModelMap modelo, @RequestParam Integer id, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam String idUsuario) {
        Prestamo prestamo = null;
        try {
            prestamo = prestamoServicio.buscarPrestamoPorId(id);
            Libro libro = libroServicio.buscarLibroPorId(idLibro);
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
            prestamoServicio.modificar(id, fechaPrestamo, fechaDevolucion, libro, usuario);
            return "redirect:/prestamos/mostrar";
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

    @GetMapping("/baja")
    public String baja(ModelMap modelo, @RequestParam Integer id) {
        try {
            prestamoServicio.deshabilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/prestamos/mostrar";
    }

    @GetMapping("/alta")
    public String alta(ModelMap modelo, @RequestParam Integer id) {
        try {
            prestamoServicio.habilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/prestamos/mostrar";
    }

    @GetMapping("/eliminar")
    public String eliminar(ModelMap modelo, @RequestParam Integer id) {
        try {
            prestamoServicio.eliminar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/prestamos/mostrar";
    }
}
