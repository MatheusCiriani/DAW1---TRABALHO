package app.odontocare.service;

import app.odontocare.model.Consulta;
import app.odontocare.model.Cliente;
import app.odontocare.model.Dentista;
import app.odontocare.model.Pagamento;
import app.odontocare.repository.ConsultaRepository;
import app.odontocare.repository.ClienteRepository;
import app.odontocare.repository.DentistaRepository;
import app.odontocare.repository.PagamentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final DentistaService dentistaService;
    private final ClienteRepository clienteRepository;
    private final DentistaRepository dentistaRepository;
    private final PagamentoRepository pagamentoRepository;

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
    public Consulta agendarConsulta(Consulta consulta) {
        // getCliente(), getDentista() em Consulta
        Cliente cliente = clienteRepository.findById(consulta.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        Dentista dentista = dentistaRepository.findById(consulta.getDentista().getId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado."));

        consulta.setCliente(cliente); // setCliente(), setDentista() em Consulta
        consulta.setDentista(dentista);

        // getId(), getDataHora() em Dentista/Consulta
        if (!dentistaService.verificarDisponibilidade(dentista.getId(), consulta.getDataHora())) {
            throw new RuntimeException("Horário indisponível para o dentista selecionado.");
        }

        consulta.setStatus("AGENDADA"); // setStatus() em Consulta
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
        consulta.setStatus("CANCELADA"); // setStatus() em Consulta

        // getPagamento() em Consulta
        if (consulta.getPagamento() != null) {
            consulta.getPagamento().setStatus("ESTORNADO");
            pagamentoRepository.save(consulta.getPagamento());
        }
        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta finalizarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        consulta.setStatus("REALIZADA"); // setStatus() em Consulta
        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta atualizarConsulta(Consulta consultaAtualizada) {
        // getId() em Consulta
        Consulta consultaExistente = consultaRepository.findById(consultaAtualizada.getId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada para atualização."));

        // getCliente(), getDentista() em ConsultaAtualizada
        Cliente cliente = clienteRepository.findById(consultaAtualizada.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para atualização da consulta."));
        Dentista dentista = dentistaRepository.findById(consultaAtualizada.getDentista().getId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado para atualização da consulta."));

        // getId(), getDataHora() em Dentista/ConsultaAtualizada
        if (!dentistaService.verificarDisponibilidade(dentista.getId(), consultaAtualizada.getDataHora())) {
            throw new RuntimeException("Horário indisponível para o dentista selecionado na atualização.");
        }

        // getDataHora(), getStatus() em ConsultaAtualizada
        consultaExistente.setDataHora(consultaAtualizada.getDataHora());
        consultaExistente.setStatus(consultaAtualizada.getStatus());
        consultaExistente.setCliente(cliente); // setCliente() em Consulta
        consultaExistente.setDentista(dentista); // setDentista() em Consulta

        return consultaRepository.save(consultaExistente);
    }
}