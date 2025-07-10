package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Consulta; // PACOTE CORRIGIDO
import app.odontocare.model.Dentista; // PACOTE CORRIGIDO
import app.odontocare.model.Cliente; // PACOTE CORRIGIDO
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

}