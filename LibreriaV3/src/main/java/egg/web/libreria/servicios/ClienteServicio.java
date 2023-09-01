package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Cliente;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ClienteRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lucas
 */
@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Transactional
    public void registar(String nombre, String apellido, String documento, String telefono) throws ErrorServicio {

        validar(nombre, apellido, documento, telefono);

        Cliente cliente = new Cliente();

        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDocumento(documento);
        cliente.setTelefono(telefono);
        cliente.setAlta(true);

        clienteRepositorio.save(cliente);
    }

    @Transactional
    public void modificar(Integer id, String nombre, String apellido, String documento, String telefono) throws ErrorServicio {

        validar(nombre, apellido, documento, telefono);

        Optional<Cliente> respuesta = clienteRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();

            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDocumento(documento);
            cliente.setTelefono(telefono);

            clienteRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("No se encontro el cliente solicitado para modificar");
        }
    }

    @Transactional
    public void eliminar(Integer idCliente) throws ErrorServicio {
        Optional<Cliente> respuesta = clienteRepositorio.findById(idCliente);

        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            clienteRepositorio.delete(cliente);
        } else {
            throw new ErrorServicio("No se encontro el cliente solicitado para eliminar");
        }
    }

    @Transactional
    public void deshabilitar(Integer id) throws ErrorServicio {
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(false);

            clienteRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("Error al deshabilitar el cliente solicitado");
        }
    }

    @Transactional
    public void habilitar(Integer id) throws ErrorServicio {
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setAlta(true);

            clienteRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("Error al habilitar el cliente solicitado");
        }
    }

    @Transactional
    public Cliente buscarClientePorId(Integer id) throws ErrorServicio {
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            return cliente;
        } else {
            throw new ErrorServicio("No se encontro el cliente solicitado");
        }
    }

    private void validar(String nombre, String apellido, String documento, String telefono) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("Nombre invalido.");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("Apellido invalido.");
        }
        if (documento == null || documento.isEmpty() || (documento.length() < 7 || documento.length() > 10)) {
            throw new ErrorServicio("Documento invalido.");
        }
        if (telefono == null || telefono.isEmpty() || telefono.length() < 10 || telefono.length() > 10) {
            throw new ErrorServicio("Telefono invalido.");
        }
    }
}
