package odontocare.controller;

import odontocare.model.Consulta;
import odontocare.service.ConsultaService;
import odontocare.service.ClienteService; // Para listar clientes no formulário
import odontocare.service.DentistaService; // Para listar dentistas no formulário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final ClienteService clienteService;   // Para popular dropdown de clientes
    private final DentistaService dentistaService; // Para popular dropdown de dentistas

    @Autowired
    public ConsultaController(ConsultaService consultaService, ClienteService clienteService, DentistaService dentistaService) {
        this.consultaService = consultaService;
        this.clienteService = clienteService;
        this.dentistaService = dentistaService;
    }

    @GetMapping
    public String listarConsultas(Model model) {
        model.addAttribute("listaConsultas", consultaService.listarTodas());
        return "consulta/lista-consultas"; // Crie este HTML
    }

    @GetMapping("/novo")
    public String mostrarFormularioAgendamento(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("listaClientes", clienteService.listarTodos()); // Para o <select> de clientes
        model.addAttribute("listaDentistas", dentistaService.listarTodos()); // Para o <select> de dentistas
        return "consulta/formulario-consulta"; // Crie este HTML
    }

    @PostMapping("/agendar")
    public String agendarConsulta(@ModelAttribute("consulta") /*@Valid*/ Consulta consulta,
                                  BindingResult result,
                                  Model model, // Para repopular dropdowns em caso de erro
                                  RedirectAttributes redirectAttributes) {
        // if (result.hasErrors()) {
        //     model.addAttribute("listaClientes", clienteService.listarTodos());
        //     model.addAttribute("listaDentistas", dentistaService.listarTodos());
        //     return "consulta/formulario-consulta";
        // }
        try {
            // A lógica de associar Cliente e Dentista com base nos IDs do formulário
            // pode precisar ser feita aqui ou no service antes de chamar `agendarConsulta`.
            // Ex: Cliente cliente = clienteService.buscarPorId(consulta.getCliente().getId()).orElse(null);
            //     Dentista dentista = dentistaService.buscarPorId(consulta.getDentista().getId()).orElse(null);
            //     consulta.setCliente(cliente);
            //     consulta.setDentista(dentista);

            consultaService.agendarConsulta(consulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta agendada com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Repopular para o formulário
            // model.addAttribute("listaClientes", clienteService.listarTodos());
            // model.addAttribute("listaDentistas", dentistaService.listarTodos());
            // return "consulta/formulario-consulta";
            // Ou redirecionar para /novo
            return "redirect:/consultas/novo";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicaoConsulta(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Consulta> consultaOptional = consultaService.buscarPorId(id);
        if (consultaOptional.isPresent()) {
            model.addAttribute("consulta", consultaOptional.get());
            model.addAttribute("listaClientes", clienteService.listarTodos());
            model.addAttribute("listaDentistas", dentistaService.listarTodos());
            return "consulta/formulario-consulta-edicao"; // Crie este HTML
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada.");
            return "redirect:/consultas";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarConsulta(@PathVariable Long id,
                                    @ModelAttribute("consulta") /*@Valid*/ Consulta consulta,
                                    BindingResult result,
                                    Model model, // Para repopular dropdowns
                                    RedirectAttributes redirectAttributes) {
        consulta.setId(id); // Garanta que o ID está no objeto
        // if (result.hasErrors()) {
        //     model.addAttribute("listaClientes", clienteService.listarTodos());
        //     model.addAttribute("listaDentistas", dentistaService.listarTodos());
        //     return "consulta/formulario-consulta-edicao";
        // }
        try {
            // Similar ao agendamento, pode precisar carregar Cliente e Dentista
            consultaService.atualizarConsulta(consulta); // Supondo que este método exista no service
            redirectAttributes.addFlashAttribute("successMessage", "Consulta atualizada com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/consultas/editar/" + id;
        }
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarConsulta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            consultaService.cancelarConsulta(id);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta cancelada com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cancelar consulta: " + e.getMessage());
        }
        return "redirect:/consultas";
    }

    @GetMapping("/finalizar/{id}")
    public String finalizarConsulta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            consultaService.finalizarConsulta(id);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta finalizada com sucesso!");
            // Poderia redirecionar para a página de pagamento ou detalhes da consulta
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao finalizar consulta: " + e.getMessage());
        }
        return "redirect:/consultas";
    }
}