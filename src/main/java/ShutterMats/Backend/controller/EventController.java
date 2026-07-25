package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
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
    public EventResponseDTO getEvent(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PostMapping
    public EventResponseDTO createEvent(@RequestBody EventRequestDTO dto) {
        return eventService.create(dto);
    }

    @PutMapping("/{id}")
    public EventResponseDTO updateEvent(@PathVariable Long id, @RequestBody EventRequestDTO dto) {
        return eventService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }
}
