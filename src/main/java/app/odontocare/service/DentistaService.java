package app.odontocare.service;

import app.odontocare.model.Dentista;
import app.odontocare.model.Agenda;
import app.odontocare.model.Consulta;
import app.odontocare.model.Papel;
import app.odontocare.repository.DentistaRepository;
import app.odontocare.repository.AgendaRepository;
import app.odontocare.repository.ConsultaRepository;
import app.odontocare.repository.UsuarioRepository;
import app.odontocare.repository.PapelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Comparator;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;
    private final AgendaRepository agendaRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PapelRepository papelRepository;
    
    private static final int DURACAO_SLOT_MINUTOS = 30;


    @Autowired
    public DentistaService(DentistaRepository dentistaRepository,
                           AgendaRepository agendaRepository,
                           ConsultaRepository consultaRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           PapelRepository papelRepository) {
        this.dentistaRepository = dentistaRepository;
        this.agendaRepository = agendaRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.papelRepository = papelRepository;
    }

    @Transactional
    public Dentista cadastrarDentista(Dentista dentista) {
        if (dentista.getLogin() != null && usuarioRepository.findByLogin(dentista.getLogin()).isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }
        if (dentista.getEmail() != null && usuarioRepository.findByEmail(dentista.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        if (dentista.getCro() != null && dentistaRepository.findByCro(dentista.getCro()).isPresent()) {
            throw new RuntimeException("CRO já cadastrado.");
        }

        if (dentista.getDataCriacao() == null) {
            dentista.setDataCriacao(new Date());
        }

        if (dentista.getSenha() == null || dentista.getSenha().isEmpty()) {
            throw new RuntimeException("A senha não pode ser vazia.");
        }
        dentista.setSenha(passwordEncoder.encode(dentista.getSenha()));

        Papel papelDentista = papelRepository.findByNome("ROLE_DENTISTA")
            .orElseThrow(() -> new RuntimeException("Papel ROLE_DENTISTA não encontrado. Verifique as migrações Flyway."));
        dentista.getPapeis().add(papelDentista);
        dentista.setAtivo(true);

        return dentistaRepository.save(dentista);
    }

    // MÉTODO RENOMEADO para listarTodosDentistas
    public List<Dentista> listarTodosDentistas() {
        return dentistaRepository.findAll();
    }

    public Optional<Dentista> buscarPorId(Long id) {
        return dentistaRepository.findById(id);
    }

    @Transactional
    public Dentista atualizarDentista(Long id, Dentista dentistaAtualizado) {
        return dentistaRepository.findById(id)
                .map(dentistaExistente -> {
                    dentistaExistente.setNomeAdm(dentistaAtualizado.getNomeAdm());

                    if (!dentistaExistente.getEmail().equals(dentistaAtualizado.getEmail())) {
                        if (usuarioRepository.findByEmail(dentistaAtualizado.getEmail()).isPresent()) {
                            throw new RuntimeException("Novo email já está em uso.");
                        }
                        dentistaExistente.setEmail(dentistaAtualizado.getEmail());
                    }

                    if (!dentistaExistente.getLogin().equals(dentistaAtualizado.getLogin())) {
                        if (usuarioRepository.findByLogin(dentistaAtualizado.getLogin()).isPresent()) {
                            throw new RuntimeException("Novo login já está em uso.");
                        }
                        dentistaExistente.setLogin(dentistaAtualizado.getLogin());
                    }

                    if (!dentistaExistente.getCro().equals(dentistaAtualizado.getCro())) {
                        if (dentistaRepository.findByCro(dentistaAtualizado.getCro()).isPresent()) {
                            throw new RuntimeException("Novo CRO já está em uso.");
                        }
                        dentistaExistente.setCro(dentistaAtualizado.getCro());
                    }

                    if (dentistaAtualizado.getSenha() != null && !dentistaAtualizado.getSenha().isEmpty()) {
                        dentistaExistente.setSenha(passwordEncoder.encode(dentistaAtualizado.getSenha()));
                    }

                    return dentistaRepository.save(dentistaExistente);
                }).orElseThrow(() -> new RuntimeException("Dentista não encontrado com id: " + id));
    }

    @Transactional
    public void deletarDentista(Long id) {
        if (!dentistaRepository.existsById(id)) {
            throw new RuntimeException("Dentista não encontrado com id: " + id);
        }
        dentistaRepository.deleteById(id);
    }

    public List<LocalTime> listarHorariosDisponiveis(Long dentistaId, LocalDate dataEspecifica) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado."));

        DayOfWeek diaDaSemana = dataEspecifica.getDayOfWeek();

        List<Agenda> horariosDeTrabalhoPadrao = agendaRepository
                .findByDentistaAndDiaDaSemana(dentista, diaDaSemana);
        
        if (horariosDeTrabalhoPadrao.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocalTime> todosOsSlotsPotenciais = new ArrayList<>();
        for (Agenda agenda : horariosDeTrabalhoPadrao) {
            LocalTime slotAtual = agenda.getHoraInicio();
            while (slotAtual.isBefore(agenda.getHoraFim())) {
                todosOsSlotsPotenciais.add(slotAtual);
                slotAtual = slotAtual.plusMinutes(DURACAO_SLOT_MINUTOS);
            }
        }
        
        LocalDateTime inicioDoDia = dataEspecifica.atStartOfDay();
        LocalDateTime fimDoDia = dataEspecifica.atTime(LocalTime.MAX);

        List<Consulta> consultasAgendadasNoDia = consultaRepository
                .findByDentistaAndDataHoraBetween(dentista, 
                                                  Date.from(inicioDoDia.atZone(ZoneId.systemDefault()).toInstant()),
                                                  Date.from(fimDoDia.atZone(ZoneId.systemDefault()).toInstant()));

        List<LocalTime> slotsOcupados = consultasAgendadasNoDia.stream()
                .map(consulta -> consulta.getDataHora().toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
                .collect(Collectors.toList());

        List<LocalTime> slotsDisponiveis = todosOsSlotsPotenciais.stream()
                .filter(slot -> !slotsOcupados.contains(slot))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        return slotsDisponiveis;
    }

    public boolean verificarDisponibilidade(Long dentistaId, LocalDateTime dataHoraConsulta) {
        Optional<Dentista> dentistaOptional = dentistaRepository.findById(dentistaId);
        if (!dentistaOptional.isPresent()) {
            throw new RuntimeException("Dentista não encontrado.");
        }
        Dentista dentista = dentistaOptional.get();

        LocalDate data = dataHoraConsulta.toLocalDate();
        LocalTime hora = dataHoraConsulta.toLocalTime();
        DayOfWeek diaDaSemana = data.getDayOfWeek();

        List<Agenda> horariosDeTrabalhoPadrao = agendaRepository.findByDentistaAndDiaDaSemana(dentista, diaDaSemana);
        boolean estaDentroDoHorarioDeTrabalhoPadrao = horariosDeTrabalhoPadrao.stream().anyMatch(agenda ->
            (hora.equals(agenda.getHoraInicio()) || hora.isAfter(agenda.getHoraInicio())) &&
            (hora.isBefore(agenda.getHoraFim()))
        );

        if (!estaDentroDoHorarioDeTrabalhoPadrao) {
            return false;
        }

        Date inicioSlot = Date.from(dataHoraConsulta.atZone(ZoneId.systemDefault()).toInstant());
        Date fimSlot = Date.from(dataHoraConsulta.plusMinutes(DURACAO_SLOT_MINUTOS).atZone(ZoneId.systemDefault()).toInstant());

        List<Consulta> consultasExistentesNoSlot = consultaRepository
                .findByDentistaAndDataHoraBetween(dentista, inicioSlot, fimSlot);
        
        return consultasExistentesNoSlot.isEmpty();
    }
}