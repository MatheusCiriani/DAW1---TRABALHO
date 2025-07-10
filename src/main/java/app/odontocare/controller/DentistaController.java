package app.odontocare.controller;

import app.odontocare.model.Dentista;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
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

    // ✅ MANTENHA APENAS ESTA VERSÃO DO MÉTODO
    @GetMapping
    public String listarDentistas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "nomeAdm") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String nome,
            Model model) {

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, 5, Sort.by(direction, sort));

        // Supondo que seu service foi atualizado para receber o nome
        Page<Dentista> pagina = dentistaService.listarPaginado(nome, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        model.addAttribute("nome", nome);
        model.addAttribute("reverseOrder", "asc".equals(order) ? "desc" : "asc");

        return "dentista/lista-dentistas";
    }

    // O método abaixo foi removido para evitar duplicidade.

    @GetMapping("/novo")
    public String mostrarFormularioCadastroDentista(Model model) {
        Dentista dentista = new Dentista();
        model.addAttribute("dentista", dentista);
        return "dentista/formulario-dentista";
    }

    @PostMapping("/cadastrar")
    public String cadastrarDentista(@ModelAttribute("dentista") Dentista dentista,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Supondo que o método cadastrarDentista exista no service
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
            return "dentista/formulario-dentista-edicao";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Dentista não encontrado.");
            return "redirect:/dentistas";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarDentista(@PathVariable Long id,
                                    @ModelAttribute("dentista") Dentista dentista,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Supondo que o método atualizarDentista exista no service
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
            // Supondo que o método deletarDentista exista no service
            dentistaService.deletarDentista(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dentista deletado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar dentista: " + e.getMessage());
        }
        return "redirect:/dentistas";
    }

    @GetMapping("/{id}/agenda")
    public String visualizarAgenda(@PathVariable Long id,
                                   @RequestParam(name = "data", required = false) String dataStr,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Dentista> dentistaOptional = dentistaService.buscarPorId(id);
        if (dentistaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dentista não encontrado.");
            return "redirect:/dentistas";
        }

        Dentista dentista = dentistaOptional.get();
        model.addAttribute("dentista", dentista);

        LocalDate dataSelecionada = (dataStr != null && !dataStr.isEmpty()) ? LocalDate.parse(dataStr) : LocalDate.now();
        model.addAttribute("dataSelecionada", dataSelecionada);

        try {
            List<LocalTime> horariosDisponiveis = dentistaService.listarHorariosDisponiveis(id, dataSelecionada);
            model.addAttribute("horariosDisponiveis", horariosDisponiveis);
            return "dentista/agenda-dentista";
        } catch (RuntimeException e) {
            model.addAttribute("agendaErrorMessage", "Erro ao carregar agenda: " + e.getMessage());
            return "dentista/agenda-dentista";
        }
    }
}