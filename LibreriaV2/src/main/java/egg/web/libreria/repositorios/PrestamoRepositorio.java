package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lucas
 */
@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, Integer> {
    
}
