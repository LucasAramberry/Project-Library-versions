package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Cliente;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.entidades.Prestamo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ClienteRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import egg.web.libreria.servicios.ClienteServicio;
import egg.web.libreria.servicios.LibroServicio;
import egg.web.libreria.servicios.PrestamoServicio;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/mostrar")
    public String mostrar(ModelMap model) {

        List<Prestamo> prestamos = prestamoRepositorio.findAll();
        model.put("prestamos", prestamos);

        return "prestamos.html";
    }

    @GetMapping("/registro-prestamo")
    public String registroPrestamo(ModelMap model) {
        //colocamos la fecha actual para al crear el prestamo nos setee la fecha del dia en q se hizo
        model.addAttribute("fechaPrestamo", LocalDate.now());

        List<Libro> libros = libroRepositorio.findAll();
        model.put("libros", libros);

        List<Cliente> clientes = clienteRepositorio.findAll();
        model.put("clientes", clientes);

        return "add-prestamo.html";
    }

    @PostMapping("/registrar-prestamo")
    public String registrarPrestamo(ModelMap modelo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam Integer idCliente) {

        try {
            Libro libro = libroServicio.buscarLibroPorId(idLibro);
            Cliente cliente = clienteServicio.buscarClientePorId(idCliente);
            prestamoServicio.registar(fechaPrestamo, fechaDevolucion, libro, cliente);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());

            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);

            List<Cliente> clientes = clienteRepositorio.findAll();
            modelo.put("clientes", clientes);

            modelo.put("fechaPrestamo", fechaPrestamo);
            modelo.put("fechaDevolucion", fechaDevolucion);
            modelo.put("idLibro", idLibro);
            modelo.put("idCliente", idCliente);

            return "add-prestamo.html";
        }
        modelo.put("titulo", "Registro exitoso!");
        modelo.put("descripcion", "El prestamo ingresado fue registrado correctamente.");
        return "exito.html";
    }

    @GetMapping("/modificar-prestamo")
    public String modificarPrestamo(ModelMap modelo, @RequestParam Integer id) {

        List<Libro> libros = libroRepositorio.findAll();
        modelo.put("libros", libros);

        List<Cliente> clientes = clienteRepositorio.findAll();
        modelo.put("clientes", clientes);

        try {
            Prestamo prestamo = prestamoServicio.buscarPrestamoPorId(id);
            modelo.addAttribute("prestamo", prestamo);
        } catch (ErrorServicio ex) {
            modelo.addAttribute("error", ex.getMessage());
        }

        return "modificar-prestamo.html";
    }

    @PostMapping("/actualizar-prestamo")
    public String modificarPrestamo(ModelMap modelo, @RequestParam Integer id, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, @RequestParam String idLibro, @RequestParam Integer idCliente) {
        Prestamo prestamo = null;
        try {
            prestamo = prestamoServicio.buscarPrestamoPorId(id);
            Libro libro = libroServicio.buscarLibroPorId(idLibro);
            Cliente cliente = clienteServicio.buscarClientePorId(idCliente);
            prestamoServicio.modificar(id, fechaPrestamo, fechaDevolucion, libro, cliente);
            return "redirect:/prestamos/mostrar";
        } catch (ErrorServicio ex) {
            List<Libro> libros = libroRepositorio.findAll();
            modelo.put("libros", libros);

            List<Cliente> clientes = clienteRepositorio.findAll();
            modelo.put("clientes", clientes);

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
