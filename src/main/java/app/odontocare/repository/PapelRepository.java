package app.odontocare.repository;

import app.odontocare.model.Papel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PapelRepository extends JpaRepository<Papel, Long> {
    Optional<Papel> findByNome(String nome); // Para buscar um papel pelo nome
}