package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Autor;
import egg.web.libreria.entidades.Foto;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.AutorRepositorio;
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
public class AutorServicio {

    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private FotoServicio fotoServicio;

    /**
     * metodo para registar autor
     *
     * @param archivo
     * @param nombre
     * @throws ErrorServicio
     */
    @Transactional
    public void registrar(MultipartFile archivo, String nombre) throws ErrorServicio {
        validar(nombre);

        Autor a = new Autor();
        a.setNombre(nombre);
        a.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        a.setFoto(foto);

        autorRepositorio.save(a);
    }

    /**
     * metodo para modificar autor
     *
     * @param archivo
     * @param id
     * @param nombre
     * @throws ErrorServicio
     */
    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre) throws ErrorServicio {
        validar(nombre);

        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor a = respuesta.get();
            a.setNombre(nombre);

            String idFoto = null;
            if (a.getFoto() != null) {
                idFoto = a.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            a.setFoto(foto);

            autorRepositorio.save(a);
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado para modificar.");
        }
    }

    /**
     * metodo para eliminar autor
     *
     * @param idAutor
     * @throws ErrorServicio
     */
    @Transactional
    public void eliminar(String idAutor) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(idAutor);

        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autorRepositorio.delete(autor);
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado para eliminar.");
        }
    }

    /**
     * metodo para deshabilitar autor
     *
     * @param id
     * @throws ErrorServicio
     */
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor a = respuesta.get();
            a.setBaja(new Date());

            autorRepositorio.save(a);
        } else {
            throw new ErrorServicio("Error al deshabilitar el autor solicitado.");
        }
    }

    /**
     * Metodo para habilitar autor
     *
     * @param id
     * @throws ErrorServicio
     */
    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor a = respuesta.get();
            a.setBaja(null);

            autorRepositorio.save(a);
        } else {
            throw new ErrorServicio("Error al habilitar el autor solicitado.");
        }
    }

    /**
     * metodo para buscar autor por id
     *
     * @param id
     * @return
     * @throws ErrorServicio
     */
    @Transactional
    public Autor buscarAutorPorId(String id) throws ErrorServicio {
        Optional<Autor> respuesta = autorRepositorio.findById(id);//autorRepositorio.getOne(id)//autorRepositorio.getById(id)
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            return autor;
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado por id.");
        }
    }

    /**
     * metodo para buscar autor por nombre
     *
     * @param nombre
     * @return
     * @throws ErrorServicio
     */
    @Transactional
    public Autor buscarAutorPorNombre(String nombre) throws ErrorServicio {
        Autor autor = autorRepositorio.buscarPorNombre(nombre);
        if (autor != null) {
            return autor;
        } else {
            throw new ErrorServicio("No se encontro el autor solicitado por nombre.");
        }
    }

    /**
     * Metodo para listar autores
     *
     * @return
     */
    @Transactional
    public List<Autor> listarAutores() {
        return autorRepositorio.findAll();
    }

    /**
     * Metodo para listar autores activos
     *
     * @return
     */
    @Transactional
    public List<Autor> listaAutoresActivos() {
        return autorRepositorio.buscarActivos();
    }

    /**
     * Metodo para listar autores inactivos
     *
     * @return
     */
    @Transactional
    public List<Autor> listaAutoresInactivos() {
        return autorRepositorio.buscarInactivos();
    }

    /**
     * metodo para validar atributos
     *
     * @param nombre
     * @throws ErrorServicio
     */
    private void validar(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del autor no puede ser nulo.");
        }
    }
}
