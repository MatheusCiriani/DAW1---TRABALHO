package app.odontocare.controller;

import app.odontocare.model.Dentista;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dentistas")
public class DentistaController {

    private final DentistaService dentistaService;

    @Autowired
    public DentistaController(DentistaService dentistaService) {
        this.dentistaService = dentistaService;
    }

    @GetMapping
    public String listarDentistas(Model model) {
        model.addAttribute("listaDentistas", dentistaService.listarTodos());
        return "dentista/lista-dentistas :: content"; // CORRIGIDO
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastroDentista(Model model) {
        Dentista dentista = new Dentista();
        model.addAttribute("dentista", dentista);
        return "dentista/formulario-dentista :: content"; // CORRIGIDO
    }

    @PostMapping("/cadastrar")
    public String cadastrarDentista(@ModelAttribute("dentista") /*@Valid*/ Dentista dentista,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        try {
            dentistaService.cadastrarDentista(dentista);
            redirectAttributes.addFlashAttribute("successMessage", "Dentista cadastrado com sucesso!");
            return "redirect:/dentistas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dentistas/novo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicaoDentista(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Dentista> dentistaOptional = dentistaService.buscarPorId(id);
        if (dentistaOptional.isPresent()) {
            model.addAttribute("dentista", dentistaOptional.get());
            return "dentista/formulario-dentista-edicao :: content"; // CORRIGIDO
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Dentista não encontrado.");
            return "redirect:/dentistas";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarDentista(@PathVariable Long id,
                                    @ModelAttribute("dentista") /*@Valid*/ Dentista dentista,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        try {
            dentistaService.atualizarDentista(id, dentista);
            redirectAttributes.addFlashAttribute("successMessage", "Dentista atualizado com sucesso!");
            return "redirect:/dentistas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dentistas/editar/" + id;
        }
    }

    @GetMapping("/deletar/{id}")
    public String deletarDentista(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dentistaService.deletarDentista(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dentista deletado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar dentista: " + e.getMessage());
        }
        return "redirect:dentistas";
    }

    @GetMapping("/{id}/agenda")
    public String visualizarAgenda(@PathVariable Long id,
                                   @RequestParam(name = "data", required = false) String dataStr,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Dentista> dentistaOptional = dentistaService.buscarPorId(id);
        if (!dentistaOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dentista não encontrado.");
            return "redirect:/dentistas";
        }

        Dentista dentista = dentistaOptional.get();
        model.addAttribute("dentista", dentista);

        LocalDate dataSelecionada = (dataStr != null && !dataStr.isEmpty()) ? LocalDate.parse(dataStr) : LocalDate.now();
        model.addAttribute("dataSelecionada", dataSelecionada);

        try {
            List<String> horariosDisponiveis = dentistaService.listarHorariosDisponiveis(id, dataSelecionada);
            model.addAttribute("horariosDisponiveis", horariosDisponiveis);
            return "dentista/agenda-dentista :: content"; // CORRIGIDO (se este for o template para agendamento do dentista)
        } catch (RuntimeException e) {
            model.addAttribute("agendaErrorMessage", "Erro ao carregar agenda: " + e.getMessage());
            return "dentista/agenda-dentista :: content"; // CORRIGIDO
        }
    }
}