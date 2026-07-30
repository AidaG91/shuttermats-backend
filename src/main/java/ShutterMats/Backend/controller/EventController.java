package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.service.EventService;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Validated
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public Page<EventResponseDTO> getEvents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            Pageable pageable) {
        return eventService.findAll(status, location, pageable);
    }

    @GetMapping("/locations")
    public List<String> getLocations() {
        return eventService.findAllLocations();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEvent(@PathVariable @Positive Long id) {
        return eventService.findById(id);
    }
}
