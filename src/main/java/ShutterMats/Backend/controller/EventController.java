package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@Valid @RequestBody EventRequestDTO dto) {
        return eventService.create(dto);
    }

    @PutMapping("/{id}")
    public EventResponseDTO updateEvent(@PathVariable @Positive Long id, @Valid @RequestBody EventRequestDTO dto) {
        return eventService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable @Positive Long id) {
        eventService.delete(id);
    }
}
