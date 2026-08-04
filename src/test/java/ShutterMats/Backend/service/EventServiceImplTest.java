package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.exception.DefaultPricingPlanException;
import ShutterMats.Backend.mapper.EventMapper;
import ShutterMats.Backend.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private PricingPlanService pricingPlanService;

    @InjectMocks
    private EventServiceImpl eventService;

    private static EventResponseDTO sampleResponseDTO() {
        return new EventResponseDTO(
                1L, "Open BJJ", LocalDate.now(), "Madrid", null, null, null,
                new BigDecimal("35.00"), new BigDecimal("25.00"), null, null
        );
    }

    @Test
    void findAll_returnsPageOfEventResponseDTO() {
        Event event = Event.builder()
                .id(1L)
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .build();
        EventResponseDTO dto = sampleResponseDTO();
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
        EventResponseDTO dto = sampleResponseDTO();

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
            assertEquals("No se ha encontrado ningún evento con id 99", ex.getMessage());
        }
    }

    @Test
    void create_snapshotsChosenPlanOntoEvent() {
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris", LocalDate.now(), "Sabadell", null, "Superfights", null, 5L
        );
        Event mappedEvent = new Event();
        when(eventMapper.toEntity(dto)).thenReturn(mappedEvent);

        PricingPlan plan = mock(PricingPlan.class);
        when(plan.getBasePrice()).thenReturn(new BigDecimal("60.00"));
        when(plan.getExtraMatchPrice()).thenReturn(new BigDecimal("40.00"));
        when(pricingPlanService.getEntityById(5L)).thenReturn(plan);

        when(eventRepository.save(mappedEvent)).thenReturn(mappedEvent);
        when(eventMapper.toResponseDTO(mappedEvent)).thenReturn(sampleResponseDTO());

        eventService.create(dto);

        assertEquals(new BigDecimal("60.00"), mappedEvent.getBasePrice());
        assertEquals(new BigDecimal("40.00"), mappedEvent.getExtraMatchPrice());
        assertEquals(plan, mappedEvent.getPricingPlan());
    }

    @Test
    void create_throwsDefaultPricingPlanException_whenPricingPlanIdIsMissing() {
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris", LocalDate.now(), "Sabadell", null, "Superfights", null, null
        );
        when(eventMapper.toEntity(dto)).thenReturn(new Event());

        assertThrows(DefaultPricingPlanException.class, () -> eventService.create(dto));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_resnapshotsPrice_whenPricingPlanIdProvided() {
        Event existing = Event.builder().id(1L).name("Open BJJ").date(LocalDate.now()).location("Madrid")
                .basePrice(new BigDecimal("35.00")).extraMatchPrice(new BigDecimal("25.00")).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));

        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ", LocalDate.now(), "Madrid", null, "Desc", null, 5L
        );
        PricingPlan plan = mock(PricingPlan.class);
        when(plan.getBasePrice()).thenReturn(new BigDecimal("60.00"));
        when(plan.getExtraMatchPrice()).thenReturn(new BigDecimal("40.00"));
        when(pricingPlanService.getEntityById(5L)).thenReturn(plan);

        when(eventRepository.save(existing)).thenReturn(existing);
        when(eventMapper.toResponseDTO(existing)).thenReturn(sampleResponseDTO());

        eventService.update(1L, dto);

        assertEquals(new BigDecimal("60.00"), existing.getBasePrice());
        assertEquals(new BigDecimal("40.00"), existing.getExtraMatchPrice());
    }

    @Test
    void update_leavesExistingPriceUntouched_whenPricingPlanIdIsNull() {
        Event existing = Event.builder().id(1L).name("Open BJJ").date(LocalDate.now()).location("Madrid")
                .basePrice(new BigDecimal("35.00")).extraMatchPrice(new BigDecimal("25.00")).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));

        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ Actualizado", LocalDate.now(), "Madrid", null, "Desc nueva", null, null
        );

        when(eventRepository.save(existing)).thenReturn(existing);
        when(eventMapper.toResponseDTO(existing)).thenReturn(sampleResponseDTO());

        eventService.update(1L, dto);

        assertEquals(new BigDecimal("35.00"), existing.getBasePrice());
        assertEquals(new BigDecimal("25.00"), existing.getExtraMatchPrice());
        assertNull(existing.getPricingPlan());
        verify(pricingPlanService, never()).getEntityById(any());
    }
}
