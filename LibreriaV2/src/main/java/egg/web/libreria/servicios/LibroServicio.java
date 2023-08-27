package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.entidades.Editorial;
import egg.web.libreria.entidades.Libro;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.AutorRepositorio;
import egg.web.libreria.repositorios.EditorialRepositorio;
import egg.web.libreria.repositorios.LibroRepositorio;
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
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void registrar(String isbn, String titulo, Date fechaPublicacion, Integer ejemplares, Integer ejemplaresPrestados, String idAutor, String idEditorial) throws ErrorServicio {
        Autor autor = autorRepositorio.getOne(idAutor);
        Editorial editorial = editorialRepositorio.findById(idEditorial).get();

        validar(isbn, titulo, fechaPublicacion, ejemplares, ejemplaresPrestados, autor, editorial);

        Libro l = new Libro();
        l.setIsbn(isbn);
        l.setTitulo(titulo);
        l.setFechaPublicacion(fechaPublicacion);
        l.setEjemplares(ejemplares);
        l.setEjemplaresPrestados(ejemplaresPrestados);
        l.setEjemplaresRestantes(ejemplares - ejemplaresPrestados);
        l.setAlta(true);
        l.setAutor(autor);
        l.setEditorial(editorial);

        libroRepositorio.save(l);
    }

    @Transactional
    public void modificar(String id, String isbn, String titulo, Date fechaPublicacion, Integer ejemplares, Integer ejemplaresPrestados, String idAutor, String idEditorial) throws ErrorServicio {

        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            
            Autor autor = autorRepositorio.getOne(idAutor);
            Editorial editorial = editorialRepositorio.findById(idEditorial).get();
            
            validar(isbn, titulo, fechaPublicacion, ejemplares, ejemplaresPrestados, autor, editorial);
            
            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setFechaPublicacion(fechaPublicacion);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(ejemplaresPrestados);

            libro.setAutor(autor);
            libro.setEditorial(editorial);

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("No se encontro el libro solicitado para modificar");
        }
    }
    
    @Transactional
    public void eliminar(String idLibro) throws ErrorServicio{
        Optional<Libro> respuesta = libroRepositorio.findById(idLibro);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libroRepositorio.delete(libro);
        }else{
            throw new ErrorServicio("No se encontro el libro solicitado para eliminar");
        }
    }

    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(false);

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("Error al deshabilitar el libro solicitado");
        }
    }

    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Libro> respuesta = libroRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Libro libro = respuesta.get();
            libro.setAlta(true);

            libroRepositorio.save(libro);
        } else {
            throw new ErrorServicio("Error al habilitar el libro solicitado");
        }
    }
    
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

    private void validar(String isbn, String titulo, Date fechaPublicacion, Integer ejemplares, Integer ejemplaresPrestados, Autor autor, Editorial editorial) throws ErrorServicio {
        if (isbn == null || isbn.isEmpty() || isbn.length() < 13 || isbn.length() > 13) {
            throw new ErrorServicio("El isbn del libro no puede ser nulo y debe contener 13 numeros");
        }
        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("El titulo del libro no puede ser nulo");
        }
        if (fechaPublicacion == null || fechaPublicacion.getYear() < 0 || fechaPublicacion.getYear() > 2023) {
            throw new ErrorServicio("El año ingresado no registra ningun libro");
        }
        if (ejemplares < 0) {
            throw new ErrorServicio("Los ejemplares del libro no pueden ser negativos");
        }
        if (ejemplaresPrestados < 0 || ejemplaresPrestados > ejemplares) {
            throw new ErrorServicio("Los ejemplares prestados del libro no pueden ser mayores a la cantidad de ejemplares ni -0");
        }
        if (autor == null) {
            throw new ErrorServicio("El autor del libro no puede ser nulo");
        }
        if (editorial == null) {
            throw new ErrorServicio("La editorial del libro no puede ser nulo");
        }
    }
}
