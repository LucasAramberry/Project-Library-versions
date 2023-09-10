package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Editorial;
import egg.web.libreria.entidades.Foto;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.EditorialRepositorio;
import java.util.Date;
import java.util.List;
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
public class EditorialServicio {

    @Autowired
    private EditorialRepositorio editorialRepositorio;
    @Autowired
    private FotoServicio fotoServicio;

    /**
     * metodo para registrar editorial
     * @param archivo
     * @param nombre
     * @throws ErrorServicio 
     */
    @Transactional
    public void registrar(MultipartFile archivo, String nombre) throws ErrorServicio {
        validar(nombre);

        Editorial e = new Editorial();
        e.setNombre(nombre);
        e.setAlta(new Date());
        
        Foto foto = fotoServicio.guardar(archivo);
        e.setFoto(foto);

        editorialRepositorio.save(e);
    }

    /**
     * metodo para modificar editorial
     * @param archivo
     * @param id
     * @param nombre
     * @throws ErrorServicio 
     */
    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre) throws ErrorServicio {

        validar(nombre);

        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial e = respuesta.get();
            e.setNombre(nombre);

            String idFoto = null;
            if (e.getFoto() != null) {
                idFoto = e.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            e.setFoto(foto);
            
            editorialRepositorio.save(e);
        } else {
            throw new ErrorServicio("No se encontro la editorial solicitada para modificar.");
        }
    }

    /**
     * metodo para eliminar editorial
     * @param idEditorial
     * @throws ErrorServicio 
     */
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

    /**
     * Metodo para deshabilitar editorial
     * @param id
     * @throws ErrorServicio 
     */
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial e = respuesta.get();
            e.setBaja(new Date());

            editorialRepositorio.save(e);
        } else {
            throw new ErrorServicio("Error al deshabilitar la editorial solicitada.");
        }
    }

    /**
     * Metodo para habilitar editorial
     * @param id
     * @throws ErrorServicio 
     */
    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial e = respuesta.get();
            e.setBaja(null);

            editorialRepositorio.save(e);
        } else {
            throw new ErrorServicio("Error al deshabilitar la editorial solicitada.");
        }
    }
    
    /**
     * metodo para buscar editorial por id
     * @param id
     * @return
     * @throws ErrorServicio 
     */
    @Transactional
    public Editorial buscarEditorialPorId(String id) throws ErrorServicio {
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            return editorial;
        } else {
            throw new ErrorServicio("No se encontro la editorial solicitada por id.");
        }
    }

    
    @Transactional
    public List<Editorial> listarEditoriales() throws ErrorServicio {
        List<Editorial> editoriales = editorialRepositorio.findAll();
        if (editoriales != null) {
            return editoriales;
        }else{
            throw new ErrorServicio("No se encontro ninguna editorial.");
        }
    }
    
    /**
     * Metodo para validar atributos
     * @param nombre
     * @throws ErrorServicio 
     */
    private void validar(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre de la editorial no puede ser nulo");
        }
    }
}
