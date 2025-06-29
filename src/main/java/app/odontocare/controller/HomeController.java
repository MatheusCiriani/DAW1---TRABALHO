package app.odontocare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // Se precisar passar dados para o template

@Controller
public class HomeController {

    @GetMapping("/") // Mapeia a URL raiz
    public String index(Model model) {
        // Você pode adicionar atributos ao modelo aqui, se precisar
        // model.addAttribute("boasVindas", "Bem-vindo ao OdontoCare!");
        return "home/index"; // Nome do template
    }
}