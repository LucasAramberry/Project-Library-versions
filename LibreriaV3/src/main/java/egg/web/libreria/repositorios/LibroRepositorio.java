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
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    // Método que devuelve el/los Libro/s vinculado a un Autor:
    @Query("SELECT a FROM Libro a WHERE a.autor.id = :id")
    public List<Libro> buscarLibrosPorAutor(@Param("id") String id);

    // Método que devuelve el/los Libro/s vinculado a una Editorial:
    @Query("SELECT a FROM Libro a WHERE a.editorial.id = :id")
    public List<Libro> buscarLibrosPorEditorial(@Param("id") String id);

    // Método que devuelve los libros activos
    @Query("SELECT a FROM Libro a WHERE a.baja IS null")
    public List<Libro> listaLibrosActivos();

    // Método que devuelve los libros inactivos
    @Query("SELECT a FROM Libro a WHERE a.baja IS NOT null")
    public List<Libro> listaLibrosInactivos();
}
