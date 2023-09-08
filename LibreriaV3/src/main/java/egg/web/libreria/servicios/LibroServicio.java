package egg.web.libreria.servicios;

import egg.web.libreria.entidades.*;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.*;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lucas
 */
@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private FotoServicio fotoServicio;

    /**
     * Metodo para registrar libro
     * @param archivo
     * @param isbn
     * @param titulo
     * @param descripcion
     * @param fechaPublicacion
     * @param cantidadPaginas
     * @param ejemplares
     * @param ejemplaresPrestados
     * @param idAutor
     * @param idEditorial
     * @throws ErrorServicio 
     */
    @Transactional
    public void registrar(MultipartFile archivo, String isbn, String titulo, String descripcion, Date fechaPublicacion, Integer cantidadPaginas, Integer ejemplares, Integer ejemplaresPrestados, String idAutor, String idEditorial) throws ErrorServicio {
        Autor autor = autorServicio.buscarAutorPorId(idAutor);
        Editorial editorial = editorialServicio.buscarEditorialPorId(idEditorial);

        validar(isbn, titulo, descripcion, fechaPublicacion, cantidadPaginas, ejemplares, ejemplaresPrestados, autor, editorial);

        Libro l = new Libro();
        l.setIsbn(isbn);
        l.setTitulo(titulo);
        l.setDescripcion(descripcion);
        l.setFechaPublicacion(fechaPublicacion);
        l.setCantidadPaginas(cantidadPaginas);
        l.setEjemplares(ejemplares);
        l.setEjemplaresPrestados(ejemplaresPrestados);
        l.setEjemplaresRestantes(ejemplares - ejemplaresPrestados);
        l.setAlta(new Date());
        l.setAutor(autor);
        l.setEditorial(editorial);

        Foto foto = fotoServicio.guardar(archivo);
        l.setFoto(foto);

        libroRepositorio.save(l);
    }

    /**
     * metodo para modificar libro
     * @param archivo
     * @param id
     * @param isbn
     * @param titulo
     * @param descripcion
     * @param fechaPublicacion
     * @param cantidadPaginas
     * @param ejemplares
     * @param ejemplaresPrestados
     * @param idAutor
     * @param idEditorial
     * @throws ErrorServicio 
     */
    @Transactional
    public void modificar(MultipartFile archivo, String id, String isbn, String titulo, String descripcion, Date fechaPublicacion, Integer cantidadPaginas, Integer ejemplares, Integer ejemplaresPrestados, String idAutor, String idEditorial) throws ErrorServicio {

        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();

            Autor autor = autorServicio.buscarAutorPorId(idAutor);
        Editorial editorial = editorialServicio.buscarEditorialPorId(idEditorial);

            validar(isbn, titulo,descripcion, fechaPublicacion, cantidadPaginas, ejemplares, ejemplaresPrestados, autor, editorial);

            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setDescripcion(descripcion);
            libro.setFechaPublicacion(fechaPublicacion);
            libro.setCantidadPaginas(cantidadPaginas);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(ejemplaresPrestados);
            libro.setEjemplaresRestantes(ejemplares - ejemplaresPrestados);

            libro.setAutor(autor);
            libro.setEditorial(editorial);

            String idFoto = null;
            if (libro.getFoto() != null) {
                idFoto = libro.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            libro.setFoto(foto);
            
            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontro el libro solicitado para modificar");
        }
    }

    /**
     * metodo para eliminar libro
     * @param idLibro
     * @throws ErrorServicio 
     */
    @Transactional
    public void eliminar(String idLibro) throws ErrorServicio {
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libroRepositorio.delete(libro);
        } else {
            throw new ErrorServicio("No se encontro el libro solicitado para eliminar");
        }
    }

    /**
     * Metodo para deshabilitar libro
     * @param id
     * @throws ErrorServicio 
     */
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setBaja(new Date());

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("Error al deshabilitar el libro solicitado");
        }
    }

    /**
     * Metodo para habilitar libro
     * @param id
     * @throws ErrorServicio 
     */
    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setBaja(null);

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("Error al habilitar el libro solicitado");
        }
    }

    /**
     * Metodo para buscar libro por id
     * @param id
     * @return
     * @throws ErrorServicio 
     */
    @Transactional
    public Libro buscarLibroPorId(String id) throws ErrorServicio {
        Optional<Libro> respuesta = libroRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            return libro;
        } else {
            throw new ErrorServicio("No se encontro el libro solicitado");
        }
    }

    /**
     * Metodo prestamo libro. Cuando prestamos restamos los restantes y sumamos a prestados 
     * @param libro
     * @throws ErrorServicio 
     */
    @Transactional
    public void prestarLibro(Libro libro) throws ErrorServicio {
        if (libro != null) {
            if (libro.getEjemplaresRestantes() >= 1) {
                libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
                libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);
                libroRepositorio.save(libro);
            } else {
                throw new ErrorServicio("El libro ingresado no tiene mas ejemplares disponibles");
            }
        } else {
            throw new ErrorServicio("El libro no esta disponible");
        }
    }

    /**
     * metodo para devolver el libro que prestamos
     * @param libro
     * @throws ErrorServicio 
     */
    @Transactional
    public void devolverLibro(Libro libro) throws ErrorServicio {
        if (libro != null) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + 1);
            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("Error al devolver el libro.");
        }
    }

    /**
     * metodo de validacion de atributos
     * @param isbn
     * @param titulo
     * @param descripcion
     * @param fechaPublicacion
     * @param cantidadPaginas
     * @param ejemplares
     * @param ejemplaresPrestados
     * @param autor
     * @param editorial
     * @throws ErrorServicio 
     */
    private void validar(String isbn, String titulo, String descripcion, Date fechaPublicacion, Integer cantidadPaginas, Integer ejemplares, Integer ejemplaresPrestados, Autor autor, Editorial editorial) throws ErrorServicio {
        if (isbn == null || isbn.isEmpty() || isbn.length() < 13 || isbn.length() > 13) {
            throw new ErrorServicio("El isbn del libro no puede ser nulo y debe contener 13 numeros");
        }
        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("El titulo del libro no puede ser nulo");
        }
        if (descripcion == null || descripcion.isEmpty()) {
            throw new ErrorServicio("La descripcion del libro no puede ser nula");
        }
        if (fechaPublicacion == null || fechaPublicacion.getYear() > 2023) {
            throw new ErrorServicio("Fecha de publicacion invalida.");
        }
        if (cantidadPaginas <= 0) {
            throw new ErrorServicio("Cantidad de paginas del libro invalida.");
        }
        if (ejemplares <= 0) {
            throw new ErrorServicio("Cantidad de ejemplares del libro invalida.");
        }
        if (ejemplaresPrestados < 0 || ejemplaresPrestados > ejemplares) {
            throw new ErrorServicio("Cantidad de ejemplares prestados del libro invalida");
        }
        if (autor == null) {
            throw new ErrorServicio("El autor del libro no puede ser nulo");
        }
        if (editorial == null) {
            throw new ErrorServicio("La editorial del libro no puede ser nulo");
        }
    }
}
