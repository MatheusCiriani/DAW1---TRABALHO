package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Usuario; // PACOTE CORRIGIDO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByEmail(String email); // Se Usuario também tiver um campo de email distinto do login
}