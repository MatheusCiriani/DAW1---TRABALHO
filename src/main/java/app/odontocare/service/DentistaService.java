package odontocare.service;

import odontocare.model.Dentista;
import odontocare.model.HorarioTrabalho; // Se usar
import odontocare.model.Consulta;      // Se usar
import odontocare.repository.DentistaRepository;
import odontocare.repository.HorarioTrabalhoRepository; // Se usar
import odontocare.repository.ConsultaRepository;      // Se usar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date; // Mudar para java.time se possível
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;
    private final HorarioTrabalhoRepository horarioTrabalhoRepository; // Injete se usar HorarioTrabalho
    private final ConsultaRepository consultaRepository; // Injete para verificar consultas existentes

    @Autowired
    public DentistaService(DentistaRepository dentistaRepository,
                           HorarioTrabalhoRepository horarioTrabalhoRepository,
                           ConsultaRepository consultaRepository) {
        this.dentistaRepository = dentistaRepository;
        this.horarioTrabalhoRepository = horarioTrabalhoRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public Dentista cadastrarDentista(Dentista dentista) {
        // Adicionar validações, como CRO único, email único, etc.
        // Lógica para criptografar senha do usuário associado (similar ao ClienteService)
        return dentistaRepository.save(dentista);
    }

    public List<Dentista> listarTodos() {
        return dentistaRepository.findAll();
    }

    public List<String> listarHorariosDisponiveis(Long dentistaId, LocalDate dia) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado"));

        DayOfWeek diaDaSemana = dia.getDayOfWeek();

        List<HorarioTrabalho> horariosDeTrabalhoDoDia = horarioTrabalhoRepository
                .findByDentistaAndDiaDaSemana(dentista, diaDaSemana);

        List<Consulta> consultasAgendadasNoDia = consultaRepository
                .findByDentistaAndDataHoraBetween(dentista,
                        java.sql.Timestamp.valueOf(dia.atStartOfDay()),
                        java.sql.Timestamp.valueOf(dia.atTime(LocalTime.MAX))); // Adapte para java.util.Date se mantiver

        List<LocalTime> slotsOcupados = consultasAgendadasNoDia.stream()
                .map(consulta -> {
                    // Converter java.util.Date para LocalTime
                    // Esta conversão é um pouco mais complexa e depende de como Date foi armazenado.
                    // Supondo que Date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    // Mas o ideal é usar java.time.LocalDateTime em Consulta.dataHora
                    // Por simplicidade, vamos pular a conversão exata aqui.
                    // Se Consulta.dataHora fosse LocalDateTime:
                    // return consulta.getDataHora().toLocalTime();
                    return LocalTime.of(new Date(consulta.getDataHora().getTime()).getHours(), new Date(consulta.getDataHora().getTime()).getMinutes()); // Exemplo simplificado
                })
                .collect(Collectors.toList());

        List<String> slotsDisponiveis = new ArrayList<>();
        // Assumindo slots de 30 minutos, por exemplo
        int duracaoSlot = 30;

        for (HorarioTrabalho ht : horariosDeTrabalhoDoDia) {
            LocalTime slotAtual = ht.getHoraInicio();
            while (slotAtual.isBefore(ht.getHoraFim())) {
                if (!slotsOcupados.contains(slotAtual)) {
                    slotsDisponiveis.add(slotAtual.toString());
                }
                slotAtual = slotAtual.plusMinutes(duracaoSlot);
            }
        }
        return slotsDisponiveis;
    }

    public boolean verificarDisponibilidade(Long dentistaId, Date dataHora) {
        // Lógica complexa:
        // 1. Verificar se o dentista trabalha no dia/hora (usando HorarioTrabalho)
        // 2. Verificar se já não existe uma consulta para esse dentista nesse dia/hora
        // Esta implementação é apenas um placeholder.
        return true;
    }

    // Outros métodos: atualizar, deletar, etc.
}