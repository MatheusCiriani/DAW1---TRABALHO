package app.odontocare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "dentistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true) // Importante para equals/hashCode em classes que herdam
public class Dentista extends Usuario { // HERDA DE USUARIO

    // Campos herdados de Usuario: id, login, email, senha, dataCriacao

    @Column(nullable = false, length = 255)
    private String nomeAdm; // Nome administrativo do dentista

    @Column(unique = true, nullable = false, length = 20) // CRO é único e obrigatório
    private String cro;
}