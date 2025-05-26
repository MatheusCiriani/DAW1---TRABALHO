package odontocare.controller;

import odontocare.model.Cliente;
import odontocare.model.Usuario; // Adicionado
import odontocare.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Para validação
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para mensagens flash

// import jakarta.validation.Valid; // Para usar com Bean Validation

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
        cliente.setUsuario(new Usuario()); // Inicializa o usuário para o formulário
        model.addAttribute("cliente", cliente);
        return "cliente/formulario-cliente"; // Ajuste o caminho se necessário
    }

    @PostMapping("/cadastrar")
    public String cadastrarCliente(@ModelAttribute("cliente") /*@Valid*/ Cliente cliente,
                                   BindingResult result, // Para validação
                                   RedirectAttributes redirectAttributes) {
        // if (result.hasErrors()) {
        //     return "cliente/formulario-cliente";
        // }
        try {
            clienteService.cadastrarCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso!");
            return "redirect:/clientes";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Para manter os dados no formulário, você pode adicionar o cliente de volta ao model
            // e retornar para a página do formulário, mas isso é mais complexo com @ModelAttribute
            // e @OneToOne. O mais simples é redirecionar para /novo com a mensagem de erro.
            return "redirect:/clientes/novo";
        }
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("listaClientes", clienteService.listarTodos());
        return "cliente/lista-clientes"; // Ajuste o caminho se necessário
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOptional = clienteService.buscarPorId(id);
        if (clienteOptional.isPresent()) {
            model.addAttribute("cliente", clienteOptional.get());
            return "cliente/formulario-cliente-edicao"; // Crie este HTML
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
        //    // Se o ID não estiver no objeto cliente vindo do form, adicione-o para o formulário de edição
        //    cliente.setId(id);
        //    return "cliente/formulario-cliente-edicao";
        // }
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