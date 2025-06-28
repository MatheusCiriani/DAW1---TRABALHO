package app.odontocare.model;

import jakarta.persistence.*;
import java.util.Date; // Para compatibilidade com o tipo Date em ConsultaController/Service
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP) // Armazena data e hora
    @Column(nullable = false)
    private Date dataHora;

    @Column(nullable = false, length = 50) // Status da consulta (AGENDADA, CANCELADA, REALIZADA)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false) // Chave estrangeira para Cliente
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false) // Chave estrangeira para Dentista
    private Dentista dentista;

    // Relacionamento unidirecional: Consulta TEM um Pagamento.
    // 'mappedBy' indica que o lado 'Pagamento' é que gerencia a chave estrangeira.
    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Pagamento pagamento;
}