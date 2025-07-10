package app.odontocare.dto;

import lombok.Data;

@Data
public class ConsultaDTO {
    private Long clienteId;
    private Long dentistaId;
    private String dataAgendamento; // yyyy-MM-dd
    private String hora;            // HH:mm
    private String status;
}
