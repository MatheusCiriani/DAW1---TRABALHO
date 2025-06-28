package app.odontocare.repository; // PACOTE CORRIGIDO

import app.odontocare.model.Pagamento; // PACOTE CORRIGIDO
// import odontocare.model.Consulta; // REMOVIDO: Pagamento não tem Consulta como atributo
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    // REMOVIDO: Optional<Pagamento> findByConsulta(Consulta consulta);
    // Conforme o feedback, Pagamento não tem referência a Consulta.
    // Para buscar o pagamento de uma consulta, você buscará a Consulta e, a partir dela, acessará o Pagamento.
}