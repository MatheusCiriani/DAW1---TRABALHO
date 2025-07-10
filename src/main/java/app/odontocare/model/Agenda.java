package app.odontocare.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate; // Manter este import se precisar de LocalDate em outros lugares, mas 'data' não será um campo persistido.
import java.time.LocalTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "agendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaDaSemana;

    // REMOVIDO: Atributo 'data'
    // private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;
}