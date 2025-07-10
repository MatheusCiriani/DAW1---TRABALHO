package app.odontocare.service;

import app.odontocare.model.Consulta;
import app.odontocare.dto.ConsultaDTO;
import app.odontocare.model.Cliente;
import app.odontocare.model.Dentista;
import app.odontocare.repository.ConsultaRepository;
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.DentistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Date;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final DentistaService dentistaService;
    private final ClienteRepository clienteRepository;
    private final DentistaRepository dentistaRepository;

    @Autowired
    public ConsultaService(ConsultaRepository consultaRepository,
                           DentistaService dentistaService,
                           ClienteRepository clienteRepository,
                           DentistaRepository dentistaRepository) {
        this.consultaRepository = consultaRepository;
        this.dentistaService = dentistaService;
        this.clienteRepository = clienteRepository;
        this.dentistaRepository = dentistaRepository;
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

    @Transactional
    public Consulta atualizarConsulta(Long id, ConsultaDTO consultaDTO) {
        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada para atualização."));

        // Monta a nova data e hora a partir do DTO
        LocalDate data = LocalDate.parse(consultaDTO.getDataAgendamento());
        LocalTime hora = LocalTime.parse(consultaDTO.getHora());
        LocalDateTime novaDataHora = LocalDateTime.of(data, hora);
        Date novaDataHoraDate = Date.from(novaDataHora.atZone(ZoneId.systemDefault()).toInstant());

        // Valida a disponibilidade, exceto se o horário não mudou
        if (!novaDataHora.equals(LocalDateTime.ofInstant(consultaExistente.getDataHora().toInstant(), ZoneId.systemDefault()))) {
            if (!dentistaService.verificarDisponibilidade(consultaDTO.getDentistaId(), novaDataHora)) {
                 throw new RuntimeException("Horário indisponível para o dentista selecionado na atualização.");
            }
        }

        // Busca as entidades relacionadas
        Cliente cliente = clienteRepository.findById(consultaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        Dentista dentista = dentistaRepository.findById(consultaDTO.getDentistaId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado."));

        // Atualiza os campos da entidade existente
        consultaExistente.setCliente(cliente);
        consultaExistente.setDentista(dentista);
        consultaExistente.setDataHora(novaDataHoraDate);
        consultaExistente.setHorario(novaDataHoraDate); // Mantendo consistência
        consultaExistente.setStatus(consultaDTO.getStatus());

        return consultaRepository.save(consultaExistente);
    }

    // ✅ MÉTODO ATUALIZADO para ADMIN
    public Page<Consulta> listarTodasPaginadas(String nome, Pageable pageable) {
        if (nome != null && !nome.trim().isEmpty()) {
            return consultaRepository.findByClienteNomeClienteContainingIgnoreCase(nome, pageable);
        } else {
            return consultaRepository.findAll(pageable);
        }
    }

    public Page<Consulta> listarPorClientePaginadas(Long clienteId, Pageable pageable) {
        return consultaRepository.findByClienteId(clienteId, pageable);
    }

    // ✅ MÉTODO ATUALIZADO para DENTISTA
    public Page<Consulta> listarPorDentistaPaginadas(Long dentistaId, String nome, Pageable pageable) {
        if (nome != null && !nome.trim().isEmpty()) {
            return consultaRepository.findByDentistaIdAndClienteNomeClienteContainingIgnoreCase(dentistaId, nome, pageable);
        } else {
            return consultaRepository.findByDentistaId(dentistaId, pageable);
        }
    }

    // Em ConsultaService.java
    public List<Consulta> listarPorDentistaNoDia(Long dentistaId, LocalDate data) {
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.atTime(LocalTime.MAX);
        return consultaRepository.findByDentistaIdAndDataHoraBetweenOrderByDataHoraAsc(
            dentistaId,
            Date.from(inicioDoDia.atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(fimDoDia.atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    // ... dentro da classe ConsultaService

    /**
     * ✅ NOVO: Lista todas as consultas de um cliente específico em uma data específica, ordenadas por hora.
     * @param clienteId O ID do cliente.
     * @param data A data para a qual as consultas serão buscadas.
     * @return Uma lista de consultas do cliente para o dia especificado.
     */
    public List<Consulta> listarPorClienteNoDia(Long clienteId, LocalDate data) {
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.atTime(LocalTime.MAX);

        // Converte LocalDateTime para Date para ser compatível com o tipo no repositório
        Date inicio = Date.from(inicioDoDia.atZone(ZoneId.systemDefault()).toInstant());
        Date fim = Date.from(fimDoDia.atZone(ZoneId.systemDefault()).toInstant());

        return consultaRepository.findByClienteIdAndDataHoraBetweenOrderByDataHoraAsc(clienteId, inicio, fim);
    }
}