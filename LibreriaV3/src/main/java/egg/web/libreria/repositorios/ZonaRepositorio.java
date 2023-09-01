package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lucas
 */
@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, Integer> {

}
