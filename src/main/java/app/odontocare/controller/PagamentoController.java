package app.odontocare.controller;

import app.odontocare.model.Pagamento;
import app.odontocare.service.PagamentoService;
import app.odontocare.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import app.odontocare.model.Consulta;
import java.util.Optional;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final ConsultaService consultaService;

    @Autowired
    public PagamentoController(PagamentoService pagamentoService, ConsultaService consultaService) {
        this.pagamentoService = pagamentoService;
        this.consultaService = consultaService;
    }

    @GetMapping
    public String listarPagamentos(Model model) {
        model.addAttribute("listaPagamentos", pagamentoService.listarTodos());
        return "pagamento/lista-pagamentos :: content"; // CORRIGIDO
    }

    @GetMapping("/registrar/consulta/{consultaId}")
    public String mostrarFormularioPagamento(@PathVariable Long consultaId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Consulta> consultaOptional = consultaService.buscarPorId(consultaId);
        if (consultaOptional.isPresent()) {
            Consulta consulta = consultaOptional.get();
            Pagamento pagamento = new Pagamento();
            model.addAttribute("pagamento", pagamento);
            model.addAttribute("consultaId", consultaId);
            model.addAttribute("consulta", consulta);
            return "pagamento/formulario-pagamento :: content"; // CORRIGIDO
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada para registrar pagamento.");
            return "redirect:/consultas";
        }
    }

    @PostMapping("/realizar")
    public String realizarPagamento(@RequestParam("consultaId") Long consultaId,
                                    @ModelAttribute("pagamento") /*@Valid*/ Pagamento pagamento,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            pagamentoService.registrarPagamento(consultaId, pagamento);
            redirectAttributes.addFlashAttribute("successMessage", "Pagamento realizado com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pagamentos/registrar/consulta/" + consultaId;
        }
    }

    @GetMapping("/consulta/{consultaId}")
    public String visualizarPagamentoPorConsulta(@PathVariable Long consultaId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Consulta> consultaOptional = consultaService.buscarPorId(consultaId);
        if (consultaOptional.isPresent()) {
            Consulta consulta = consultaOptional.get();
            if (consulta.getPagamento() != null) {
                model.addAttribute("pagamento", consulta.getPagamento());
                model.addAttribute("consulta", consulta);
                return "pagamento/detalhes-pagamento :: content"; // CORRIGIDO
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Nenhum pagamento encontrado para esta consulta. Você pode registrar um novo.");
                return "redirect:/pagamentos/registrar/consulta/" + consultaId;
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada.");
            return "redirect:/consultas";
        }
    }

    @GetMapping("/recibo/{pagamentoId}")
    public String emitirRecibo(@PathVariable Long pagamentoId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pagamento> pagamentoOptional = pagamentoService.buscarPorId(pagamentoId);
        if (pagamentoOptional.isPresent()) {
            model.addAttribute("pagamento", pagamentoOptional.get());
            return "pagamento/recibo :: content"; // CORRIGIDO
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pagamento não encontrado.");
            return "redirect:/consultas";
        }
    }
}