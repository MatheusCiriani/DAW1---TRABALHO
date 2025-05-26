package odontocare.controller;

import odontocare.model.Pagamento;
import odontocare.service.PagamentoService;
import odontocare.service.ConsultaService; // Para buscar informações da consulta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final ConsultaService consultaService; // Para obter detalhes da consulta

    @Autowired
    public PagamentoController(PagamentoService pagamentoService, ConsultaService consultaService) {
        this.pagamentoService = pagamentoService;
        this.consultaService = consultaService;
    }

    // Listar pagamentos pode não ser uma tela principal, talvez mais via consulta
    @GetMapping
    public String listarPagamentos(Model model) {
        model.addAttribute("listaPagamentos", pagamentoService.listarTodos()); // Supondo que este método exista
        return "pagamento/lista-pagamentos"; // Crie este HTML
    }

    // Formulário para registrar um novo pagamento, geralmente a partir de uma consulta
    @GetMapping("/registrar/consulta/{consultaId}")
    public String mostrarFormularioPagamento(@PathVariable Long consultaId, Model model, RedirectAttributes redirectAttributes) {
        if (consultaService.buscarPorId(consultaId).isPresent()) {
            Pagamento pagamento = new Pagamento();
            // Você pode preencher o valor e outros detalhes da consulta aqui
            // Ex: pagamento.setValor(consultaService.buscarPorId(consultaId).get().getValorSugerido());
            model.addAttribute("pagamento", pagamento);
            model.addAttribute("consultaId", consultaId);
            model.addAttribute("consulta", consultaService.buscarPorId(consultaId).get());
            return "pagamento/formulario-pagamento"; // Crie este HTML
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada para registrar pagamento.");
            return "redirect:/consultas"; // Ou outra página de erro/listagem
        }
    }

    @PostMapping("/realizar")
    public String realizarPagamento(@RequestParam("consultaId") Long consultaId,
                                    @ModelAttribute("pagamento") /*@Valid*/ Pagamento pagamento,
                                    BindingResult result,
                                    Model model, // Para repopular dados se houver erro
                                    RedirectAttributes redirectAttributes) {
        // if (result.hasErrors()) {
        //     model.addAttribute("consultaId", consultaId);
        //     model.addAttribute("consulta", consultaService.buscarPorId(consultaId).orElse(null));
        //     return "pagamento/formulario-pagamento";
        // }
        try {
            pagamentoService.registrarPagamento(consultaId, pagamento);
            redirectAttributes.addFlashAttribute("successMessage", "Pagamento realizado com sucesso!");
            return "redirect:/consultas"; // Ou para detalhes da consulta/pagamento
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // model.addAttribute("consultaId", consultaId);
            // model.addAttribute("consulta", consultaService.buscarPorId(consultaId).orElse(null));
            // return "pagamento/formulario-pagamento";
            return "redirect:/pagamentos/registrar/consulta/" + consultaId;
        }
    }

    @GetMapping("/consulta/{consultaId}")
    public String visualizarPagamentoPorConsulta(@PathVariable Long consultaId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pagamento> pagamentoOptional = pagamentoService.buscarPagamentoPorConsultaId(consultaId);
        if (pagamentoOptional.isPresent()) {
            model.addAttribute("pagamento", pagamentoOptional.get());
            model.addAttribute("consulta", pagamentoOptional.get().getConsulta()); // Acesso à consulta via pagamento
            return "pagamento/detalhes-pagamento"; // Crie este HTML
        } else {
            // Se não houver pagamento, talvez redirecionar para registrar um novo
            // ou mostrar uma mensagem indicando que não há pagamento.
            redirectAttributes.addFlashAttribute("infoMessage", "Nenhum pagamento encontrado para esta consulta. Você pode registrar um novo.");
            return "redirect:/pagamentos/registrar/consulta/" + consultaId;
        }
    }

    // Um método para emitir recibo poderia gerar um PDF ou uma view específica
    @GetMapping("/recibo/{pagamentoId}")
    public String emitirRecibo(@PathVariable Long pagamentoId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pagamento> pagamentoOptional = pagamentoService.buscarPorId(pagamentoId); // Supondo este método
        if (pagamentoOptional.isPresent()) {
            model.addAttribute("pagamento", pagamentoOptional.get());
            return "pagamento/recibo"; // Crie este HTML (ou endpoint que gera PDF)
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Pagamento não encontrado.");
            return "redirect:/consultas"; // Ou uma página de listagem de pagamentos
        }
    }
}