package app.odontocare.service;

import app.odontocare.model.Agenda;
import app.odontocare.model.Dentista;
import app.odontocare.repository.AgendaRepository;
import app.odontocare.repository.DentistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate; // Ainda útil para lógica, mas não no objeto Agenda
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final DentistaRepository dentistaRepository;

    @Autowired
    public AgendaService(AgendaRepository agendaRepository, DentistaRepository dentistaRepository) {
        this.agendaRepository = agendaRepository;
        this.dentistaRepository = dentistaRepository;
    }

    @Transactional
    public Agenda cadastrarAgenda(Agenda agenda) {
        Dentista dentista = dentistaRepository.findById(agenda.getDentista().getId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado para associar à agenda."));
        agenda.setDentista(dentista);

        // Validação de sobreposição de horários para o mesmo dentista no mesmo dia da semana
        List<Agenda> horariosExistentesParaDiaSemana = agendaRepository.findByDentistaAndDiaDaSemana(
                dentista, agenda.getDiaDaSemana());

        // Para um CRUD simples:
        // Se já existe um horário para o mesmo dentista no mesmo dia da semana
        if (!horariosExistentesParaDiaSemana.isEmpty()) {
            // Lógica mais precisa aqui: verificar se o novo horário se sobrepõe
            // ao invés de apenas existir para o dia da semana.
            // Por simplicidade: se já existe um registro para o dia da semana, impede novo cadastro
            throw new RuntimeException("Já existe um horário de trabalho cadastrado para este dentista neste dia da semana. Use a edição para alterá-lo.");
        }
        
        return agendaRepository.save(agenda);
    }

    public Optional<Agenda> buscarPorId(Long id) {
        return agendaRepository.findById(id);
    }

    public List<Agenda> listarTodas() {
        return agendaRepository.findAll();
    }

    @Transactional
    public Agenda atualizarAgenda(Long id, Agenda agendaAtualizada) {
        return agendaRepository.findById(id)
                .map(agendaExistente -> {
                    Dentista dentista = dentistaRepository.findById(agendaAtualizada.getDentista().getId())
                            .orElseThrow(() -> new RuntimeException("Dentista não encontrado para atualizar a agenda."));
                    
                    agendaExistente.setDentista(dentista);
                    agendaExistente.setDiaDaSemana(agendaAtualizada.getDiaDaSemana());
                    // REMOVIDO: agendaExistente.setData(agendaAtualizada.getData());
                    agendaExistente.setHoraInicio(agendaAtualizada.getHoraInicio());
                    agendaExistente.setHoraFim(agendaAtualizada.getHoraFim());

                    // Validação de sobreposição para atualização:
                    // Verifica se a atualização criaria uma sobreposição com OUTROS horários.
                    List<Agenda> horariosExistentesParaDiaSemana = agendaRepository.findByDentistaAndDiaDaSemana(
                        dentista, agendaAtualizada.getDiaDaSemana());

                    for (Agenda horario : horariosExistentesParaDiaSemana) {
                        if (!horario.getId().equals(agendaExistente.getId())) { // Ignora a própria agenda que está sendo atualizada
                            throw new RuntimeException("A atualização criaria sobreposição com outro horário de trabalho já cadastrado para este dentista neste dia da semana.");
                        }
                    }

                    return agendaRepository.save(agendaExistente);
                }).orElseThrow(() -> new RuntimeException("Agenda não encontrada com id: " + id));
    }

    @Transactional
    public void deletarAgenda(Long id) {
        if (!agendaRepository.existsById(id)) {
            throw new RuntimeException("Agenda não encontrada com id: " + id);
        }
        agendaRepository.deleteById(id);
    }

    public List<Agenda> listarAgendasPorDentista(Long dentistaId) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado."));
        return agendaRepository.findByDentista(dentista);
    }

    // ✅ MÉTODO ATUALIZADO para aceitar o nome do dentista na busca
    public Page<Agenda> listarPaginado(String nomeDentista, Pageable pageable) {
        if (nomeDentista != null && !nomeDentista.trim().isEmpty()) {
            return agendaRepository.findByDentistaNomeAdmContainingIgnoreCase(nomeDentista, pageable);
        } else {
            return agendaRepository.findAll(pageable);
        }
    }
}