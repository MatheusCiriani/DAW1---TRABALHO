package app.odontocare.controller;

import app.odontocare.model.Agenda;
import app.odontocare.model.Dentista;
import app.odontocare.service.AgendaService;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/agendas")
public class AgendaController {

    private final AgendaService agendaService;
    private final DentistaService dentistaService;

    @Autowired
    public AgendaController(AgendaService agendaService, DentistaService dentistaService) {
        this.agendaService = agendaService;
        this.dentistaService = dentistaService;
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastroAgenda(Agenda agenda, Model model) {
        model.addAttribute("agenda", agenda);
        model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas()); // MÉTODO RENOMEADO
        model.addAttribute("diasDaSemana", DayOfWeek.values());
        return "agenda/formulario-agenda";
    }

    @PostMapping("/cadastrar")
    public String cadastrarAgenda(@ModelAttribute("agenda") /*@Valid*/ Agenda agenda,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
            try {
                agendaService.cadastrarAgenda(agenda);
                redirectAttributes.addFlashAttribute("successMessage", "Horário de trabalho cadastrado com sucesso!");
                return "redirect:/agendas";
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas()); // MÉTODO RENOMEADO
                model.addAttribute("diasDaSemana", DayOfWeek.values());
                return "agenda/formulario-agenda";
            }
        }

    // ✅ MÉTODO ATUALIZADO PARA BUSCA E ORDENAÇÃO
    @GetMapping
    public String listarAgendas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "dentista.nomeAdm") String sort, // Ordenação padrão
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String nome, // Nome do dentista para buscar
            Model model) {

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, 5, Sort.by(direction, sort));

        Page<Agenda> pagina = agendaService.listarPaginado(nome, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        model.addAttribute("nome", nome);
        model.addAttribute("reverseOrder", "asc".equals(order) ? "desc" : "asc");

        return "agenda/lista-agendas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicaoAgenda(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Agenda> agendaOptional = agendaService.buscarPorId(id);
        if (agendaOptional.isPresent()) {
            model.addAttribute("agenda", agendaOptional.get());
            model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas()); // MÉTODO RENOMEADO
            model.addAttribute("diasDaSemana", DayOfWeek.values());
            return "agenda/formulario-agenda-edicao";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Horário de trabalho não encontrado.");
            return "redirect:/agendas";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarAgenda(@PathVariable Long id,
                                      @ModelAttribute("agenda") /*@Valid*/ Agenda agenda,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
            try {
                agendaService.atualizarAgenda(id, agenda);
                redirectAttributes.addFlashAttribute("successMessage", "Horário de trabalho atualizado com sucesso!");
                return "redirect:/agendas";
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas()); // MÉTODO RENOMEADO
                model.addAttribute("diasDaSemana", DayOfWeek.values());
                return "agenda/formulario-agenda-edicao";
            }
        }

    @GetMapping("/deletar/{id}")
    public String deletarAgenda(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            agendaService.deletarAgenda(id);
            redirectAttributes.addFlashAttribute("successMessage", "Horário de trabalho deletado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar horário de trabalho: " + e.getMessage());
        }
        return "redirect:/agendas";
    }

}