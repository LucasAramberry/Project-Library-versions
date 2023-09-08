package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Prestamo;
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
public interface PrestamoRepositorio extends JpaRepository<Prestamo, Integer> {
    
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :id")
    public List<Prestamo> buscarPrestamoPorUsuario(@Param("id") String id);
}
