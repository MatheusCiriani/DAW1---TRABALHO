package odontocare.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; 

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaSemplificadaDTO {
    private Long id;
    private String nomeCliente;
    private String horaConsulta; 
    private String status;
}