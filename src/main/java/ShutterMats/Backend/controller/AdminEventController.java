package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.service.EventService;
import ShutterMats.Backend.service.ImageStorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// PRIVATE (ADMIN) CRUD OF EVENTS
@RestController
@RequestMapping("/api/admin/events")
@Validated
public class AdminEventController {

    private final EventService eventService;
    private final ImageStorageService imageStorageService;

    public AdminEventController(EventService eventService, ImageStorageService imageStorageService) {
        this.eventService = eventService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(
            @RequestPart("event") @Valid EventRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        EventRequestDTO withImage = resolveImage(dto, image, null);
        return eventService.create(withImage);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EventResponseDTO updateEvent(
            @PathVariable @Positive Long id,
            @RequestPart("event") @Valid EventRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String currentImageUrl = eventService.findById(id).imageUrl();
        EventRequestDTO withImage = resolveImage(dto, image, currentImageUrl);
        return eventService.update(id, withImage);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable @Positive Long id) {
        eventService.delete(id);
    }

     private EventRequestDTO resolveImage(EventRequestDTO dto, MultipartFile image, String currentImageUrl) {
        String imageUrl;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageStorageService.store(image);
        } else if (dto.imageUrl() == null) {
            imageUrl = currentImageUrl;
        } else {
            imageUrl = StringUtils.hasText(dto.imageUrl()) ? dto.imageUrl() : null;
        }

        return new EventRequestDTO(dto.name(), dto.date(), dto.location(), imageUrl, dto.description());
    }
}
