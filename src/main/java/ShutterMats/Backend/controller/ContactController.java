package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.request.ContactMessageRequestDTO;
import ShutterMats.Backend.dto.response.ContactMessageResponseDTO;
import ShutterMats.Backend.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@Validated
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    // TODO(security): no anti-bot protection and no rate limiting on this
    // public endpoint -> an easy target for automated spam. Add reCAPTCHA v3
    // (or Cloudflare Turnstile) later: token in the frontend (ContactPage.jsx)
    // + verification here against the secret key before saving the message.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactMessageResponseDTO createContactMessage(@Valid @RequestBody ContactMessageRequestDTO dto) {
        return contactMessageService.create(dto);
    }
}
