package ShutterMats.Backend.dto.response;

import ShutterMats.Backend.entity.enums.ContactSubject;

import java.time.LocalDateTime;

public record ContactMessageResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        ContactSubject subject,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
}
