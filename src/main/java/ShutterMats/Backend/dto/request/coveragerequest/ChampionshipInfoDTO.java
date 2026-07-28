package ShutterMats.Backend.dto.request.coveragerequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChampionshipInfoDTO(

        @NotNull(message = "El evento es obligatorio")
        Long eventId,

        @Size(max = 150, message = "El organizador no puede superar los 150 caracteres")
        String organizer,

        @Size(max = 500, message = "El link de Smoothcomp no puede superar los 500 caracteres")
        String smoothcompLink
) {
}
