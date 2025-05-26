
package odontocare.service;

import odontocare.model.Consulta;
import odontocare.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final DentistaService dentistaService; // Para verificar disponibilidade

    @Autowired
    public ConsultaService(ConsultaRepository consultaRepository, DentistaService dentistaService) {
        this.consultaRepository = consultaRepository;
        this.dentistaService = dentistaService;
    }

    @Transactional
    public Consulta agendarConsulta(Consulta consulta) {
        // Validações: Cliente existe, Dentista existe
        // Verificar disponibilidade do dentista (usar DentistaService.verificarDisponibilidade)
        if (!dentistaService.verificarDisponibilidade(consulta.getDentista().getId(), consulta.getDataHora())) {
            throw new RuntimeException("Horário indisponível para o dentista selecionado.");
        }
        consulta.setStatus("AGENDADA"); // Definir status inicial
        return consultaRepository.save(consulta);
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    @Transactional
    public Consulta cancelarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        // Adicionar regras: só pode cancelar com X antecedência, etc.
        consulta.setStatus("CANCELADA");
        // Se houver pagamento associado, estornar/cancelar pagamento
        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta finalizarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        consulta.setStatus("REALIZADA");
        // Pode disparar a criação de um pagamento se não existir, ou marcar como pendente de pagamento.
        return consultaRepository.save(consulta);
    }
}