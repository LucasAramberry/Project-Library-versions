package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lucas
 */
@Repository
public interface FotoRepositorio extends JpaRepository<Foto, String> {

}
