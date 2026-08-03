package ShutterMats.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateContactMessageResponseDTO(

        @NotBlank(message = "La respuesta no puede estar vacía")
        @Size(max = 2000, message = "La respuesta no puede superar los 2000 caracteres")
        String adminResponse
) {
}
