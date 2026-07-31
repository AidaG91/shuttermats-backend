package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.response.ContactMessageResponseDTO;
import ShutterMats.Backend.service.ContactMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/contact-messages")
public class AdminContactMessageController {

    private final ContactMessageService contactMessageService;

    public AdminContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping
    public Page<ContactMessageResponseDTO> getContactMessages(
            @RequestParam(required = false) Boolean read,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return contactMessageService.findAll(read, pageable);
    }

    @GetMapping("/{id}")
    public ContactMessageResponseDTO getContactMessage(@PathVariable Long id) {
        return contactMessageService.findById(id);
    }

    @PatchMapping("/{id}/read")
    public ContactMessageResponseDTO markAsRead(@PathVariable Long id) {
        return contactMessageService.markAsRead(id);
    }
}
