package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.AutorRepositorio;
import egg.web.libreria.servicios.AutorServicio;
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
//@PreAuthorize("hasAnyRole('ROLE_USUARIO')")
@Controller
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private AutorRepositorio autorRepositorio;

    @GetMapping("/registro")
    public String registroAutor() {
        return "registro-autor.html";
    }

    @PostMapping("/registrar")
    public String registrarAutor(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre) {
        try {
            autorServicio.registrar(archivo, nombre);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);

            return "registro-autor.html";
        }
        modelo.put("titulo", "Registro exitoso!");
        modelo.put("descripcion", "El autor ingresado fue registrado correctamente.");
        return "exito.html";
    }

    @GetMapping("/modificar")
    public String modificarAutor(ModelMap modelo, @RequestParam String id) {

        try {
            Autor autor = autorServicio.buscarAutorPorId(id);
            modelo.addAttribute("autor", autor);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "modificar-autor.html";
    }

    @PostMapping("/actualizar")
    public String modificarAutor(ModelMap modelo, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre) {

        Autor autor = null;

        try {
            autor = autorServicio.buscarAutorPorId(id);
            autorServicio.modificar(archivo, id, nombre);
            return "redirect:/autores/mostrar";
        } catch (ErrorServicio ex) {
            modelo.put("autor", autor);
            modelo.put("error", ex.getMessage());
            return "modificar-autor.html";
        }
    }

    @GetMapping("/baja")
    public String baja(ModelMap modelo, @RequestParam String id) {
        try {
            autorServicio.deshabilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/autores/mostrar";
    }

    @GetMapping("/alta")
    public String alta(ModelMap modelo, @RequestParam String id) {
        try {
            autorServicio.habilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/autores/mostrar";
    }

    @GetMapping("/eliminar")
    public String eliminar(ModelMap modelo, @RequestParam String id) {
        try {
            autorServicio.eliminar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/autores/mostrar";
    }
}
