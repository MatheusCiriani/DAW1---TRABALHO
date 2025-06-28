package app.odontocare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true) // Importante para equals/hashCode em classes que herdam
public class Cliente extends Usuario { // HERDA DE USUARIO

    // Campos herdados de Usuario: id, login, email, senha, dataCriacao

    @Column(nullable = false, length = 255)
    private String nomeCliente; // Nome específico do cliente (se diferente do nome de login)

    @Column(length = 500)
    private String endereco;

    @Column(length = 20)
    private String telefone;

    private Integer idade; // Idade é opcionalmente tratada como campo sem length
}