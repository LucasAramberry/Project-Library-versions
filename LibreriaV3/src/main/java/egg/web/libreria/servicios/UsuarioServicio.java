package egg.web.libreria.servicios;

import egg.web.libreria.entidades.Foto;
import egg.web.libreria.entidades.Usuario;
import egg.web.libreria.entidades.Zona;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.UsuarioRepositorio;
import egg.web.libreria.repositorios.ZonaRepositorio;
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
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ZonaRepositorio zonaRepositorio;
    @Autowired
    private FotoServicio fotoServicio;

    /**
     * Metodo para registrar usuario
     *
     * @param nombre
     * @param apellido
     * @param documento
     * @param telefono
     * @param sexo
     * @param idZona
     * @param mail
     * @param clave
     * @throws ErrorServicio
     */
    @Transactional
    public void registar(MultipartFile archivo, String nombre, String apellido, String documento, String telefono, Sexo sexo, Integer idZona, String mail, String clave) throws ErrorServicio {

        validar(nombre, apellido, documento, telefono, mail, clave, sexo);

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDocumento(documento);
        usuario.setTelefono(telefono);
        usuario.setSexo(sexo);

        //buscar zona por id crear metodo en zona servicio que envie excepcion si no decuelve nada
        Optional<Zona> respuesta = zonaRepositorio.findById(idZona);
        if (respuesta.isPresent()) {
            Zona zona = respuesta.get();
            usuario.setZona(zona);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado para modificar");
        }

        usuario.setMail(mail);
        usuario.setClave(clave);
        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);
    }

    /**
     * Metodo para modificar usuario
     *
     * @param id
     * @param nombre
     * @param apellido
     * @param documento
     * @param telefono
     * @param sexo
     * @param idZona
     * @param mail
     * @param clave
     * @throws ErrorServicio
     */
    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String documento, String telefono, Sexo sexo, Integer idZona, String mail, String clave) throws ErrorServicio {

        validar(nombre, apellido, documento, telefono, mail, clave, sexo);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDocumento(documento);
            usuario.setTelefono(telefono);
            usuario.setSexo(sexo);

            //buscar zona por id crear metodo en zona servicio que envie excepcion si no decuelve nada
            Optional<Zona> respuesta1 = zonaRepositorio.findById(idZona);
            if (respuesta1.isPresent()) {
                Zona zona = respuesta1.get();
                usuario.setZona(zona);
            } else {
                throw new ErrorServicio("No se encontro el usuario solicitado para modificar");
            }

            usuario.setMail(mail);
            usuario.setClave(clave);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado para modificar");
        }
    }

    /**
     * Metodo para eliminar usuario
     *
     * @param idUsuario
     * @throws ErrorServicio
     */
    @Transactional
    public void eliminar(String idUsuario) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuarioRepositorio.delete(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado para eliminar");
        }
    }

    /**
     * Metodo para deshabilitar usuario
     *
     * @param id
     * @throws ErrorServicio
     */
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("Error al deshabilitar el usuario solicitado");
        }
    }

    /**
     * Metodo para habilitar usuario
     *
     * @param id
     * @throws ErrorServicio
     */
    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario cliente = respuesta.get();
            cliente.setBaja(null);

            usuarioRepositorio.save(cliente);
        } else {
            throw new ErrorServicio("Error al habilitar el usuario solicitado");
        }
    }

    /**
     * Metodo para buscar usuario por mail
     *
     * @param mail
     * @return
     * @throws ErrorServicio
     */
    @Transactional
    public Usuario buscarUsuarioPorMail(String mail) throws ErrorServicio {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if (usuario != null) {
            return usuario;
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    /**
     * Metodo para validar atributos ingresados
     *
     * @param nombre
     * @param apellido
     * @param documento
     * @param telefono
     * @param mail
     * @param clave
     * @param sexo
     * @throws ErrorServicio
     */
    private void validar(String nombre, String apellido, String documento, String telefono, String mail, String clave, Sexo sexo) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo.");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido no puede ser nulo.");
        }
        if (documento == null || documento.isEmpty() || (documento.length() < 7 || documento.length() > 10)) {
            throw new ErrorServicio("El documento no puede ser nulo.");
        }
        if (telefono == null || telefono.isEmpty() || telefono.length() < 10 || telefono.length() > 10) {
            throw new ErrorServicio("El telefono no puede ser nulo.");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail no puede ser nulo.");
        }
        if (clave == null || clave.isEmpty() || clave.length() < 6) {
            throw new ErrorServicio("Clave invalida. La clave debe ser contener al menos 6 digitos.");
        }
        if (sexo == null) {
            throw new ErrorServicio("El sexo no puede ser nulo.");
        }
    }
}
