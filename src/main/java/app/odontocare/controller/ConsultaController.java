package app.odontocare.controller;

import app.odontocare.model.Consulta;
import app.odontocare.service.ConsultaService;
import app.odontocare.service.ClienteService;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final ClienteService clienteService;
    private final DentistaService dentistaService;

    @Autowired
    public ConsultaController(ConsultaService consultaService, ClienteService clienteService, DentistaService dentistaService) {
        this.consultaService = consultaService;
        this.clienteService = clienteService;
        this.dentistaService = dentistaService;
    }

    @GetMapping
    public String listarConsultas(Model model) {
        model.addAttribute("listaConsultas", consultaService.listarTodas());
        return "consulta/lista-consultas :: content"; // CORRIGIDO
    }

    @GetMapping("/novo")
    public String mostrarFormularioAgendamento(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("listaClientes", clienteService.listarTodos());
        model.addAttribute("listaDentistas", dentistaService.listarTodos());
        return "consulta/formulario-consulta :: content"; // CORRIGIDO
    }

    @PostMapping("/agendar")
    public String agendarConsulta(@ModelAttribute("consulta") /*@Valid*/ Consulta consulta,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            consultaService.agendarConsulta(consulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta agendada com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
            return "consulta/formulario-consulta-edicao :: content"; // CORRIGIDO
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada.");
            return "redirect:/consultas";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarConsulta(@PathVariable Long id,
                                    @ModelAttribute("consulta") /*@Valid*/ Consulta consulta,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        consulta.setId(id);
        try {
            consultaService.atualizarConsulta(consulta);
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
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao finalizar consulta: " + e.getMessage());
        }
        return "redirect:/consultas";
    }
}