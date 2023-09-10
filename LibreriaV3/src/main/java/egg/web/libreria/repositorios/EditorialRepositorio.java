package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Editorial;
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
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {

    // Devuelve una Lista con editoriales activos.
    @Query("SELECT e FROM Editorial e WHERE e.baja IS null")
    public List<Editorial> buscarActivos();

    // Devuelve una Lista con editoriales dados de baja.
    @Query("SELECT e FROM Editorial e WHERE e.baja IS NOT null")
    public List<Editorial> buscarInactivos();

    // Devuelve editorial por nombre
    @Query("SELECT e FROM Editorial e WHERE e.nombre = :nombre")
    public Editorial buscarPorNombre(@Param("nombre") String nombre);
}
