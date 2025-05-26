package odontocare.model; 

import jakarta.persistence.*;
import java.util.List;
import lombok.Data; 
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String nomeCliente;
    private String endereco;
    private String email;
    private Integer idade; 
    private String telefone;


    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "usuario_id", referencedColumnName = "id") 
    private Usuario usuario;


    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consulta> consultas;


}