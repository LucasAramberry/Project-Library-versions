package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Editorial;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.EditorialRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lucas
 */
@Service
public class EditorialServicio {

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void registrar(String nombre) throws ErrorServicio {
        validar(nombre);

        Editorial e = new Editorial();
        e.setNombre(nombre);
        e.setAlta(true);

        editorialRepositorio.save(e);
    }

    @Transactional
    public void modificar(String id, String nombre) throws ErrorServicio {

        validar(nombre);

        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial e = respuesta.get();
            e.setNombre(nombre);

            editorialRepositorio.save(e);
        } else {
            throw new ErrorServicio("No se encontro la editorial solicitado");
        }
    }

    @Transactional
    public void eliminar(String idEditorial) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);

        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorialRepositorio.delete(editorial);
        } else {
            throw new ErrorServicio("No se encontro la editorial solicitada para eliminar");
        }
    }

    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial e = respuesta.get();
            e.setAlta(false);

            editorialRepositorio.save(e);
        } else {
            throw new ErrorServicio("Error al deshabilitar la editorial solicitado");
        }
    }

    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial e = respuesta.get();
            e.setAlta(true);

            editorialRepositorio.save(e);
        } else {
            throw new ErrorServicio("Error al deshabilitar la editorial solicitado");
        }
    }
    
    @Transactional
    public Editorial buscarEditorialPorId(String id) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            return editorial;
        } else {
            throw new ErrorServicio("No se encontro el editorial solicitado");
        }
    }

    private void validar(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre de la editorial no puede ser nulo");
        }
    }
}
