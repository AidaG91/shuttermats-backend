package ShutterMats.Backend.dto.request.coveragerequest;

import ShutterMats.Backend.entity.enums.BeltCategory;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryInfoDTO(

        @Size(max = 50, message = "El peso no puede superar los 50 caracteres")
        String weight,

        @NotNull(message = "El cinturón es obligatorio")
        BeltCategory belt,

        @NotNull(message = "La división es obligatoria")
        Division division,

        @NotNull(message = "La modalidad es obligatoria")
        CompetitionModality modality
) {
}
