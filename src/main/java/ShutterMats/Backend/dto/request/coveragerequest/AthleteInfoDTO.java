package ShutterMats.Backend.dto.request.coveragerequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AthleteInfoDTO(

        @NotBlank(message = "El nombre del atleta es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String name,

        @NotBlank(message = "El email del atleta es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "El teléfono del atleta es obligatorio")
        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
        String phone,

        @Size(max = 100, message = "El instagram no puede superar los 100 caracteres")
        String instagram,

        @Size(max = 150, message = "El gimnasio/academia no puede superar los 150 caracteres")
        String gym,

        @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
        String city,

        @Size(max = 100, message = "El país no puede superar los 100 caracteres")
        String country
) {
}
