package ShutterMats.Backend.dto.response;

import java.time.LocalDate;

public record EventResponseDTO(
        Long id,
        String name,
        LocalDate date,
        String location,
        String imageUrl,
        String description
) {
}
