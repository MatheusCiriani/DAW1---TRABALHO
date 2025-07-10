package app.odontocare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivelDTO {
    private LocalTime horario;
    private boolean disponivel;
    // Futuramente, podemos adicionar o nome do cliente se estiver ocupado
    // private String nomeCliente; 
}