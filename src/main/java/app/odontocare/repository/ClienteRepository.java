package odontocare.repository;

import com.example.seuprojeto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    // Você pode adicionar outros métodos de busca customizados aqui
    // Ex: Optional<Cliente> findByUsuarioLogin(String login);
}