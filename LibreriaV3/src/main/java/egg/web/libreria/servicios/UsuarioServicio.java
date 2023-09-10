package egg.web.libreria.servicios;

import egg.web.libreria.entidades.*;
import egg.web.libreria.enumeraciones.Rol;
import egg.web.libreria.enumeraciones.Sexo;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.repositorios.UsuarioRepositorio;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Lucas
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ZonaServicio zonaServicio;
    @Autowired
    private FotoServicio fotoServicio;
    @Autowired
    private NotificacionServicio notificacionServicio;

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
    public void registar(MultipartFile archivo, String nombre, String apellido, String documento, String telefono, Sexo sexo, String idZona, String mail, String clave, String clave2) throws ErrorServicio {

        validar(nombre, apellido, documento, telefono, mail, clave, clave2, sexo);

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDocumento(documento);
        usuario.setTelefono(telefono);
        usuario.setSexo(sexo);
        usuario.setRol(Rol.USUARIO);

        Zona zona = zonaServicio.buscarZonaPorId(idZona);
        usuario.setZona(zona);

        usuario.setMail(mail);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);

        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

//        notificacionServicio.enviar("Bienvenidos a la Libreria!", "Libreria web", usuario.getMail());
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
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String documento, String telefono, Sexo sexo, String idZona, String mail, String clave, String clave2) throws ErrorServicio {

        validar(nombre, apellido, documento, telefono, mail, clave, clave2, sexo);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDocumento(documento);
            usuario.setTelefono(telefono);
            usuario.setSexo(sexo);

            Zona zona = zonaServicio.buscarZonaPorId(idZona);
            usuario.setZona(zona);

            usuario.setMail(mail);

            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado para modificar.");
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
            throw new ErrorServicio("No se encontro el usuario solicitado para eliminar.");
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
     * Metodo para buscar usuario por id
     *
     * @param id
     * @return
     * @throws ErrorServicio
     */
    @Transactional
    public Usuario buscarUsuarioPorId(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            return usuario;
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
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
    private void validar(String nombre, String apellido, String documento, String telefono, String mail, String clave, String clave2, Sexo sexo) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo.");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido no puede ser nulo.");
        }
        if (documento == null || documento.isEmpty() || (documento.length() < 7 || documento.length() > 10)) {
            throw new ErrorServicio("El documento no puede ser nulo.");
        }
        if (telefono == null || telefono.isEmpty() || telefono.length() != 10) {
            throw new ErrorServicio("El telefono no puede ser nulo.");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El email no puede ser nulo.");
        }
        if (clave == null || clave.isEmpty() || clave.length() < 6) {
            throw new ErrorServicio("Clave invalida. Debe contener al menos 6 digitos.");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las contraseñas deben ser iguales.");
        }
        if (sexo == null) {
            throw new ErrorServicio("El sexo no puede ser nulo.");
        }
    }

    @Transactional
    public Usuario getById(String id) {
        return usuarioRepositorio.getById(id);
    }

    @Transactional
    public Usuario getOne(String id) {
        return usuarioRepositorio.getOne(id);
    }

    @Transactional
    public List<Usuario> findAll() {
        return usuarioRepositorio.findAll();
    }

    @Transactional
    public List<Usuario> buscarActivos() {
        return usuarioRepositorio.buscarActivos();
    }

    @Transactional
    public List<Usuario> buscarInactivos() {
        return usuarioRepositorio.buscarInactivos();
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());
            permisos.add(p1);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }
}
