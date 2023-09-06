package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.entidades.Zona;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.UsuarioRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import egg.web.libreria.repositorios.ZonaRepositorio;
import egg.web.libreria.servicios.UsuarioServicio;
import egg.web.libreria.servicios.LibroServicio;
import egg.web.libreria.servicios.PrestamoServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/usuario")
public class UsuarioController {

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
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @GetMapping("/editar-perfil")
    public String editarPerfil(ModelMap modelo, @RequestParam String id) {

        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);

        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            modelo.addAttribute("usuario", usuario);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "perfil.html";
    }

    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String documento, @RequestParam String telefono, @RequestParam(required = false) Sexo sexo, @RequestParam(required = false) String idZona, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2) {
        Usuario usuario = null;
        try {
            usuario = usuarioServicio.buscarUsuarioPorId(id);
            usuarioServicio.modificar(archivo, id, nombre, apellido, documento, telefono, Sexo.Hombre, idZona, mail, clave, clave2);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);

            modelo.put("error", ex.getMessage());

            modelo.put("usuario", usuario);

            return "perfil.html";
        }
    }

//    @GetMapping("/mostrar")
//    public String mostrar(ModelMap model) {
//
//        List<Usuario> usuarios = usuarioRepositorio.findAll();
//        model.put("usuarios", usuarios);
//
//        return "usuarios.html";
//    }
//
//    @GetMapping("/registro-usuario")
//    public String registroUsuario(ModelMap model) {
//        List<Usuario> usuario = usuarioRepositorio.findAll();
//        model.put("usuarios", usuario);
//
//        return "add-usuario.html";
//    }
//
//    @PostMapping("/registrar-usuario")
//    public String registrarUsuario(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String documento, @RequestParam String telefono) {
//
//        try {
//            usuarioServicio.registar(nombre, apellido, documento, telefono);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//
//            modelo.put("nombre", nombre);
//            modelo.put("apellido", apellido);
//            modelo.put("documento", documento);
//            modelo.put("telefono", telefono);
//
//            return "add-usuario.html";
//        }
//        modelo.put("titulo", "Registro exitoso!");
//        modelo.put("descripcion", "El usuario ingresado fue registrado correctamente.");
//        return "exito.html";
//    }
//
//    @GetMapping("/baja")
//    public String baja(ModelMap modelo, @RequestParam String id) {
//        try {
//            usuarioServicio.deshabilitar(id);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//        }
//        return "redirect:/usuario/mostrar";
//    }
//
//    @GetMapping("/alta")
//    public String alta(ModelMap modelo, @RequestParam String id) {
//        try {
//            usuarioServicio.habilitar(id);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//        }
//        return "redirect:/usuario/mostrar";
//    }
//
//    @GetMapping("/eliminar")
//    public String eliminar(ModelMap modelo, @RequestParam String id) {
//        try {
//            usuarioServicio.eliminar(id);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//        }
//        return "redirect:/usuario/mostrar";
//    }
}
