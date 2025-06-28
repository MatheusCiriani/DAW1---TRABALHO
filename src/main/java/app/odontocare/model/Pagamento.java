package app.odontocare.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valor;

    @Temporal(TemporalType.TIMESTAMP) // Data e hora do pagamento
    @Column(nullable = false)
    private Date dataPagamento;

    @Column(nullable = false, length = 50) // Status do pagamento (PAGO, PENDENTE, ESTORNADO)
    private String status;

    @Column(nullable = false, length = 50) // Tipo de pagamento (CARTAO, DINHEIRO, PIX)
    private String tipoPagamento;

    // Relacionamento unidirecional: Pagamento PERTENCE a uma Consulta.
    // Esta é a chave estrangeira que aponta para Consulta.
    // 'unique = true' garante que uma consulta só tenha um pagamento.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false, unique = true)
    private Consulta consulta; // ADICIONADO de volta, pois a regra de negocio exige que o pagamento saiba a qual consulta ele pertence
}