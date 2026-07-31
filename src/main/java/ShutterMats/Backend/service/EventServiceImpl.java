package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.exception.EventNotFoundException;
import ShutterMats.Backend.mapper.EventMapper;
import ShutterMats.Backend.repository.EventRepository;
import ShutterMats.Backend.repository.specifications.EventSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Page<EventResponseDTO> findAll(String status, String location, Pageable pageable) {
        Specification<Event> spec = Specification.allOf(
                EventSpecifications.hasStatus(status),
                EventSpecifications.hasLocation(location)
        );

        return eventRepository.findAll(spec, pageable)
                .map(eventMapper::toResponseDTO);
    }

    @Override
    public List<String> findAllLocations() {
        return eventRepository.findDistinctLocations();
    }

    @Override
    public EventResponseDTO findById(Long id) {
        return eventMapper.toResponseDTO(getEntityById(id));
    }

    @Override
    public EventResponseDTO create(EventRequestDTO dto) {
        Event event = eventMapper.toEntity(dto);
        Event saved = eventRepository.save(event);
        return eventMapper.toResponseDTO(saved);
    }

    @Override
    public EventResponseDTO update(Long id, EventRequestDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        eventMapper.updateEntity(event, dto);

        Event updated = eventRepository.save(event);
        return eventMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public Event getEntityById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }
}
