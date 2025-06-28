package app.odontocare.service;

import app.odontocare.model.Dentista;
import app.odontocare.model.Agenda;
import app.odontocare.model.Consulta;
import app.odontocare.repository.DentistaRepository;
import app.odontocare.repository.AgendaRepository;
import app.odontocare.repository.ConsultaRepository;
import app.odontocare.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;
    private final AgendaRepository agendaRepository;
    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public DentistaService(DentistaRepository dentistaRepository,
                           AgendaRepository agendaRepository,
                           ConsultaRepository consultaRepository,
                           UsuarioRepository usuarioRepository) {
        this.dentistaRepository = dentistaRepository;
        this.agendaRepository = agendaRepository;
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Dentista cadastrarDentista(Dentista dentista) {
        // getLogin(), getEmail(), getDataCriacao() em Dentista (herdado de Usuario)
        if (dentista.getLogin() != null && usuarioRepository.findByLogin(dentista.getLogin()).isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }
        if (dentista.getEmail() != null && usuarioRepository.findByEmail(dentista.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        // getCro() em Dentista
        if (dentista.getCro() != null && dentistaRepository.findByCro(dentista.getCro()).isPresent()) {
            throw new RuntimeException("CRO já cadastrado.");
        }

        if (dentista.getDataCriacao() == null) {
            dentista.setDataCriacao(new Date());
        }

        return dentistaRepository.save(dentista);
    }

    public List<Dentista> listarTodos() {
        return dentistaRepository.findAll();
    }

    public Optional<Dentista> buscarPorId(Long id) {
        return dentistaRepository.findById(id);
    }

    @Transactional
    public Dentista atualizarDentista(Long id, Dentista dentistaAtualizado) {
        return dentistaRepository.findById(id)
                .map(dentistaExistente -> {
                    // getNomeAdm() em Dentista
                    dentistaExistente.setNomeAdm(dentistaAtualizado.getNomeAdm());

                    // getEmail() e setEmail() em Dentista (herdado de Usuario)
                    if (!dentistaExistente.getEmail().equals(dentistaAtualizado.getEmail())) {
                        if (usuarioRepository.findByEmail(dentistaAtualizado.getEmail()).isPresent()) {
                            throw new RuntimeException("Novo email já está em uso.");
                        }
                        dentistaExistente.setEmail(dentistaAtualizado.getEmail());
                    }

                    // getLogin() e setLogin() em Dentista (herdado de Usuario)
                    if (!dentistaExistente.getLogin().equals(dentistaAtualizado.getLogin())) {
                        if (usuarioRepository.findByLogin(dentistaAtualizado.getLogin()).isPresent()) {
                            throw new RuntimeException("Novo login já está em uso.");
                        }
                        dentistaExistente.setLogin(dentistaAtualizado.getLogin());
                    }

                    // getCro() e setCro() em Dentista
                    if (!dentistaExistente.getCro().equals(dentistaAtualizado.getCro())) {
                        if (dentistaRepository.findByCro(dentistaAtualizado.getCro()).isPresent()) {
                            throw new RuntimeException("Novo CRO já está em uso.");
                        }
                        dentistaExistente.setCro(dentistaAtualizado.getCro());
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

    public List<String> listarHorariosDisponiveis(Long dentistaId, LocalDate dia) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        DayOfWeek diaDaSemana = dia.getDayOfWeek();

        // getHoraInicio() e getHoraFim() em Agenda
        List<Agenda> horariosDeTrabalhoDoDia = agendaRepository
                .findByDentistaAndDiaDaSemana(dentista, diaDaSemana);

        Date inicioDoDia = Date.from(dia.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fimDoDia = Date.from(dia.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<Consulta> consultasAgendadasNoDia = consultaRepository
                .findByDentistaAndDataHoraBetween(dentista, inicioDoDia, fimDoDia);

        List<LocalTime> slotsOcupados = consultasAgendadasNoDia.stream()
                .map(consulta -> consulta.getDataHora().toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
                .collect(Collectors.toList());

        List<String> slotsDisponiveis = new ArrayList<>();
        int duracaoSlot = 30;

        for (Agenda agenda : horariosDeTrabalhoDoDia) {
            LocalTime slotAtual = agenda.getHoraInicio();
            while (slotAtual.isBefore(agenda.getHoraFim())) {
                if (!slotsOcupados.contains(slotAtual)) {
                    slotsDisponiveis.add(slotAtual.toString());
                }
                slotAtual = slotAtual.plusMinutes(duracaoSlot);
            }
        }
        return slotsDisponiveis;
    }

    public boolean verificarDisponibilidade(Long dentistaId, Date dataHora) {
        Optional<Dentista> dentistaOptional = dentistaRepository.findById(dentistaId);
        if (!dentistaOptional.isPresent()) {
            throw new RuntimeException("Dentista não encontrado.");
        }
        Dentista dentista = dentistaOptional.get();

        LocalDate dataConsulta = dataHora.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime horaConsulta = dataHora.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        DayOfWeek diaDaSemanaConsulta = dataConsulta.getDayOfWeek();

        List<Agenda> horariosAgenda = agendaRepository.findByDentistaAndDiaDaSemana(dentista, diaDaSemanaConsulta);
        boolean estaDentroDoHorarioDeTrabalho = horariosAgenda.stream().anyMatch(agenda ->
            (horaConsulta.equals(agenda.getHoraInicio()) || horaConsulta.isAfter(agenda.getHoraInicio())) &&
            horaConsulta.isBefore(agenda.getHoraFim())
        );

        if (!estaDentroDoHorarioDeTrabalho) {
            return false;
        }

        Date inicioSlot = Date.from(dataConsulta.atTime(horaConsulta).atZone(ZoneId.systemDefault()).toInstant());
        Date fimSlot = Date.from(dataConsulta.atTime(horaConsulta.plusMinutes(30)).atZone(ZoneId.systemDefault()).toInstant());

        List<Consulta> consultasExistentes = consultaRepository
                .findByDentistaAndDataHoraBetween(dentista, inicioSlot, fimSlot);

        return consultasExistentes.isEmpty();
    }
}