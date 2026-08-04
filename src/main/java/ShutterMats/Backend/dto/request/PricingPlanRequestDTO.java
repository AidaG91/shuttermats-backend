package ShutterMats.Backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PricingPlanRequestDTO(

        @NotBlank(message = "El nombre de la tarifa es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @NotNull(message = "El precio base es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor que 0")
        BigDecimal basePrice,

        @NotNull(message = "El precio del combate extra es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio del combate extra debe ser mayor que 0")
        BigDecimal extraMatchPrice,

        // true para marcarla como la tarifa por defecto (se usa cuando un
        // evento no indica ninguna tarifa). null/false no hace nada - la
        // única forma de dejar de ser la tarifa por defecto es que otra la
        // sustituya.
        Boolean isDefault
) {
}
