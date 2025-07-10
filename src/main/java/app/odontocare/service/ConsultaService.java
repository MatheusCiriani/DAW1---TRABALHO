package app.odontocare.service;

import app.odontocare.model.Consulta;
import app.odontocare.model.Cliente;
import app.odontocare.model.Dentista;
import app.odontocare.model.Pagamento; // Mantido, mas não será usado devido ao escopo
import app.odontocare.repository.ConsultaRepository;
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.DentistaRepository;
import app.odontocare.repository.PagamentoRepository; // Mantido, mas não será usado devido ao escopo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final DentistaService dentistaService;
    private final ClienteRepository clienteRepository;
    private final DentistaRepository dentistaRepository;
    private final PagamentoRepository pagamentoRepository; // Mantido, mas não será usado devido ao escopo

    @Autowired
    public ConsultaService(ConsultaRepository consultaRepository,
                           DentistaService dentistaService,
                           ClienteRepository clienteRepository,
                           DentistaRepository dentistaRepository,
                           PagamentoRepository pagamentoRepository) {
        this.consultaRepository = consultaRepository;
        this.dentistaService = dentistaService;
        this.clienteRepository = clienteRepository;
        this.dentistaRepository = dentistaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public Consulta cadastrarConsulta(Consulta consulta) {
        Cliente cliente = clienteRepository.findById(consulta.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para agendamento."));
        Dentista dentista = dentistaRepository.findById(consulta.getDentista().getId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado para agendamento."));

        consulta.setCliente(cliente);
        consulta.setDentista(dentista);

        LocalDateTime dataHoraParaValidacao = LocalDateTime.ofInstant(consulta.getDataHora().toInstant(), ZoneId.systemDefault());

        if (!dentistaService.verificarDisponibilidade(dentista.getId(), dataHoraParaValidacao)) {
            throw new RuntimeException("Horário indisponível para o dentista selecionado.");
        }

        consulta.setStatus("AGENDADA");
        return consultaRepository.save(consulta);
    }

    // RENOMEADO: listarTodas -> listarTodasConsultas
    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    // RENOMEADO: buscarPorId -> buscarConsultaPorId
    public Optional<Consulta> buscarConsultaPorId(Long id) {
        return consultaRepository.findById(id);
    }

    @Transactional
    public Consulta cancelarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada para cancelamento."));
        consulta.setStatus("CANCELADA");
        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta finalizarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada para finalização."));
        consulta.setStatus("REALIZADA");
        return consultaRepository.save(consulta);
    }

    public List<String> buscarHorariosDisponiveis(Long dentistaId, LocalDate dataAgendamento) {
    // Defina a agenda fixa do sistema (pode vir do banco futuramente)
        List<String> todosHorarios = List.of(
            "08:00", "09:00", "10:00", "11:00",
            "13:00", "14:00", "15:00", "16:00"
        );

        // Busca todas as consultas do dentista nesta data
        LocalDateTime inicio = dataAgendamento.atStartOfDay();
        LocalDateTime fim = dataAgendamento.atTime(23, 59);

        List<Consulta> consultasAgendadas = consultaRepository.findByDentistaIdAndDataHoraBetween(dentistaId, inicio, fim);

        // Extrai os horários ocupados
        List<String> horariosOcupados = consultasAgendadas.stream()
            .map(consulta -> consulta.getDataHora()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                .withSecond(0).withNano(0).toString().substring(0, 5)) // "HH:mm"
            .toList();

        // Filtra os horários disponíveis
        return todosHorarios.stream()
            .filter(horario -> !horariosOcupados.contains(horario))
            .toList();
    }


    @Transactional
    public Consulta atualizarConsulta(Long id, Consulta consultaAtualizada) {
        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada para atualização."));

        Cliente cliente = clienteRepository.findById(consultaAtualizada.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para atualização da consulta."));
        Dentista dentista = dentistaRepository.findById(consultaAtualizada.getDentista().getId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado para atualização da consulta."));

        LocalDateTime dataHoraParaValidacao = LocalDateTime.ofInstant(consultaAtualizada.getDataHora().toInstant(), ZoneId.systemDefault());

        if (!dentistaService.verificarDisponibilidade(dentista.getId(), dataHoraParaValidacao)) {
             throw new RuntimeException("Horário indisponível para o dentista selecionado na atualização.");
        }

        consultaExistente.setDataHora(consultaAtualizada.getDataHora());
        consultaExistente.setStatus(consultaAtualizada.getStatus());
        consultaExistente.setCliente(cliente);
        consultaExistente.setDentista(dentista);

        return consultaRepository.save(consultaExistente);
    }

    public Page<Consulta> listarPaginadas(Pageable pageable) {
        return consultaRepository.findAll(pageable);
    }
}