package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lucas
 */
@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {

}
