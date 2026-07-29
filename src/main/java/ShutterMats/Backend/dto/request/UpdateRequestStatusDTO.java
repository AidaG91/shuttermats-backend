package ShutterMats.Backend.dto.request;

import ShutterMats.Backend.entity.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Body del PATCH /admin/requests/{id}/status.
 * adminResponse es siempre opcional (ver KAN-96): null -> no se toca el
 * valor guardado, "" explicito -> se borra. Mismo criterio que
 * AdminEventController#resolveImage para imageUrl.
 */
public record UpdateRequestStatusDTO(

        @NotNull(message = "El estado es obligatorio")
        RequestStatus status,

        @Size(max = 1000, message = "La respuesta no puede superar los 1000 caracteres")
        String adminResponse
) {
}
