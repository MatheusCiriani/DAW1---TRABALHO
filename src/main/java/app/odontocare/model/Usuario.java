package app.odontocare.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED) // Estratégia de herança
@Data // Lombok: getters, setters, equals, hashCode, toString
@NoArgsConstructor // Lombok: construtor sem argumentos
@AllArgsConstructor // Lombok: construtor com todos os argumentos
@SuperBuilder // Lombok: builder que funciona com herança
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100) // Adicionado length para evitar erro no DB
    private String login;

    @Column(unique = true, nullable = false, length = 255) // Adicionado length
    private String email;

    @Column(nullable = false, length = 255) // Adicionado length
    private String senha;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataCriacao;
}