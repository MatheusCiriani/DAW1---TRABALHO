package odontocare.model;

import jakarta.persistence.*;
import java.util.Date; 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valor;

    @Temporal(TemporalType.DATE)
    private Date dataPagamento; 

    private String status; 
    private String tipoPagamento;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false, unique = true) 
    private Consulta consulta;


    
}