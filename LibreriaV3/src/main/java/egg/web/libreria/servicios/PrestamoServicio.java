package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Libro;
import egg.web.libreria.entidades.Prestamo;
import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.PrestamoRepositorio;
import java.util.Date;
import java.util.List;
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
    private LibroServicio libroServicio;
    @Autowired
    private NotificacionServicio notificacionServicio;

    @Transactional
    public void registrar(Date fechaPrestamo, Date fechaDevolucion, Libro libro, Usuario usuario) throws ErrorServicio {

        validar(fechaPrestamo, fechaDevolucion, libro, usuario);

        //realizar el seteo del ejemplar prestado en el libro
        libroServicio.prestarLibro(libro);

        Prestamo prestamo = new Prestamo();

        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucion(fechaDevolucion);
        prestamo.setAlta(new Date());

        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);

        prestamoRepositorio.save(prestamo);

//        notificacionServicio.enviar("Realizaste el prestamo de un libro.", "Libreria web", usuario.getMail());
    }

    @Transactional
    public void modificar(Integer id, Date fechaPrestamo, Date fechaDevolucion, Libro libro, Usuario usuario) throws ErrorServicio {

        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();

            validar(fechaPrestamo, fechaDevolucion, libro, usuario);

            prestamo.setFechaPrestamo(fechaPrestamo);
            prestamo.setFechaDevolucion(fechaDevolucion);

            if (prestamo.getLibro() != libro) {
                //realizamos el seteo del ejemplar prestado en el libro nuevo
                libroServicio.prestarLibro(libro);
                //devolvemos el ejemplar del libro que habiamos pedido
                libroServicio.devolverLibro(prestamo.getLibro());
            }

            prestamo.setLibro(libro);
            prestamo.setUsuario(usuario);

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

            //verificamos si el prestamo esta dado de alta ya que debemos devolver el ejemplar
            // si esta de baja dando la baja automaticamente se devuelve el ejemplar del libro
            if (prestamo.getBaja() == null) {
                libroServicio.devolverLibro(prestamo.getLibro());
            }

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

            //devolvemos el libro por deshabilitar el prestamo
            libroServicio.devolverLibro(prestamo.getLibro());

            prestamo.setBaja(new Date());

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

            //chequeamos que la fecha de devolucion sea posterior a la actual para poder volver habilitar el prestamo
            if (prestamo.getFechaDevolucion().after(new Date())) {
                //realizamos el seteo del ejemplar prestado en el libro nuevo
                libroServicio.prestarLibro(prestamo.getLibro());
            }

            prestamo.setBaja(null);

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

    @Transactional
    public List<Prestamo> buscarPrestamosPorUsuario(String id) throws ErrorServicio {
        List<Prestamo> prestamosUsuario = prestamoRepositorio.buscarPrestamoPorUsuario(id);
        return prestamosUsuario;
    }

    @Transactional
    public List<Prestamo> listaPrestamos() throws ErrorServicio {
        List<Prestamo> listaPrestamos = prestamoRepositorio.findAll();
        if (listaPrestamos != null) {
            return listaPrestamos;
        } else {
            throw new ErrorServicio("No se encuentra ningun prestamo.");
        }
    }

    @Transactional
    public void devolucion(Integer id) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            prestamo.setFechaDevolucion(new Date());

            //devolvemos el libro por deshabilitar el prestamo
            libroServicio.devolverLibro(prestamo.getLibro());

            prestamoRepositorio.save(prestamo);
        } else {
            throw new ErrorServicio("No se encontro el prestamo solicitado");
        }
    }

    private void validar(Date fechaPrestamo, Date fechaDevolucion, Libro libro, Usuario usuario) throws ErrorServicio {

//        if (fechaPrestamo == null || fechaPrestamo.after(new Date()) || fechaPrestamo.before(fechaDevolucion)) {
        if (fechaPrestamo == null) {
            throw new ErrorServicio("Fecha de prestamo invalida.");
        }
        if (fechaDevolucion == null || fechaDevolucion.before(fechaPrestamo) || fechaDevolucion.equals(fechaPrestamo)) {
            throw new ErrorServicio("Fecha de prestamo invalida.");
        }
        if (usuario == null) {
            throw new ErrorServicio("Usuario invalido.");
        }
        if (libro == null) {
            throw new ErrorServicio("Libro invalido.");
        }
    }
}
