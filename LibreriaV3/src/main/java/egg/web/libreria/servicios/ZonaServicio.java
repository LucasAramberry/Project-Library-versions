package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Zona;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.ZonaRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lucas
 */
@Service
public class ZonaServicio {

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    /**
     * metodo para agregar zona
     *
     * @param nombre
     * @param descripcion
     * @throws ErrorServicio
     */
    @Transactional
    public void agregar(String nombre, String descripcion) throws ErrorServicio {
        validar(nombre, descripcion);

        Zona z = new Zona();
        z.setNombre(nombre);
        z.setDescripcion(descripcion);

        zonaRepositorio.save(z);
    }

    /**
     * metodo para modificar zona
     * @param id
     * @param nombre
     * @param descripcion
     * @throws ErrorServicio 
     */
    @Transactional
    public void modificar(String id, String nombre, String descripcion) throws ErrorServicio {
        validar(nombre, descripcion);

        Optional<Zona> respuesta = zonaRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Zona z = respuesta.get();
            z.setNombre(nombre);
            z.setDescripcion(descripcion);

            zonaRepositorio.save(z);
        } else {
            throw new ErrorServicio("No se encontro la zona solicitado");
        }
    }

    /**
     * metodo para eliminar autor
     * @param id
     * @throws ErrorServicio 
     */
    @Transactional
    public void eliminar(String id) throws ErrorServicio {
        Optional<Zona> respuesta = zonaRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Zona z = respuesta.get();
            zonaRepositorio.delete(z);
        } else {
            throw new ErrorServicio("No se encontro la zona solicitado para eliminar");
        }
    }

    /**
     * metodo para buscar zona por id
     *
     * @param id
     * @return
     * @throws ErrorServicio
     */
    @Transactional
    public Zona buscarZonaPorId(String id) throws ErrorServicio {
        Optional<Zona> respuesta = zonaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Zona zona = respuesta.get();
            return zona;
        } else {
            throw new ErrorServicio("No se encontro la zona solicitada");
        }
    }
    
    /**
     * Metodo para validar atributos
     * @param nombre
     * @param descripcion
     * @throws ErrorServicio 
     */
    private void validar(String nombre, String descripcion) throws ErrorServicio{
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre de la zona no puede ser nulo");
        }
//        if (descripcion == null || descripcion.isEmpty()) {
//            throw new ErrorServicio("La descripcion de la zona no puede ser nulo");
//        }
    }
}
