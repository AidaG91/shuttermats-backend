package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventMapperTest {

    private final EventMapper mapper = new EventMapper();

    @Test
    void toResponseDTO_mapsCorrectly() {
        Event event = new Event(1L, "Open BJJ", LocalDate.now(), "Madrid", null, null);

        EventResponseDTO dto = mapper.toResponseDTO(event);

        assertEquals(event.getId(), dto.id());
        assertEquals(event.getName(), dto.name());
        assertEquals(event.getLocation(), dto.location());
    }

    @Test
    void toEntity_mapsCorrectly() {
        EventRequestDTO dto = new EventRequestDTO("Open BJJ", LocalDate.now(), "Madrid", null, "Desc");

        Event event = mapper.toEntity(dto);

        assertEquals(dto.name(), event.getName());
        assertEquals(dto.location(), event.getLocation());
    }
}
