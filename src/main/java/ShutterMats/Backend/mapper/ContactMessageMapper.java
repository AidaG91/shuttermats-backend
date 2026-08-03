package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.ContactMessageRequestDTO;
import ShutterMats.Backend.dto.response.ContactMessageResponseDTO;
import ShutterMats.Backend.entity.ContactMessage;
import org.springframework.stereotype.Component;

@Component
public class ContactMessageMapper {

    public ContactMessage toEntity(ContactMessageRequestDTO dto) {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(dto.name());
        contactMessage.setEmail(dto.email());
        contactMessage.setPhone(dto.phone());
        contactMessage.setSubject(dto.subject());
        contactMessage.setMessage(dto.message());
        contactMessage.setPrivacyAccepted(dto.privacyAccepted());
        contactMessage.setRead(false);
        return contactMessage;
    }

    public ContactMessageResponseDTO toResponseDTO(ContactMessage contactMessage) {
        return new ContactMessageResponseDTO(
                contactMessage.getId(),
                contactMessage.getName(),
                contactMessage.getEmail(),
                contactMessage.getPhone(),
                contactMessage.getSubject(),
                contactMessage.getMessage(),
                contactMessage.getRead(),
                contactMessage.getAdminResponse(),
                contactMessage.getCreatedAt()
        );
    }
}
