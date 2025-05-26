package odontocare.model;

import jakarta.persistence.*;
import java.time.LocalTime; 
import lombok.Data;

@Entity
@Data
public class HorarioTrabalho { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    private java.time.DayOfWeek diaDaSemana; 

    private LocalTime horaInicio;
    private LocalTime horaFim;

   
}