package ShutterMats.Backend.dto.response;

import ShutterMats.Backend.entity.enums.BeltCategory;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import ShutterMats.Backend.entity.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CoverageRequestResponseDTO(
        Long id,
        RequestStatus status,
        String athleteName,
        String athleteEmail,
        EventResponseDTO event,
        Division division,
        CompetitionModality modality,
        BeltCategory belt,
        String weight,
        List<String> extras,
        LocalDateTime createdAt,
        String adminResponse
) {
}
