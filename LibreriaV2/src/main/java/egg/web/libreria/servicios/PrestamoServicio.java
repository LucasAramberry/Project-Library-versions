package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Cliente;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.entidades.Prestamo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ClienteRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lucas
 */
@Service
public class PrestamoServicio {
    
    @Autowired
    private PrestamoRepositorio prestamoRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    
    @Transactional
    public void registar(Date fechaPrestamo, Date fechaDevolucion, Libro libro, Cliente cliente) throws ErrorServicio {
        
        validar(fechaPrestamo, fechaDevolucion, libro, cliente);
        
        Prestamo prestamo = new Prestamo();
        
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucion(fechaDevolucion);
        prestamo.setAlta(true);
        // queda realizar el seteo del ejemplar prestadd en el libro
        prestamo.setLibro(libro);
        prestamo.setCliente(cliente);
        
        prestamoRepositorio.save(prestamo);
    }
    
    @Transactional
    public void modificar(Integer id, Date fechaPrestamo, Date fechaDevolucion, Libro libro, Cliente cliente) throws ErrorServicio {
        
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            
            validar(fechaPrestamo, fechaDevolucion, libro, cliente);
            
            prestamo.setFechaPrestamo(fechaPrestamo);
            prestamo.setFechaDevolucion(fechaDevolucion);
            prestamo.setAlta(true);
            // queda realizar el seteo del ejemplar prestadd en el libro
            prestamo.setLibro(libro);
            prestamo.setCliente(cliente);
            
            prestamoRepositorio.save(prestamo);
        } else {
            throw new ErrorServicio("No se encontro el prestamo solicitado para modificar");
        }
    }
    
    @Transactional
    public void eliminar(Integer idPrestamo) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(idPrestamo);
        
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamoRepositorio.delete(prestamo);
        } else {
            throw new ErrorServicio("No se encontro el prestamo solicitado para eliminar");
        }
    }
    
    @Transactional
    public void deshabilitar(Integer id) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamo.setAlta(false);
            
            prestamoRepositorio.save(prestamo);
        } else {
            throw new ErrorServicio("Error al deshabilitar el prestamo solicitado");
        }
    }
    
    @Transactional
    public void habilitar(Integer id) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamo.setAlta(true);
            
            prestamoRepositorio.save(prestamo);
        } else {
            throw new ErrorServicio("Error al habilitar el prestamo solicitado");
        }
    }
    
    @Transactional
    public Prestamo buscarPrestamoPorId(Integer id) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            return prestamo;
        } else {
            throw new ErrorServicio("No se encontro el prestamo solicitado");
        }
    }
    
    public void devolucion(Integer id) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamo.setFechaDevolucion(new Date());
            // editar ejemplar prestado en libro
            
            prestamoRepositorio.save(prestamo);
        } else {
            throw new ErrorServicio("No se encontro el prestamo solicitado");
        }
    }
    
    private void validar(Date fechaPrestamo, Date fechaDevolucion, Libro libro, Cliente cliente) throws ErrorServicio {
        
        if (fechaPrestamo == null) {
            throw new ErrorServicio("Fecha de prestamo invalida.");
        }
        if (fechaPrestamo.after(fechaDevolucion)) {
            throw new ErrorServicio("Fecha de prestamo invalida. La fecha de prestamo no puede ser mayor a la de devolucion");
        }
        if (fechaDevolucion == null) {
            throw new ErrorServicio("Fecha de prestamo invalida.");
        }
        if (cliente == null) {
            throw new ErrorServicio("Cliente invalido.");
        }
        if (libro == null) {
            throw new ErrorServicio("Libro invalido.");
        }
    }
}
