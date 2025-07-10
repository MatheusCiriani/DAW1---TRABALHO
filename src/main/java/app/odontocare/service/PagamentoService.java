package app.odontocare.service;

import app.odontocare.model.Pagamento;
import app.odontocare.model.Consulta;
import app.odontocare.repository.PagamentoRepository;
import app.odontocare.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Date;
import java.util.List;

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

        Pagamento novoPagamento = new Pagamento();
        // getValor(), getTipoPagamento() em PagamentoInfo
        // setValor(), setTipoPagamento(), setDataPagamento(), setStatus() em novoPagamento
        novoPagamento.setValor(pagamentoInfo.getValor());
        novoPagamento.setTipoPagamento(pagamentoInfo.getTipoPagamento());
        novoPagamento.setDataPagamento(new Date());
        novoPagamento.setStatus("PAGO");

        Pagamento pagamentoSalvo = pagamentoRepository.save(novoPagamento);

        consultaRepository.save(consulta);

        return pagamentoSalvo;
    }

    public Optional<Pagamento> buscarPagamentoPorConsultaId(Long pagamentoId) {
        return pagamentoRepository.findById(pagamentoId);
    }

    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }
}