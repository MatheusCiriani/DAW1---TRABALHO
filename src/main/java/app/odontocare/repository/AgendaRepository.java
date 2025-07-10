package app.odontocare.repository;

import app.odontocare.model.Agenda;
import app.odontocare.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate; // Manter o import se precisar para outros métodos ou lógica.
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findByDentista(Dentista dentista);
    List<Agenda> findByDentistaAndDiaDaSemana(Dentista dentista, DayOfWeek diaDaSemana);
    // REMOVIDO: Optional<Agenda> findByDentistaAndData(Dentista dentista, LocalDate data);
    // REMOVIDO: List<Agenda> findByDentistaAndDataBetween(Dentista dentista, LocalDate dataInicio, LocalDate dataFim);
}