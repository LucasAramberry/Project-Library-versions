package egg.web.libreria.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Lucas
 */
@Controller
@RequestMapping("/")
public class PortalController {
    
    @GetMapping("/")
    public String index(){
        return "index.html";
    }
    
    @GetMapping("/login")
    public String login(){
        return "login.html";
    }
    
    @GetMapping("/registro")
    public String registro(){
        return "registro.html";
    }
}
