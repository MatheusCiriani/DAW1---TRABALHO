package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Cliente; // PACOTE CORRIGIDO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // findByEmail pode não fazer mais sentido se o email estiver no Usuario pai
    // Considere usar findByLogin (herdado de Usuario) ou findByEmail do Usuario
    Optional<Cliente> findByEmail(String email); // Manter por enquanto, mas revisar após ajuste no model
    Optional<Cliente> findByLogin(String login); // Adicionado, pois Cliente agora herda de Usuario e tem login
}