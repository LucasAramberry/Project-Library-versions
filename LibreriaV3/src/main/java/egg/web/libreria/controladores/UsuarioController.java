package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.entidades.Zona;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ZonaRepositorio;
import egg.web.libreria.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
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
@PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, ModelMap modelo, @RequestParam String id) {

        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);

        modelo.put("sexos", Sexo.values());

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/";
        }

        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            modelo.addAttribute("usuario", usuario);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String documento, @RequestParam String telefono, @RequestParam Sexo sexo, @RequestParam String idZona, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2) {
        Usuario usuario = null;
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/";
            }

            usuario = usuarioServicio.buscarUsuarioPorId(id);
            usuarioServicio.modificar(archivo, id, nombre, apellido, documento, telefono, sexo, idZona, mail, clave, clave2);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("sexos", Sexo.values());
            modelo.put("error", ex.getMessage());
            modelo.put("usuario", usuario);

            return "perfil.html";
        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/cambiar-rol")
    public String cambiarRol(ModelMap modelo, @RequestParam String id) {
        try {
            usuarioServicio.cambiarRol(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/baja")
    public String baja(ModelMap modelo, @RequestParam String id) {
        try {
            usuarioServicio.deshabilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta")
    public String alta(ModelMap modelo, @RequestParam String id) {
        try {
            usuarioServicio.habilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar")
    public String eliminar(ModelMap modelo, @RequestParam String id) {
        try {
            usuarioServicio.eliminar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}
