package ShutterMats.Backend.dto.request.coveragerequest;

import jakarta.validation.constraints.Size;

public record PreferencesDTO(

        @Size(max = 1000, message = "Las preferencias de fotos no pueden superar los 1000 caracteres")
        String photoPreferences,

        @Size(max = 1000, message = "Los momentos especiales no pueden superar los 1000 caracteres")
        String specialMoments,

        @Size(max = 1000, message = "Las notas adicionales no pueden superar los 1000 caracteres")
        String additionalNotes
) {
}
