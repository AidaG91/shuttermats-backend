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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void findAll_returnsPageOfEventResponseDTO() {
        Event event = Event.builder()
                .id(1L)
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .build();
        EventResponseDTO dto = new EventResponseDTO(1L, "Open BJJ", LocalDate.now(), "Madrid", null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event), pageable, 1);

        when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(eventPage);
        when(eventMapper.toResponseDTO(event)).thenReturn(dto);

        Page<EventResponseDTO> result = eventService.findAll("upcoming", "Madrid", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Open BJJ", result.getContent().get(0).name());
        verify(eventRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(eventMapper).toResponseDTO(event);
    }

    @Test
    void findAllLocations_returnsDistinctLocations() {
        when(eventRepository.findDistinctLocations()).thenReturn(List.of("Madrid", "Valencia"));

        List<String> result = eventService.findAllLocations();

        assertEquals(2, result.size());
        assertTrue(result.contains("Madrid"));
        verify(eventRepository).findDistinctLocations();
    }

    @Test
    void findById_returnsEventResponseDTO_whenEventExists() {
        Event event = Event.builder().id(1L).name("Open BJJ").date(LocalDate.now()).location("Madrid").build();
        EventResponseDTO dto = new EventResponseDTO(1L, "Open BJJ", LocalDate.now(), "Madrid", null, null);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDTO(event)).thenReturn(dto);

        EventResponseDTO result = eventService.findById(1L);

        assertEquals("Open BJJ", result.name());
    }

    @Test
    void findById_throws_whenEventDoesNotExist() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        try {
            eventService.findById(99L);
            assertTrue(false, "Expected RuntimeException was not thrown");
        } catch (RuntimeException ex) {
            assertEquals("Event not found", ex.getMessage());
        }
    }
}
