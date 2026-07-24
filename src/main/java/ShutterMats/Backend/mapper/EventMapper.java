package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;

public class EventMapper {

    public EventResponseDTO toResponseDTO (Event event){
        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getLocation(),
                event.getImageUrl(),
                event.getDescription()
        );
    }

    public Event toEntity(EventRequestDTO dto) {
        Event event = new Event();
        event.setName(dto.name());
        event.setDate(dto.date());
        event.setLocation(dto.location());
        event.setImageUrl(dto.imageUrl());
        event.setDescription(dto.description());
        return event;
    }

    public void updateEntity(Event event, EventRequestDTO dto) {
        event.setName(dto.name());
        event.setDate(dto.date());
        event.setLocation(dto.location());
        event.setImageUrl(dto.imageUrl());
        event.setDescription(dto.description());
    }
}
