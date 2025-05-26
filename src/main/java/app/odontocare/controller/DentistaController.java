package odontocare.controller;

import odontocare.model.Dentista;
import odontocare.model.Usuario; // Para o formulário
import odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import jakarta.validation.Valid; // Se for usar Bean Validation
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
        return "dentista/lista-dentistas"; // Crie este HTML
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastroDentista(Model model) {
        Dentista dentista = new Dentista();
        dentista.setUsuario(new Usuario()); // Inicializa usuário para o formulário
        model.addAttribute("dentista", dentista);
        return "dentista/formulario-dentista"; // Crie este HTML
    }

    @PostMapping("/cadastrar")
    public String cadastrarDentista(@ModelAttribute("dentista") /*@Valid*/ Dentista dentista,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        // if (result.hasErrors()) {
        //     // Se o usuário não foi preenchido no form, reinicialize para não dar erro de null no template
        //     if (dentista.getUsuario() == null) {
        //         dentista.setUsuario(new Usuario());
        //     }
        //     return "dentista/formulario-dentista";
        // }
        try {
            dentistaService.cadastrarDentista(dentista);
            redirectAttributes.addFlashAttribute("successMessage", "Dentista cadastrado com sucesso!");
            return "redirect:/dentistas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Para manter os dados no formulário:
            // model.addAttribute("dentista", dentista); // Precisa do Model como parâmetro do método
            // return "dentista/formulario-dentista";
            // Ou redirecionar para /novo:
            return "redirect:/dentistas/novo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicaoDentista(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Dentista> dentistaOptional = dentistaService.buscarPorId(id); // Supondo que exista este método no service
        if (dentistaOptional.isPresent()) {
            model.addAttribute("dentista", dentistaOptional.get());
            return "dentista/formulario-dentista-edicao"; // Crie este HTML
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
        // if (result.hasErrors()) {
        //    dentista.setId(id); // Garanta que o ID está presente para o formulário de edição
        //    return "dentista/formulario-dentista-edicao";
        // }
        try {
            dentistaService.atualizarDentista(id, dentista); // Supondo que exista este método no service
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
            dentistaService.deletarDentista(id); // Supondo que exista este método no service
            redirectAttributes.addFlashAttribute("successMessage", "Dentista deletado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar dentista: " + e.getMessage());
        }
        return "redirect:/dentistas";
    }

    // Método para visualizar agenda do dentista
    @GetMapping("/{id}/agenda")
    public String visualizarAgenda(@PathVariable Long id,
                                   @RequestParam(name = "data", required = false) String dataStr, // data no formato YYYY-MM-DD
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
            // Supondo que o service retorne algo como AgendaVisualizacaoDTO
            // ou uma lista de horários e uma lista de consultas
            List<String> horariosDisponiveis = dentistaService.listarHorariosDisponiveis(id, dataSelecionada);
            // List<Consulta> consultasDoDia = dentistaService.listarConsultasPorDia(id, dataSelecionada);

            model.addAttribute("horariosDisponiveis", horariosDisponiveis);
            // model.addAttribute("consultasDoDia", consultasDoDia);
            return "dentista/agenda-dentista"; // Crie este HTML
        } catch (RuntimeException e) {
            model.addAttribute("agendaErrorMessage", "Erro ao carregar agenda: " + e.getMessage());
            return "dentista/agenda-dentista"; // Ou uma página de erro
        }
    }
}