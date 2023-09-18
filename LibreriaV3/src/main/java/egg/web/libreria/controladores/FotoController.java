package egg.web.libreria.controladores;

import egg.web.libreria.entidades.*;
import egg.web.libreria.errores.ErrorServicio;
import egg.web.libreria.servicios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Lucas
 */
@Controller
@RequestMapping("/foto")
public class FotoController {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]> fotoUsuario(@PathVariable String id) {
        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            if (usuario.getFoto() == null) {
                throw new ErrorServicio("El usuario no tiene una foto asignada.");
            }

            byte[] foto = usuario.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (ErrorServicio ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/libro/{id}")
    public ResponseEntity<byte[]> fotoLibro(@PathVariable String id) {
        try {
            Libro libro = libroServicio.buscarLibroPorId(id);
            if (libro.getFoto() == null) {
                throw new ErrorServicio("El libro no tiene una foto asignada.");
            }

            byte[] foto = libro.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (ErrorServicio ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/autor/{id}")
    public ResponseEntity<byte[]> fotoAutor(@PathVariable String id) {
        try {
            Autor autor = autorServicio.buscarAutorPorId(id);
            if (autor.getFoto() == null) {
                throw new ErrorServicio("El autor no tiene una foto asignada.");
            }

            byte[] foto = autor.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (ErrorServicio ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/editorial/{id}")
    public ResponseEntity<byte[]> fotoEditorial(@PathVariable String id) {
        try {
            Editorial editorial = editorialServicio.buscarEditorialPorId(id);
            if (editorial.getFoto() == null) {
                throw new ErrorServicio("El editorial no tiene una foto asignada.");
            }

            byte[] foto = editorial.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (ErrorServicio ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
