package ShutterMats.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequestDTO(

        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
