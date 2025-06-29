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
        return "cliente/formulario-cliente"; // Retorna o template completo sem fragmento para nova entrada
    }

    @PostMapping("/cadastrar")
    public String cadastrarCliente(@ModelAttribute("cliente") /*@Valid*/ Cliente cliente,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        // Validação (se usar @Valid, adicionar 'jakarta.validation.Valid')
        // if (result.hasErrors()) {
        //     return "cliente/formulario-cliente :: content"; // Retorna o fragmento se houver erros
        // }
        try {
            clienteService.cadastrarCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso!");
            return "redirect:/clientes"; // Redireciona para a lista
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clientes/novo"; // Redireciona de volta ao formulário com erro
        }
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("listaClientes", clienteService.listarTodos());
        return "cliente/lista-clientes"; // Retorna o template completo sem fragmento
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOptional = clienteService.buscarPorId(id);
        if (clienteOptional.isPresent()) {
            model.addAttribute("cliente", clienteOptional.get());
            return "cliente/formulario-cliente-edicao"; // Retorna o template de edição
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
        // if (result.hasErrors()) {
        //    cliente.setId(id); // Garante que o ID está presente para o formulário de edição
        //    return "cliente/formulario-cliente-edicao :: content";
        // }
        try {
            clienteService.atualizarPerfil(id, cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente atualizado com sucesso!");
            return "redirect:/clientes";
        } // Adicione um catch para UsernameNotFoundException se for usar Spring Security com UserDetailsService diretamente
        catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clientes/editar/" + id; // Volta para o formulário de edição com erro
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
        return "redirect:/clientes"; // Redireciona de volta para a lista
    }
}