package ShutterMats.Backend.dto.request;

import ShutterMats.Backend.entity.enums.ContactSubject;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactMessageRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
        String phone,

        @NotNull(message = "El asunto es obligatorio")
        ContactSubject subject,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 2000, message = "El mensaje no puede superar los 2000 caracteres")
        String message,

        @AssertTrue(message = "Debes aceptar la política de privacidad")
        boolean privacyAccepted
) {
}
