package odontocare.service;

import odontocare.model.Pagamento;
import odontocare.model.Consulta;
import odontocare.repository.PagamentoRepository;
import odontocare.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Date;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ConsultaRepository consultaRepository;

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository, ConsultaRepository consultaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public Pagamento registrarPagamento(Long consultaId, Pagamento pagamentoInfo) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada para registrar pagamento."));

        if (pagamentoRepository.findByConsulta(consulta).isPresent()) {
            throw new RuntimeException("Já existe um pagamento registrado para esta consulta.");
        }

        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setConsulta(consulta);
        novoPagamento.setValor(pagamentoInfo.getValor()); // Idealmente o valor viria da consulta/procedimento
        novoPagamento.setTipoPagamento(pagamentoInfo.getTipoPagamento());
        novoPagamento.setDataPagamento(new Date()); // Data atual do pagamento
        novoPagamento.setStatus("PAGO"); // Ou PENDENTE se for outro fluxo

        consulta.setPagamento(novoPagamento); // Associar de volta
        // consultaRepository.save(consulta); // Cascade deve cuidar do pagamento
        return pagamentoRepository.save(novoPagamento);
    }

    public Optional<Pagamento> buscarPagamentoPorConsultaId(Long consultaId) {
        Consulta consulta = new Consulta(); // Criar instância apenas para o ID
        consulta.setId(consultaId);
        return pagamentoRepository.findByConsulta(consulta);
    }

    // Outros métodos: verificarStatus, emitirRecibo (pode ser um DTO/relatório)
}