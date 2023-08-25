package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.AutorRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lucas
 */
@Service
public class AutorServicio {

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Transactional
    public void registrar(String nombre) throws ErrorServicio {
        validar(nombre);

        Autor a = new Autor();
        a.setNombre(nombre);
        a.setAlta(true);

        autorRepositorio.save(a);
    }

    @Transactional
    public void modificar(String id, String nombre) throws ErrorServicio {
        validar(nombre);

        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor a = respuesta.get();
            a.setNombre(nombre);

            autorRepositorio.save(a);
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado");
        }
    }

    @Transactional
    public void eliminar(String idAutor) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);

        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autorRepositorio.delete(autor);
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado para eliminar");
        }
    }

    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor a = respuesta.get();
            a.setAlta(false);

            autorRepositorio.save(a);
        } else {
            throw new ErrorServicio("Error al deshabilitar el autor solicitado");
        }
    }

    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor a = respuesta.get();
            a.setAlta(true);

            autorRepositorio.save(a);
        } else {
            throw new ErrorServicio("Error al habilitar el autor solicitado");
        }
    }

    @Transactional
    public Autor buscarAutorPorId(String id) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            return autor;
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado");
        }
    }

    private void validar(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del autor no puede ser nulo");
        }
    }
}
