package ShutterMats.Backend.dto.request.coveragerequest;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record ConfirmationsDTO(

        @NotNull(message = "Debes aceptar las condiciones del servicio")
        @AssertTrue(message = "Debes aceptar las condiciones del servicio")
        Boolean termsAccepted,

        Boolean portfolioConsent
) {
}
