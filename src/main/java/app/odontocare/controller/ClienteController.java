package app.odontocare.controller;

import app.odontocare.model.Cliente;
import app.odontocare.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        Cliente cliente = new Cliente();
        model.addAttribute("cliente", cliente);
        return "cliente/formulario-cliente :: content"; // CORRIGIDO: Retorna o fragmento 'content'
    }

    @PostMapping("/cadastrar")
    public String cadastrarCliente(@ModelAttribute("cliente") /*@Valid*/ Cliente cliente,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        try {
            clienteService.cadastrarCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso!");
            return "redirect:/clientes";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clientes/novo";
        }
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("listaClientes", clienteService.listarTodos());
        return "cliente/lista-clientes :: content"; // CORRIGIDO: Retorna o fragmento 'content'
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOptional = clienteService.buscarPorId(id);
        if (clienteOptional.isPresent()) {
            model.addAttribute("cliente", clienteOptional.get());
            return "cliente/formulario-cliente-edicao :: content"; // CORRIGIDO: Retorna o fragmento 'content'
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente não encontrado.");
            return "redirect:/clientes";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarCliente(@PathVariable Long id,
                                   @ModelAttribute("cliente") /*@Valid*/ Cliente cliente,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        try {
            clienteService.atualizarPerfil(id, cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente atualizado com sucesso!");
            return "redirect:/clientes";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clientes/editar/" + id;
        }
    }

    @GetMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.deletarCliente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente deletado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }
}