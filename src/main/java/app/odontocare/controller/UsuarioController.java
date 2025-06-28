package app.odontocare.controller; // PACOTE CORRIGIDO

import app.odontocare.service.UsuarioService; // PACOTE CORRIGIDO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
// @RequestMapping("/usuarios") // Ou direto na raiz para login
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Endpoint para htmx (exemplo de verificação de login/email)
    @PostMapping("/api/usuarios/verificar-identificador")
    @ResponseBody
    public String verificarIdentificador(@RequestParam String identificador) {
        boolean existe = usuarioService.emailOuLoginExiste(identificador); // Ajustar lógica no service
        if (existe) {
            return "<span class='text-red-500 text-xs'>Identificador já em uso!</span>";
        } else {
            return "<span class='text-green-500 text-xs'>Disponível.</span>";
        }
    }

    @GetMapping("/login")
    public String paginaLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("loginError", "Login ou senha inválidos.");
        }
        return "auth/login";
    }

    // Endpoints para POST de login e logout são geralmente gerenciados pelo Spring Security
    // Você também pode ter um formulário de registro de usuário aqui
}