package odontocare.repository;

import odontocare.model.HorarioTrabalho;
import odontocare.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface HorarioTrabalhoRepository extends JpaRepository<HorarioTrabalho, Long> {
    List<HorarioTrabalho> findByDentista(Dentista dentista);
    List<HorarioTrabalho> findByDentistaAndDiaDaSemana(Dentista dentista, DayOfWeek diaDaSemana);
}