package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Dentista; // PACOTE CORRIGIDO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    Optional<Dentista> findByEmail(String email); // Manter por enquanto, mas revisar após ajuste no model
    Optional<Dentista> findByCro(String cro);
    Optional<Dentista> findByLogin(String login); // Adicionado, pois Dentista agora herda de Usuario e tem login
}