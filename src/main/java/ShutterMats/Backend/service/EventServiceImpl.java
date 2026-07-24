package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.mapper.EventMapper;
import ShutterMats.Backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventResponseDTO> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toResponseDTO)
                .toList();
    }

    @Override
    public EventResponseDTO findById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toResponseDTO(event);
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
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventMapper.updateEntity(event, dto);

        Event updated = eventRepository.save(event);
        return eventMapper.toResponseDTO(updated);    }

    @Override
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }
}
