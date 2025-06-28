package app.odontocare.dto; 

import app.odontocare.model.Consulta; 
import java.time.LocalTime; 
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaVisualizacaoDTO {
    private Long dentistaId;
    private String nomeDentista;
    private List<String> horariosDisponiveis; // "09:00", "09:30"
    private List<ConsultaSimplificadaDTO> consultasAgendadas; 
}