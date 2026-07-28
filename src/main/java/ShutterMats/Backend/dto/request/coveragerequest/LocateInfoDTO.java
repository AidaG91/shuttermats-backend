package ShutterMats.Backend.dto.request.coveragerequest;

import jakarta.validation.constraints.Size;

public record LocateInfoDTO(

        @Size(max = 150, message = "El nombre en Smoothcomp no puede superar los 150 caracteres")
        String smoothcompDisplayName,

        @Size(max = 500, message = "El link de Smoothcomp no puede superar los 500 caracteres")
        String smoothcompProfileLink,

        @Size(max = 50, message = "La hora aproximada no puede superar los 50 caracteres")
        String estimatedFirstFightTime
) {
}
