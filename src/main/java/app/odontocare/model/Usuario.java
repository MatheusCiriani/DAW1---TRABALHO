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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) 

    @Column(nullable = false)
    private String senha; 

    @Temporal(TemporalType.DATE) 
    private Date dataCriacao; 
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Dentista dentista;


   
}