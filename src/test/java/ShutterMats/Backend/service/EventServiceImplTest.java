package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.mapper.EventMapper;
import ShutterMats.Backend.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void findAll_returnListOfEventResponseDTO() {
        Event event = new Event(1L, "Open BJJ", LocalDate.now(), "Madrid", null, null);
        EventResponseDTO dto = new EventResponseDTO(1L, "Open BJJ", LocalDate.now(), "Madrid", null, null);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(dto);

        List<EventResponseDTO> result = eventService.findAll();

        assertEquals(1, result.size());
        assertEquals("Open BJJ", result.get(0).name());
        verify(eventRepository).findAll();
        verify(eventMapper).toResponseDTO(event);
    }
}
