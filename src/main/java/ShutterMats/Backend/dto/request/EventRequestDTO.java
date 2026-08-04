package ShutterMats.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventRequestDTO(

        @NotBlank(message = "El nombre del evento es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String name,

        @NotNull(message = "La fecha del evento es obligatoria")
        LocalDate date,

        @NotBlank(message = "La ubicación del evento es obligatoria")
        @Size(max = 100, message = "La ubicación no puede superar los 100 caracteres")
        String location,

        @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
        String imageUrl,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String description,

        @Size(max = 500, message = "El enlace no puede superar los 500 caracteres")
        String registrationUrl,

        // Which pricing plan to snapshot onto this event. Required when
        // creating an event; on update, leave null to keep the event's
        // current price untouched (see EventServiceImpl).
        Long pricingPlanId
) {
}
