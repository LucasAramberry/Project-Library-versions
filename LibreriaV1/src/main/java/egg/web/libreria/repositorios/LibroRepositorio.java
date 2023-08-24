package egg.web.libreria.repositorios;

import egg.web.libreria.entidades.Libro;
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
public interface LibroRepositorio extends JpaRepository<Libro, String>{
    
    @Query("SELECT a FROM Libro a WHERE a.autor.id = :id")
    public List<Libro> buscarPorAutor(@Param("id") String id);
}
