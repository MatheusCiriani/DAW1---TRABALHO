package app.odontocare.service;

import app.odontocare.dto.HorarioDisponivelDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * ✅ LÓGICA PRINCIPAL ATUALIZADA
     * Usa o método corrigido do repositório para buscar horários disponíveis.
     */
    public List<LocalTime> listarHorariosDisponiveis(Long dentistaId, LocalDate dataEspecifica) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado."));
        
        DayOfWeek diaDaSemana = dataEspecifica.getDayOfWeek();

        List<Agenda> horariosDeTrabalho = agendaRepository.findByDentistaAndDiaDaSemana(dentista, diaDaSemana);
        
        if (horariosDeTrabalho.isEmpty()) {
            return new ArrayList<>();
        }
        
        Agenda agendaDoDia = horariosDeTrabalho.get(0);

        List<LocalTime> todosOsSlotsPotenciais = new ArrayList<>();
        LocalTime slotAtual = agendaDoDia.getHoraInicio();
        while (slotAtual.isBefore(agendaDoDia.getHoraFim())) {
            todosOsSlotsPotenciais.add(slotAtual);
            slotAtual = slotAtual.plusMinutes(DURACAO_SLOT_MINUTOS);
        }
        
        LocalDateTime inicioDoDia = dataEspecifica.atStartOfDay();
        LocalDateTime fimDoDia = dataEspecifica.atTime(LocalTime.MAX);

        // ✅ CORREÇÃO: Usa o novo método do repositório que ignora consultas canceladas.
        List<Consulta> consultasAgendadas = consultaRepository
                .findByDentistaIdAndDataHoraBetweenAndStatusNot(dentistaId, 
                                                  Date.from(inicioDoDia.atZone(ZoneId.systemDefault()).toInstant()),
                                                  Date.from(fimDoDia.atZone(ZoneId.systemDefault()).toInstant()),
                                                  "CANCELADA");

        List<LocalTime> slotsOcupados = consultasAgendadas.stream()
                .map(consulta -> consulta.getDataHora().toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
                .collect(Collectors.toList());

        return todosOsSlotsPotenciais.stream()
                .filter(slot -> !slotsOcupados.contains(slot))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public boolean verificarDisponibilidade(Long dentistaId, LocalDateTime dataHoraConsulta) {
        List<LocalTime> horariosDisponiveis = listarHorariosDisponiveis(dentistaId, dataHoraConsulta.toLocalDate());
        return horariosDisponiveis.contains(dataHoraConsulta.toLocalTime());
    }

    // --- MÉTODOS EXISTENTES (sem alterações) ---

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
        Dentista dentista = dentistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado com id: " + id));

        // ✅ 1. VERIFICA SE EXISTEM AGENDAS VINCULADAS
        List<Agenda> agendasDoDentista = agendaRepository.findByDentista(dentista);
        if (!agendasDoDentista.isEmpty()) {
            throw new RuntimeException("Este dentista não pode ser excluído pois possui horários de trabalho (agendas) associados.");
        }

        // ✅ 2. VERIFICA SE EXISTEM CONSULTAS VINCULADAS
        List<Consulta> consultasDoDentista = consultaRepository.findByDentista(dentista);
        if (!consultasDoDentista.isEmpty()) {
            throw new RuntimeException("Este dentista não pode ser excluído pois possui um histórico de consultas associado.");
        }

        // ✅ 3. SE PASSAR NAS VERIFICAÇÕES, DELETA O DENTISTA
        dentistaRepository.delete(dentista);
    }
    
    public Page<Dentista> listarPaginado(Pageable pageable) {
        return dentistaRepository.findAll(pageable);
    }

    public List<HorarioDisponivelDTO> getAgendaDiaDashboard(Long dentistaId, LocalDate data) {
        // 1. Pega todos os slots de trabalho possíveis para o dia
        List<LocalTime> todosOsSlots = listarTodosOsSlotsDeTrabalho(dentistaId, data);
        
        // 2. Pega os horários já ocupados (que não estão cancelados)
        List<LocalTime> horariosOcupados = getHorariosOcupados(dentistaId, data);

        // 3. Monta a lista final de DTOs
        return todosOsSlots.stream()
                .map(slot -> new HorarioDisponivelDTO(slot, !horariosOcupados.contains(slot)))
                .collect(Collectors.toList());
    }

    private List<LocalTime> listarTodosOsSlotsDeTrabalho(Long dentistaId, LocalDate data) {
        DayOfWeek diaDaSemana = data.getDayOfWeek();
        Optional<Agenda> agendaDoDiaOpt = agendaRepository.findByDentistaIdAndDiaDaSemana(dentistaId, diaDaSemana);
        
        if (agendaDoDiaOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        Agenda agendaDoDia = agendaDoDiaOpt.get();
        List<LocalTime> todosOsSlotsPotenciais = new ArrayList<>();
        LocalTime slotAtual = agendaDoDia.getHoraInicio();
        while (slotAtual.isBefore(agendaDoDia.getHoraFim())) {
            todosOsSlotsPotenciais.add(slotAtual);
            slotAtual = slotAtual.plusMinutes(DURACAO_SLOT_MINUTOS);
        }
        return todosOsSlotsPotenciais;
    }

    private List<LocalTime> getHorariosOcupados(Long dentistaId, LocalDate data) {
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.atTime(LocalTime.MAX);

        List<Consulta> consultasAgendadas = consultaRepository
                .findByDentistaIdAndDataHoraBetweenAndStatusNot(dentistaId, 
                                                  Date.from(inicioDoDia.atZone(ZoneId.systemDefault()).toInstant()),
                                                  Date.from(fimDoDia.atZone(ZoneId.systemDefault()).toInstant()),
                                                  "CANCELADA");

        return consultasAgendadas.stream()
                .map(consulta -> consulta.getDataHora().toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
                .collect(Collectors.toList());
    }

    // ✅ MÉTODO ATUALIZADO
    public Page<Dentista> listarPaginado(String nome, Pageable pageable) {
        if (nome != null && !nome.isEmpty()) {
            // Se um nome foi fornecido, usa o novo método de busca
            return dentistaRepository.findByNomeAdmContainingIgnoreCase(nome, pageable);
        } else {
            // Caso contrário, retorna todos
            return dentistaRepository.findAll(pageable);
        }
    }
}