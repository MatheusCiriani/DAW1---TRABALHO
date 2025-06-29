package app.odontocare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder; // Adicionado para facilitar a criação de instâncias

@Entity
@Table(name = "papeis") // Nome da tabela no banco de dados
@Data // Lombok: getters, setters, equals, hashCode, toString
@NoArgsConstructor // Lombok: construtor sem argumentos
@AllArgsConstructor // Lombok: construtor com todos os argumentos
@Builder // Lombok: para construir objetos de forma mais legível
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nome; // Ex: ROLE_ADMIN, ROLE_USER, ROLE_DENTISTA, ROLE_CLIENTE

    // Construtor, getters, setters, equals e hashCode são gerados pelo Lombok
}