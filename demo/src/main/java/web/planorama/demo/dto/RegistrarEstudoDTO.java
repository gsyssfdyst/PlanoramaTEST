package web.planorama.demo.dto;

import java.util.UUID;

import jakarta.validation.constraints.AssertTrue; 
import jakarta.validation.constraints.Max;        
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrarEstudoDTO {
    
    private UUID id;

    @NotNull(message = "O assunto não pode ser nulo, selecione, pelo menos um.")
    private UUID assuntoId;

    @NotNull(message = "O campo das horas não pode ser nulo.")
    @Min(value = 0, message = "O valor mínimo para as horas é 0.")
    @Max(value = 24, message = "O valor máximo para as horas é 24.") // Valida o teste de 25h
    private Integer horasEstudadas;

    @NotNull(message = "O campo dos minutos não pode ser nulo.")
    @Min(value = 0, message = "O valor mínimo para os minutos é 0.") // Permite 0 (ex: 1h 00m)
    @Max(value = 59, message = "Os minutos não podem passar de 59.") 
    private Integer minutosEstudados;

    @NotNull
    private UUID planejamentoId;

    @NotNull
    private UUID idMateriaPlanejamento;

    // --- VALIDAÇÃO PERSONALIZADA ---
    // Impede que o usuário salve "0h 0m", mas permite "1h 0m" ou "0h 30m".
    // Atende ao requisito: "deverá ser cadastrado pelo menos 1min de estudo"
    @AssertTrue(message = "Você precisa registrar, pelo menos, 1min de estudo.")
    public boolean isTempoValido() {
        if (horasEstudadas == null || minutosEstudados == null) {
            return true; // Deixa o @NotNull tratar se for nulo
        }
        return (horasEstudadas * 60 + minutosEstudados) > 0;
    }
}