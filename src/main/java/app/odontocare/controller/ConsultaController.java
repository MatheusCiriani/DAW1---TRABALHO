package app.odontocare.controller;

import app.odontocare.model.Consulta;
import app.odontocare.dto.ConsultaDTO;
import app.odontocare.model.Cliente;
import app.odontocare.model.Dentista;
import app.odontocare.model.Usuario;
import app.odontocare.repository.UsuarioRepository;
import app.odontocare.service.ConsultaService;
import app.odontocare.service.ClienteService;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType; // Manter este import se ele for usado em outro lugar, senão remova.
import org.springframework.security.core.Authentication; // ✅ IMPORT ADICIONADO
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {
    

    @Autowired private ConsultaService consultaService;
    @Autowired private ClienteService clienteService;
    @Autowired private DentistaService dentistaService;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarConsultas(@RequestParam(defaultValue = "0") int page, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Page<Consulta> pagina;
        PageRequest pageRequest = PageRequest.of(page, 5);

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            pagina = consultaService.listarTodasPaginadas(pageRequest);
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            pagina = consultaService.listarPorDentistaPaginadas(usuarioLogado.getId(), pageRequest);
        } else { // ROLE_CLIENTE
            pagina = consultaService.listarPorClientePaginadas(usuarioLogado.getId(), pageRequest);
        }

        model.addAttribute("pagina", pagina);
        return "consulta/lista-consultas";
    }

    @GetMapping("/novo")
    public String mostrarFormularioAgendamento(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername()).get();

        ConsultaDTO consultaDTO = new ConsultaDTO();
        
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            consultaDTO.setClienteId(usuarioLogado.getId());
            model.addAttribute("clienteLogado", true);
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            consultaDTO.setDentistaId(usuarioLogado.getId());
            model.addAttribute("dentistaLogado", true);
        }

        model.addAttribute("consultaDTO", consultaDTO);
        model.addAttribute("listaClientes", clienteService.listarTodosClientes());
        model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas());
        return "consulta/formulario-consulta";
    }

    @PostMapping("/cadastrar")
    public String cadastrarConsulta(@ModelAttribute ConsultaDTO consultaDTO, RedirectAttributes redirectAttributes, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername()).get();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            if (!consultaDTO.getClienteId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro de permissão: Clientes só podem agendar para si mesmos.");
                return "redirect:/consultas/novo";
            }
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            if (!consultaDTO.getDentistaId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro de permissão: Dentistas só podem agendar para si mesmos.");
                return "redirect:/consultas/novo";
            }
        }

        try {
            LocalDate data = LocalDate.parse(consultaDTO.getDataAgendamento());
            LocalTime hora = LocalTime.parse(consultaDTO.getHora());
            LocalDateTime dataHoraCompleta = LocalDateTime.of(data, hora);
            Date dataHoraFinal = Date.from(dataHoraCompleta.atZone(ZoneId.systemDefault()).toInstant());

            Consulta consulta = new Consulta();
            consulta.setDataHora(dataHoraFinal);
            consulta.setStatus("AGENDADA");
            consulta.setHorario(dataHoraFinal);

            Cliente cliente = clienteService.buscarPorId(consultaDTO.getClienteId()).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            Dentista dentista = dentistaService.buscarPorId(consultaDTO.getDentistaId()).orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

            consulta.setCliente(cliente);
            consulta.setDentista(dentista);

            consultaService.cadastrarConsulta(consulta);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta agendada com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao agendar consulta: " + e.getMessage());
            return "redirect:/consultas/novo";
        }
    }



    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicaoConsulta(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        Optional<Consulta> consultaOptional = consultaService.buscarConsultaPorId(id);
        if (consultaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada.");
            return "redirect:/consultas";
        }
        Consulta consulta = consultaOptional.get();

        // Verificação de Segurança
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername()).get();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            if (!consulta.getDentista().getId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Acesso Negado: Você só pode editar suas próprias consultas.");
                return "redirect:/consultas";
            }
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            if (!consulta.getCliente().getId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Acesso Negado: Você só pode editar suas próprias consultas.");
                return "redirect:/consultas";
            }
        }
        
        // Converte a Entidade para um DTO para enviar para a tela
        ConsultaDTO consultaDTO = new ConsultaDTO();
        consultaDTO.setClienteId(consulta.getCliente().getId());
        consultaDTO.setDentistaId(consulta.getDentista().getId());
        consultaDTO.setStatus(consulta.getStatus());
        LocalDateTime ldt = LocalDateTime.ofInstant(consulta.getDataHora().toInstant(), ZoneId.systemDefault());
        consultaDTO.setDataAgendamento(ldt.toLocalDate().toString());
        consultaDTO.setHora(ldt.toLocalTime().toString());

        model.addAttribute("consultaId", id);
        model.addAttribute("consultaDTO", consultaDTO);
        model.addAttribute("listaClientes", clienteService.listarTodosClientes());
        model.addAttribute("listaDentistas", dentistaService.listarTodosDentistas());
        
        // Para desabilitar campos na view
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            model.addAttribute("dentistaLogado", true);
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            model.addAttribute("clienteLogado", true);
        }

        return "consulta/formulario-consulta-edicao";
    }

    /**
     * ✅ MÉTODO ATUALIZADO com verificação de segurança para DENTISTA e CLIENTE
     */
    @PostMapping("/atualizar/{id}")
    public String atualizarConsulta(@PathVariable Long id, @ModelAttribute("consultaDTO") ConsultaDTO consultaDTO, RedirectAttributes redirectAttributes, Authentication authentication) {
        Optional<Consulta> consultaOriginalOpt = consultaService.buscarConsultaPorId(id);
        if (consultaOriginalOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Consulta não encontrada.");
            return "redirect:/consultas";
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername()).get();
        
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            if (!consultaOriginalOpt.get().getDentista().getId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Acesso Negado: Você só pode atualizar suas próprias consultas.");
                return "redirect:/consultas";
            }
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            if (!consultaOriginalOpt.get().getCliente().getId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Acesso Negado: Você só pode atualizar suas próprias consultas.");
                return "redirect:/consultas";
            }
        }

        try {
            consultaService.atualizarConsulta(id, consultaDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Consulta atualizada com sucesso!");
            return "redirect:/consultas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar consulta: " + e.getMessage());
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
            @RequestParam(value = "dentistaId", required = false) Long dentistaId,
            @RequestParam("dataAgendamento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAgendamento,
            Model model, Authentication authentication) {

        Long finalDentistaId = dentistaId;

        // ✅ CORREÇÃO: Se o usuário logado for um DENTISTA, usa o ID dele, ignorando o parâmetro.
        // Isso corrige o bug do botão e aumenta a segurança.
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername()).get();
            finalDentistaId = usuarioLogado.getId();
        }

        // Se nenhum dentista foi selecionado (nem pelo formulário, nem pelo login), retorna uma lista vazia.
        if (finalDentistaId == null) {
            model.addAttribute("horarios", new ArrayList<LocalTime>());
        } else {
            List<LocalTime> horarios = dentistaService.listarHorariosDisponiveis(finalDentistaId, dataAgendamento);
            model.addAttribute("horarios", horarios);
        }
        
        model.addAttribute("dataAgendamento", dataAgendamento);
        return "consulta/horarios :: horariosSelectFragment";
    }

}