package ShutterMats.Backend.dto.request;

import ShutterMats.Backend.entity.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRequestStatusDTO(

        @NotNull(message = "El estado es obligatorio")
        RequestStatus status,

        @Size(max = 1000, message = "La respuesta no puede superar los 1000 caracteres")
        String adminResponse
) {
}
