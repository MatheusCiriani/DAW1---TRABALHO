package app.odontocare.controller;

import app.odontocare.dto.HorarioDisponivelDTO;
import app.odontocare.model.Consulta;
import app.odontocare.model.Dentista;
import app.odontocare.model.Usuario;
import app.odontocare.repository.UsuarioRepository;
import app.odontocare.service.ConsultaService;
import app.odontocare.service.DentistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

@Controller
public class HomeController {

    @Autowired private DentistaService dentistaService;
    @Autowired private ConsultaService consultaService;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String index(Model model, Authentication authentication, 
                        @RequestParam(name = "dentistaId", required = false) Long dentistaId) { // ✅ Parâmetro adicionado
        LocalDate hoje = LocalDate.now();
        model.addAttribute("dataAtualFormatada", formatarData(hoje));
        model.addAttribute("dataAtualParam", hoje);

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuarioLogado = usuarioRepository.findByLogin(userDetails.getUsername()).get();
            Long usuarioId = usuarioLogado.getId();

            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                model.addAttribute("isAdmin", true);
                List<Dentista> dentistas = dentistaService.listarTodosDentistas();
                model.addAttribute("listaDentistas", dentistas);

                Long dentistaSelecionadoId = dentistaId;

                // Se nenhum dentista foi selecionado via URL, seleciona o primeiro da lista como padrão
                if (dentistaSelecionadoId == null && !dentistas.isEmpty()) {
                    dentistaSelecionadoId = dentistas.get(0).getId();
                }

                if (dentistaSelecionadoId != null) {
                    final Long finalDentistaId = dentistaSelecionadoId;
                    Optional<Dentista> dentistaSelecionadoOpt = dentistas.stream()
                        .filter(d -> d.getId().equals(finalDentistaId)).findFirst();
                    
                    if(dentistaSelecionadoOpt.isPresent()){
                        model.addAttribute("dentistaSelecionado", dentistaSelecionadoOpt.get());
                        model.addAttribute("dentistaSelecionadoId", finalDentistaId);
                        
                        // Busca as consultas e a agenda do DENTISTA SELECIONADO
                        List<Consulta> consultasDoDia = consultaService.listarPorDentistaNoDia(finalDentistaId, hoje);
                        List<HorarioDisponivelDTO> agendaDoDia = dentistaService.getAgendaDiaDashboard(finalDentistaId, hoje);
                        
                        model.addAttribute("consultasDoDia", consultasDoDia);
                        model.addAttribute("agendaDoDia", agendaDoDia);
                    }
                }

            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DENTISTA"))) {
                model.addAttribute("isDentista", true);
                model.addAttribute("dentistaLogado", usuarioLogado);
                List<Consulta> consultasDoDia = consultaService.listarPorDentistaNoDia(usuarioLogado.getId(), hoje);
                List<HorarioDisponivelDTO> agendaDoDia = dentistaService.getAgendaDiaDashboard(usuarioLogado.getId(), hoje);
                model.addAttribute("consultasDoDia", consultasDoDia);
                model.addAttribute("agendaDoDia", agendaDoDia);

            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                model.addAttribute("isCliente", true);
                
                // ✅ NOVO: Busca as consultas do cliente para o dia atual
                List<Consulta> consultasDoDia = consultaService.listarPorClienteNoDia(usuarioId, hoje);
                model.addAttribute("consultasDoDia", consultasDoDia);

                // Seção de agendamento (sem alteração)
                List<Dentista> dentistas = dentistaService.listarTodosDentistas();
                model.addAttribute("listaDentistas", dentistas);
                
                if (!dentistas.isEmpty()) {
                    Dentista dentistaPadrao = dentistas.get(0);
                    model.addAttribute("dentistaSelecionadoId", dentistaPadrao.getId());
                    List<HorarioDisponivelDTO> agenda = dentistaService.getAgendaDiaDashboard(dentistaPadrao.getId(), hoje);
                    model.addAttribute("agendaDoDia", agenda);
                }

            }
        }

        return "home/index";
    }

    


    @HxRequest
    @GetMapping("/dashboard/agenda-dentista")
    public String getAgendaParaDashboard(@RequestParam Long dentistaId, Model model) {
        LocalDate hoje = LocalDate.now();
        List<HorarioDisponivelDTO> agenda = dentistaService.getAgendaDiaDashboard(dentistaId, hoje);
        model.addAttribute("agendaDoDia", agenda);
        model.addAttribute("dataAtualParam", hoje);
        model.addAttribute("dentistaSelecionadoId", dentistaId);
        
        // Retorna apenas o fragmento do HTML
        return "layout/fragments/dashboard-agenda :: agenda-grid";
    }

    private String formatarData(LocalDate date) {
        // Formata a data para "Quinta-feira, 10 de Julho de 2025"
        return date.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR")));
    }
}