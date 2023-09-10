package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lucas
 */
@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    // Buscar un usuario por mail y lo devuelve
    @Query("SELECT u FROM Usuario u WHERE u.mail = :mail")
    public Usuario buscarPorMail(@Param("mail") String mail);

    // Devuelve una Lista con Usuarios dados de alta.
    @Query("SELECT u FROM Usuario u WHERE u.baja IS null")
    public List<Usuario> buscarActivos();

    // Devuelve una Lista con Usuarios dados de baja.
    @Query("SELECT u FROM Usuario u WHERE u.baja IS NOT null")
    public List<Usuario> buscarInactivos();
}
