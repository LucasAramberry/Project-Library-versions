package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Editorial;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.servicios.EditorialServicio;
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
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@Controller
@RequestMapping("/editoriales")
public class EditorialController {

    @Autowired
    private EditorialServicio editorialServicio;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/registro")
    public String registroEditorial() {
        return "registro-editorial.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String registrarEditorial(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre) {
        try {
            editorialServicio.registrar(archivo, nombre);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);

            return "registro-editorial.html";
        }
        modelo.put("titulo", "Registro exitoso!");
        modelo.put("descripcion", "La editorial ingresado fue registrado correctamente.");
        return "exito.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/modificar")
    public String modificarEditorial(ModelMap modelo, @RequestParam String id) {

        try {
            Editorial editorial = editorialServicio.buscarEditorialPorId(id);
            modelo.addAttribute("editorial", editorial);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "modificar-editorial.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/actualizar")
    public String modificarEditorial(ModelMap modelo, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre) {

        Editorial editorial = null;

        try {
            editorial = editorialServicio.buscarEditorialPorId(id);
            editorialServicio.modificar(archivo, id, nombre);
            return "redirect:/editoriales";
        } catch (ErrorServicio ex) {
            modelo.put("editorial", editorial);
            modelo.put("error", ex.getMessage());
            return "modificar-editorial.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/baja")
    public String baja(ModelMap modelo, @RequestParam String id) {
        try {
            editorialServicio.deshabilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/editoriales";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/alta")
    public String alta(ModelMap modelo, @RequestParam String id) {
        try {
            editorialServicio.habilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/editoriales";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/eliminar")
    public String eliminar(ModelMap modelo, @RequestParam String id) {
        try {
            editorialServicio.eliminar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/editoriales";
    }
}
