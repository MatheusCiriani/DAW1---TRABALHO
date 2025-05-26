package odontocare.repository;

import odontocare.model.Consulta;
import odontocare.model.Dentista;
import odontocare.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByDentista(Dentista dentista);
    List<Consulta> findByCliente(Cliente cliente);
    List<Consulta> findByDentistaAndDataHoraBetween(Dentista dentista, Date inicio, Date fim);
    // Adicionar outros métodos conforme necessário
}