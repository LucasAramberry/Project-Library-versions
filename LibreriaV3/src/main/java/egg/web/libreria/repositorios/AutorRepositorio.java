package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lucas
 */
@Repository
public interface AutorRepositorio extends JpaRepository<Autor, String> {

}
