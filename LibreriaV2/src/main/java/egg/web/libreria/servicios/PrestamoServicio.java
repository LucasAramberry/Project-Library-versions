package egg.web.libreria.servicios;

import egg.web.libreria.repositorios.PrestamoRepositorio;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lucas
 */
@Service
public class PrestamoServicio {
    
    
    @Autowired
    private PrestamoRepositorio prestamoRespositorio;
    
    @Transactional
    public void registar(){
        
    }
}
