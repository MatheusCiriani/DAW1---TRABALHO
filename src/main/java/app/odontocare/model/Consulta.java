package app.odontocare.model;

import jakarta.persistence.*;
import java.util.Date; // Para compatibilidade com o tipo Date em ConsultaController/Service

import org.springframework.format.annotation.DateTimeFormat;

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
    private Long id; // <-- Campo de identificação necessário!

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // ADICIONE ESTA LINHA
    @Column(nullable = false)
    private Date dataHora;


    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date horario;

    @Column(nullable = false, length = 50)
    private String status;
}
