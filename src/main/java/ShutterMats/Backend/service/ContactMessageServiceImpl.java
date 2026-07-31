package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.ContactMessageRequestDTO;
import ShutterMats.Backend.dto.response.ContactMessageResponseDTO;
import ShutterMats.Backend.entity.ContactMessage;
import ShutterMats.Backend.exception.ContactMessageNotFoundException;
import ShutterMats.Backend.mapper.ContactMessageMapper;
import ShutterMats.Backend.repository.ContactMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository,
                                      ContactMessageMapper contactMessageMapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
    }

    @Override
    public ContactMessageResponseDTO create(ContactMessageRequestDTO dto) {
        ContactMessage contactMessage = contactMessageMapper.toEntity(dto);
        ContactMessage saved = contactMessageRepository.save(contactMessage);

        // El envío de la notificación por email al fotógrafo queda pendiente
        // (requiere configurar JavaMailSender/SMTP): ver issue de seguimiento.
        return contactMessageMapper.toResponseDTO(saved);
    }

    @Override
    public Page<ContactMessageResponseDTO> findAll(Boolean read, Pageable pageable) {
        Page<ContactMessage> page = read != null
                ? contactMessageRepository.findByRead(read, pageable)
                : contactMessageRepository.findAll(pageable);

        return page.map(contactMessageMapper::toResponseDTO);
    }

    @Override
    public ContactMessageResponseDTO findById(Long id) {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ContactMessageNotFoundException(id));

        return contactMessageMapper.toResponseDTO(contactMessage);
    }

    @Override
    public ContactMessageResponseDTO markAsRead(Long id) {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ContactMessageNotFoundException(id));

        contactMessage.setRead(true);
        ContactMessage saved = contactMessageRepository.save(contactMessage);

        return contactMessageMapper.toResponseDTO(saved);
    }
}
