package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.ContactMessageRequestDTO;
import ShutterMats.Backend.dto.response.ContactMessageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactMessageService {

    ContactMessageResponseDTO create(ContactMessageRequestDTO dto);

    Page<ContactMessageResponseDTO> findAll(Boolean read, Pageable pageable);

    ContactMessageResponseDTO findById(Long id);

    ContactMessageResponseDTO markAsRead(Long id);
}
