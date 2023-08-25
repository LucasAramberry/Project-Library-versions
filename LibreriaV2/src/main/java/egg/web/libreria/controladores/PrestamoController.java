package egg.web.libreria.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Lucas
 */
@Controller
@RequestMapping("/prestamos")
public class PrestamoController {
    
    @GetMapping("/mostrar")
    public String mostrar(){
        
        return "prestamos.html";
    }
}
