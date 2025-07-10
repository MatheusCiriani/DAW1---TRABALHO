package app.odontocare.controller;

import app.odontocare.model.Consulta;
import app.odontocare.dto.ConsultaDTO;
import app.odontocare.model.Cliente;
import app.odontocare.model.Dentista;
import app.odontocare.service.ConsultaService;
import app.odontocare.service.ClienteService;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType; // Manter este import se ele for usado em outro lugar, senão remova.
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Date;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

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
        model.addAttribute("listaConsultas", consultaService.listarTodasConsultas());
        return "consulta/lista-consultas";
    }

    @GetMapping("/novo")
    public String mostrarFormularioAgendamento(Model model) {
        model.addAttribute("consultaDTO", new ConsultaDTO());
        model.addAttribute("listaClientes", clienteService.listarTodosClientes());
        model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas());
        return "consulta/formulario-consulta";
    }


    @PostMapping("/cadastrar")
    public String cadastrarConsulta(@ModelAttribute ConsultaDTO consultaDTO,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        try {
            // Montar a dataHora a partir da data + hora
            LocalDate data = LocalDate.parse(consultaDTO.getDataAgendamento());
            LocalTime hora = LocalTime.parse(consultaDTO.getHora());
            LocalDateTime dataHoraCompleta = LocalDateTime.of(data, hora);
            Date dataHoraFinal = Date.from(dataHoraCompleta.atZone(ZoneId.systemDefault()).toInstant());

            // Criar objeto Consulta
            Consulta consulta = new Consulta();
            consulta.setDataHora(dataHoraFinal);
            consulta.setStatus(consultaDTO.getStatus());

            // Carregar entidades reais
            Cliente cliente = clienteService.listarTodosClientes().stream()
                    .filter(c -> c.getId().equals(consultaDTO.getClienteId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            Dentista dentista = dentistaService.listarTodosDentistas().stream()
                    .filter(d -> d.getId().equals(consultaDTO.getDentistaId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

            consulta.setCliente(cliente);
            consulta.setDentista(dentista);
            consulta.setHorario(Date.from(dataHoraCompleta.atZone(ZoneId.systemDefault()).toInstant()));


            System.out.println("Cliente ID: " + consultaDTO.getClienteId());
            System.out.println("Dentista ID: " + consultaDTO.getDentistaId());
            System.out.println("Data: " + consultaDTO.getDataAgendamento());
            System.out.println("Hora: " + consultaDTO.getHora());
            System.out.println("Status: " + consultaDTO.getStatus());


            // Persistir
            consultaService.cadastrarConsulta(consulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta agendada com sucesso!");
            return "redirect:/consultas";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("listaClientes", clienteService.listarTodosClientes());
            model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas());
            return "redirect:/consultas/novo";

        }
    }



    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicaoConsulta(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Consulta> consultaOptional = consultaService.buscarConsultaPorId(id);
        if (consultaOptional.isPresent()) {
            Consulta consulta = consultaOptional.get();
            model.addAttribute("consulta", consulta);
            model.addAttribute("listaClientes", clienteService.listarTodosClientes());
            model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas());

            // Carregar horários disponíveis para a data e dentista da consulta
            LocalDate dataAgendamento = consulta.getDataHora().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Long dentistaId = consulta.getDentista().getId();
            List<LocalTime> horarios = dentistaService.listarHorariosDisponiveis(dentistaId, dataAgendamento);
            model.addAttribute("horarios", horarios);

            return "consulta/formulario-consulta-edicao";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada.");
            return "redirect:/consultas";
        }
    }


    @PostMapping("/atualizar/{id}")
    public String atualizarConsulta(@PathVariable Long id,
                                    @ModelAttribute("consulta") Consulta consulta,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Já vem pronto do input hidden no HTML
            // consulta.getDataHora() já está preenchido com data + hora
            consultaService.atualizarConsulta(id, consulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta atualizada com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("listaClientes", clienteService.listarTodosClientes());
            model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas());
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
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao finalizar consulta: " + e.getMessage());
        }
        return "redirect:/consultas";
    }

    // Endpoint HTMX para buscar horários disponíveis de um dentista em uma DATA (inferindo o DayOfWeek)
    @HxRequest
    @GetMapping(value = "/horarios-disponiveis", produces = MediaType.TEXT_HTML_VALUE)
    public String getHorariosDisponiveis(
            @RequestParam("dentistaId") Long dentistaId,
            @RequestParam("dataAgendamento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAgendamento,
            Model model) {

        List<LocalTime> horarios = dentistaService.listarHorariosDisponiveis(dentistaId, dataAgendamento);
        model.addAttribute("horarios", horarios);
        model.addAttribute("dataAgendamento", dataAgendamento);
        System.out.println("Retornando fragmento com " + horarios.size() + " horários.");

        return "consulta/horarios :: horariosSelectFragment";
    }

}