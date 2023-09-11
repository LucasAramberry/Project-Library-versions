package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.entidades.Editorial;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.AutorRepositorio;
import egg.web.libreria.repositorios.EditorialRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.servicios.LibroServicio;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lucas
 */
@PreAuthorize("hasAnyRole('ROLE_USUARIO')")
@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @GetMapping("/mostrar")
    public String libros(ModelMap model) {
        List<Libro> libros = libroRepositorio.findAll();

        model.put("libros", libros);

        return "libros.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/mostrarLibro/{id}")
    public String mostrarLibros(@PathVariable String id, ModelMap modelo) {

        try {
            modelo.put("libro", libroServicio.buscarLibroPorId(id));

            return "mostrarLibro.html";
        } catch (ErrorServicio ex) {
            return "redirect:/index";
        }
    }

    @GetMapping("/registro-libro")
    public String registroLibro(ModelMap modelo) {
        List<Autor> autores = autorRepositorio.findAll();
        List<Editorial> editoriales = editorialRepositorio.findAll();

        modelo.put("autores", autores);
        modelo.put("editoriales", editoriales);

        return "add-libro.html";
    }

    @PostMapping("/registrar-libro")
    public String registrarLibro(ModelMap modelo, MultipartFile archivo, @RequestParam String isbn, @RequestParam String titulo, @RequestParam String descripcion, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPublicacion, @RequestParam Integer cantidadPaginas, @RequestParam Integer ejemplares, @RequestParam Integer ejemplaresPrestados, @RequestParam String idAutor, @RequestParam String idEditorial) {

        try {
            libroServicio.registrar(archivo, isbn, titulo, descripcion, fechaPublicacion, cantidadPaginas, ejemplares, ejemplaresPrestados, idAutor, idEditorial);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());

            List<Autor> autores = autorRepositorio.findAll();
            List<Editorial> editoriales = editorialRepositorio.findAll();

            modelo.put("autores", autores);
            modelo.put("editoriales", editoriales);

            modelo.put("isbn", isbn);
            modelo.put("titulo", titulo);
            modelo.put("descripcion", descripcion);
            modelo.put("fechaPublicacion", fechaPublicacion);
            modelo.put("cantidadPaginas", cantidadPaginas);
            modelo.put("ejemplares", ejemplares);
            modelo.put("ejemplaresPrestados", ejemplaresPrestados);
            modelo.put("idAutor", idAutor);
            modelo.put("idEditorial", idEditorial);

            return "add-libro.html";
        }
        modelo.put("titulo", "Registro exitoso!");
        modelo.put("descripcion", "El libro ingresado fue registrado correctamente.");
        return "exito.html";
    }

    @GetMapping("/modificar-libro")
    public String modificarLibro(ModelMap modelo, @RequestParam String id) {

        List<Autor> autores = autorRepositorio.findAll();
        modelo.put("autores", autores);

        List<Editorial> editoriales = editorialRepositorio.findAll();
        modelo.put("editoriales", editoriales);

        try {
            Libro libro = libroServicio.buscarLibroPorId(id);
            modelo.addAttribute("libro", libro);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "modificar-libro.html";
    }

    @PostMapping("/actualizar-libro")
    public String modificarLibro(ModelMap modelo, MultipartFile archivo, @RequestParam String id, @RequestParam String isbn, @RequestParam String titulo, @RequestParam String descripcion, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPublicacion, @RequestParam Integer cantidadPaginas, @RequestParam Integer ejemplares, @RequestParam Integer ejemplaresPrestados, @RequestParam String idAutor, @RequestParam String idEditorial) {
        Libro libro = null;
        try {
            libro = libroServicio.buscarLibroPorId(id);
            libroServicio.modificar(archivo, id, isbn, titulo, descripcion, fechaPublicacion, cantidadPaginas, ejemplares, ejemplaresPrestados, idAutor, idEditorial);
            return "redirect:/libros/mostrar";
        } catch (ErrorServicio ex) {
            List<Autor> autores = autorRepositorio.findAll();
            modelo.put("autores", autores);

            List<Editorial> editoriales = editorialRepositorio.findAll();
            modelo.put("editoriales", editoriales);

            modelo.put("libro", libro);

            modelo.put("error", ex.getMessage());

            return "modificar-libro.html";
        }
    }

    @GetMapping("/baja")
    public String baja(ModelMap modelo, @RequestParam String id) {
        try {
            libroServicio.deshabilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/libros/mostrar";
    }

    @GetMapping("/alta")
    public String alta(ModelMap modelo, @RequestParam String id) {
        try {
            libroServicio.habilitar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/libros/mostrar";
    }

    @GetMapping("/eliminar")
    public String eliminar(ModelMap modelo, @RequestParam String id) {
        try {
            libroServicio.eliminar(id);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
        }
        return "redirect:/libros/mostrar";
    }
}
