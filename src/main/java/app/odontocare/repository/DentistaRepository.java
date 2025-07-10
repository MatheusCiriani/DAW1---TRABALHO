package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Dentista; // PACOTE CORRIGIDO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    Optional<Dentista> findByEmail(String email); // Manter por enquanto, mas revisar após ajuste no model
    Optional<Dentista> findByCro(String cro);
    Optional<Dentista> findByLogin(String login); // Adicionado, pois Dentista agora herda de Usuario e tem login
    // ✅ NOVO: Método para buscar por nome (ignorando case) e paginar resultados
    Page<Dentista> findByNomeAdmContainingIgnoreCase(String nome, Pageable pageable);

}