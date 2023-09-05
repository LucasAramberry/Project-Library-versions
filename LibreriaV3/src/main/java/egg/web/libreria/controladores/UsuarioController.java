package egg.web.libreria.controladores;

import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.UsuarioRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import egg.web.libreria.servicios.UsuarioServicio;
import egg.web.libreria.servicios.LibroServicio;
import egg.web.libreria.servicios.PrestamoServicio;
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
@RequestMapping("/clientes")
public class UsuarioController {

//    @Autowired
//    private PrestamoRepositorio prestamoRepositorio;
//    @Autowired
//    private PrestamoServicio prestamoServicio;
//    @Autowired
//    private LibroServicio libroServicio;
//    @Autowired
//    private LibroRepositorio libroRepositorio;
//    @Autowired
//    private ClienteRepositorio clienteRepositorio;
//    @Autowired
//    private ClienteServicio clienteServicio;
//
//    @GetMapping("/mostrar")
//    public String mostrar(ModelMap model) {
//
//        List<Cliente> clientes = clienteRepositorio.findAll();
//        model.put("clientes", clientes);
//
//        return "clientes.html";
//    }
//
//    @GetMapping("/registro-cliente")
//    public String registroCliente(ModelMap model) {
//        List<Cliente> clientes = clienteRepositorio.findAll();
//        model.put("clientes", clientes);
//
//        return "add-cliente.html";
//    }
//
//    @PostMapping("/registrar-cliente")
//    public String registrarCliente(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String documento, @RequestParam String telefono) {
//
//        try {
//            clienteServicio.registar(nombre, apellido, documento, telefono);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//
//            modelo.put("nombre", nombre);
//            modelo.put("apellido", apellido);
//            modelo.put("documento", documento);
//            modelo.put("telefono", telefono);
//
//            return "add-cliente.html";
//        }
//        modelo.put("titulo", "Registro exitoso!");
//        modelo.put("descripcion", "El cliente ingresado fue registrado correctamente.");
//        return "exito.html";
//    }
//
//    @GetMapping("/modificar-cliente")
//    public String modificarCliente(ModelMap modelo, @RequestParam Integer id) {
//
//        try {
//            Cliente cliente = clienteServicio.buscarClientePorId(id);
//            modelo.addAttribute("cliente", cliente);
//        } catch (ErrorServicio ex) {
//            modelo.addAttribute("error", ex.getMessage());
//        }
//
//        return "modificar-cliente.html";
//    }
//
//    @PostMapping("/actualizar-cliente")
//    public String modificarCliente(ModelMap modelo, @RequestParam Integer id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String documento, @RequestParam String telefono) {
//        Cliente cliente = null;
//        try {
//            cliente = clienteServicio.buscarClientePorId(id);
//            clienteServicio.modificar(id, nombre, apellido, documento, telefono);
//            return "redirect:/clientes/mostrar";
//        } catch (ErrorServicio ex) {
//
//            modelo.put("cliente", cliente);
//            
//            modelo.put("error", ex.getMessage());
////            modelo.put("nombre", nombre);
////            modelo.put("apellido", apellido);
////            modelo.put("documento", documento);
////            modelo.put("telefono", telefono);
//
//            return "modificar-cliente.html";
//        }
//    }
//
//    @GetMapping("/baja")
//    public String baja(ModelMap modelo, @RequestParam Integer id) {
//        try {
//            clienteServicio.deshabilitar(id);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//        }
//        return "redirect:/clientes/mostrar";
//    }
//
//    @GetMapping("/alta")
//    public String alta(ModelMap modelo, @RequestParam Integer id) {
//        try {
//            clienteServicio.habilitar(id);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//        }
//        return "redirect:/clientes/mostrar";
//    }
//
//    @GetMapping("/eliminar")
//    public String eliminar(ModelMap modelo, @RequestParam Integer id) {
//        try {
//            clienteServicio.eliminar(id);
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//        }
//        return "redirect:/clientes/mostrar";
//    }
}
