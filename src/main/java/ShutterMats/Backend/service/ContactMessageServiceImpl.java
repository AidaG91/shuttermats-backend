package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.ContactMessageRequestDTO;
import ShutterMats.Backend.dto.request.UpdateContactMessageResponseDTO;
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
    private final EmailService emailService;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository,
                                      ContactMessageMapper contactMessageMapper,
                                      EmailService emailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
        this.emailService = emailService;
    }

    @Override
    public ContactMessageResponseDTO create(ContactMessageRequestDTO dto) {
        ContactMessage contactMessage = contactMessageMapper.toEntity(dto);
        ContactMessage saved = contactMessageRepository.save(contactMessage);

        // EmailServiceImpl already guards against SMTP failures (it only
        // logs a warning), so this can never break the contact form submit.
        emailService.sendContactMessageNotification(saved);

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

    @Override
    public ContactMessageResponseDTO saveResponse(Long id, UpdateContactMessageResponseDTO dto) {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ContactMessageNotFoundException(id));

        // Saving a response implies the message has already been handled.
        contactMessage.setAdminResponse(dto.adminResponse());
        contactMessage.setRead(true);
        ContactMessage saved = contactMessageRepository.save(contactMessage);

        return contactMessageMapper.toResponseDTO(saved);
    }
}
