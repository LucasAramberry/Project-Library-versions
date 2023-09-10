package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Autor;
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
public interface AutorRepositorio extends JpaRepository<Autor, String> {

    // Devuelve una Lista con autores activos.
    @Query("SELECT a FROM Autor a WHERE a.baja IS null")
    public List<Autor> buscarActivos();

    // Devuelve una Lista con autores dados de baja.
    @Query("SELECT a FROM Autor a WHERE a.baja IS NOT null")
    public List<Autor> buscarInactivos();
    
    // Devuelve autor por nombre
    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre")
    public Autor buscarPorNombre(@Param("nombre") String nombre);
}
