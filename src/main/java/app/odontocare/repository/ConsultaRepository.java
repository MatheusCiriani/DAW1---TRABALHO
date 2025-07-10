package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Consulta; // PACOTE CORRIGIDO
import app.odontocare.model.Dentista; // PACOTE CORRIGIDO
import app.odontocare.model.Cliente; // PACOTE CORRIGIDO
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByDentista(Dentista dentista);
    List<Consulta> findByCliente(Cliente cliente);
    List<Consulta> findByDentistaAndDataHoraBetween(Dentista dentista, Date inicio, Date fim);
    List<Consulta> findByDentistaIdAndDataHoraBetween(Long dentistaId, LocalDateTime inicio, LocalDateTime fim);
    List<Consulta> findByDentistaIdAndDataHoraBetweenAndStatusNot(Long dentistaId, Date inicioDoDia, Date fimDoDia, String status);

    Page<Consulta> findByClienteId(Long clienteId, Pageable pageable);
    Page<Consulta> findByDentistaId(Long dentistaId, Pageable pageable);

    // Em ConsultaRepository.java
    List<Consulta> findByDentistaIdAndDataHoraBetweenOrderByDataHoraAsc(Long dentistaId, Date inicio, Date fim);

    // ✅ NOVO: Para buscar as consultas de um cliente em um dia específico.
    List<Consulta> findByClienteIdAndDataHoraBetweenOrderByDataHoraAsc(Long clienteId, Date inicio, Date fim);

    // ✅ NOVO: Para o ADMIN buscar consultas pelo nome do cliente
    Page<Consulta> findByClienteNomeClienteContainingIgnoreCase(String nomeCliente, Pageable pageable);

    // ✅ NOVO: Para o DENTISTA buscar consultas de um cliente específico pelo nome
    Page<Consulta> findByDentistaIdAndClienteNomeClienteContainingIgnoreCase(Long dentistaId, String nomeCliente, Pageable pageable);


}