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

    /**
     * Metodo para registrar un prestamo
     *
     * @param fechaPrestamo
     * @param fechaDevolucion
     * @param libro
     * @param usuario
     * @throws ErrorServicio
     */
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

    /**
     * Metodo para modificar un prestamo
     *
     * @param id
     * @param fechaPrestamo
     * @param fechaDevolucion
     * @param libro
     * @param usuario
     * @throws ErrorServicio
     */
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
            throw new ErrorServicio("No se encontro el prestamo solicitado para modificar.");
        }
    }

    /**
     * metodo para eliminar un prestamo
     *
     * @param idPrestamo
     * @throws ErrorServicio
     */
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
            throw new ErrorServicio("No se encontro el prestamo solicitado para eliminar.");
        }
    }

    /**
     * Metodo para deshabilitar un prestamo
     *
     * @param id
     * @throws ErrorServicio
     */
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
            throw new ErrorServicio("Error al deshabilitar el prestamo solicitado.");
        }
    }

    /**
     * Metodo para habilitar un prestamo
     *
     * @param id
     * @throws ErrorServicio
     */
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
            throw new ErrorServicio("Error al habilitar el prestamo solicitado.");
        }
    }

    /**
     * metodo para buscar prestamos por id
     *
     * @param id
     * @return
     * @throws ErrorServicio
     */
    @Transactional
    public Prestamo buscarPrestamoPorId(Integer id) throws ErrorServicio {
        Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Prestamo prestamo = respuesta.get();
            return prestamo;
        } else {
            throw new ErrorServicio("No se encontro el prestamo solicitado por id.");
        }
    }

    @Transactional
    public List<Prestamo> buscarPrestamosPorUsuario(String id) {
        return prestamoRepositorio.buscarPrestamoPorUsuario(id);
    }

    @Transactional
    public List<Prestamo> listaPrestamos() {
        return prestamoRepositorio.findAll();
    }

    @Transactional
    public List<Prestamo> listaPrestamosActivos() {
        return prestamoRepositorio.buscarActivos();
    }

    @Transactional
    public List<Prestamo> listaPrestamosInactivos() {
        return prestamoRepositorio.buscarInactivos();
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

        if (fechaPrestamo == null || fechaPrestamo.after(fechaDevolucion)) {
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
