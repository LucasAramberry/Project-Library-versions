package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Usuario;
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

    @Query("SELECT u FROM Usuario u WHERE u.mail = :mail")
    public Usuario buscarPorMail(@Param("mail") String mail);
}
