package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.AutorRepositorio;
import egg.web.libreria.servicios.AutorServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
@Controller
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private AutorRepositorio autorRepositorio;

    @GetMapping("/mostrar")
    public String autores(ModelMap modelo) {
        List<Autor> autores = autorRepositorio.findAll();

        modelo.put("autores", autores);

        return "autores.html";
    }

    @GetMapping("/registro-autor")
    public String registroAutor() {
        return "add-autor.html";
    }

    @PostMapping("/registrar-autor")
    public String registrarAutor(ModelMap modelo, @RequestParam String nombre) {
        try {
            autorServicio.registrar(nombre);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("nombre", nombre);

            return "add-autor.html";
        }
        modelo.put("titulo", "Registro exitoso!");
        modelo.put("descripcion", "El autor ingresado fue registrado correctamente.");
        return "exito.html";
    }

    @GetMapping("/modificar-autor")
    public String modificarAutor(ModelMap modelo, @RequestParam String id) {

        try {
            Autor autor = autorServicio.buscarAutorPorId(id);
            modelo.addAttribute("autor", autor);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "modificar-autor.html";
    }

    @PostMapping("/actualizar-autor")
    public String modificarAutor(ModelMap modelo, @RequestParam String id, @RequestParam String nombre) {

        Autor autor = null;

        try {
            autor = autorServicio.buscarAutorPorId(id);
            autorServicio.modificar(id, nombre);
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
