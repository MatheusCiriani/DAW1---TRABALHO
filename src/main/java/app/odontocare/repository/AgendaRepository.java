package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Agenda; // PACOTE E CLASSE CORRIGIDOS (antes era HorarioTrabalho)
import app.odontocare.model.Dentista; // PACOTE CORRIGIDO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.time.LocalDate; // Adicionado para buscar por data
import java.util.List;
import java.util.Optional; // Útil para métodos de busca por ID ou únicos

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> { // NOME DA INTERFACE E TIPO CORRIGIDOS
    List<Agenda> findByDentista(Dentista dentista);
    List<Agenda> findByDentistaAndDiaDaSemana(Dentista dentista, DayOfWeek diaDaSemana);
    Optional<Agenda> findByDentistaAndData(Dentista dentista, LocalDate data); // Adicionado para buscar por data específica
    List<Agenda> findByDentistaAndDataBetween(Dentista dentista, LocalDate dataInicio, LocalDate dataFim); // Exemplo para buscar um período
    // Adicione outros métodos conforme a necessidade da sua lógica de agenda
}