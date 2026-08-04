package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.service.PricingPlanService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class EventMapperTest {

    private final PricingPlanService pricingPlanService = mock(PricingPlanService.class);
    private final EventMapper mapper = new EventMapper(pricingPlanService);

    @Test
    void toResponseDTO_mapsCorrectly() {
        Event event = Event.builder()
                .id(1L)
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .registrationUrl("https://smoothcomp.com/en/event/1")
                .basePrice(new BigDecimal("35.00"))
                .extraMatchPrice(new BigDecimal("25.00"))
                .build();

        EventResponseDTO dto = mapper.toResponseDTO(event);

        assertEquals(event.getId(), dto.id());
        assertEquals(event.getName(), dto.name());
        assertEquals(event.getLocation(), dto.location());
        assertEquals(event.getRegistrationUrl(), dto.registrationUrl());
    }

    @Test
    void toResponseDTO_usesEventsOwnSnapshottedPrice_whenPresent() {
        Event event = Event.builder()
                .id(1L).name("Polaris").date(LocalDate.now()).location("Sabadell")
                .basePrice(new BigDecimal("60.00"))
                .extraMatchPrice(new BigDecimal("40.00"))
                .build();

        EventResponseDTO dto = mapper.toResponseDTO(event);

        assertEquals(new BigDecimal("60.00"), dto.basePrice());
        assertEquals(new BigDecimal("40.00"), dto.extraMatchPrice());
        verifyNoInteractions(pricingPlanService);
    }

    @Test
    void toResponseDTO_fallsBackToDefaultPlan_whenEventHasNoSnapshottedPrice() {
        Event event = Event.builder().id(1L).name("Open BJJ").date(LocalDate.now()).location("Madrid").build();

        PricingPlan defaultPlan = new PricingPlan();
        defaultPlan.setId(9L);
        defaultPlan.setName("General");
        defaultPlan.setBasePrice(new BigDecimal("35.00"));
        defaultPlan.setExtraMatchPrice(new BigDecimal("25.00"));
        defaultPlan.setIsDefault(true);
        when(pricingPlanService.getDefaultEntity()).thenReturn(defaultPlan);

        EventResponseDTO dto = mapper.toResponseDTO(event);

        assertEquals(new BigDecimal("35.00"), dto.basePrice());
        assertEquals(new BigDecimal("25.00"), dto.extraMatchPrice());
        verify(pricingPlanService).getDefaultEntity();
    }

    @Test
    void toResponseDTO_includesPricingPlanIdAndName_whenEventHasOne() {
        PricingPlan plan = new PricingPlan();
        plan.setId(3L);
        plan.setName("Polaris");
        plan.setBasePrice(new BigDecimal("60.00"));
        plan.setExtraMatchPrice(new BigDecimal("40.00"));

        Event event = Event.builder()
                .id(1L).name("Polaris Open").date(LocalDate.now()).location("Sabadell")
                .basePrice(new BigDecimal("60.00"))
                .extraMatchPrice(new BigDecimal("40.00"))
                .pricingPlan(plan)
                .build();

        EventResponseDTO dto = mapper.toResponseDTO(event);

        assertEquals(3L, dto.pricingPlanId());
        assertEquals("Polaris", dto.pricingPlanName());
    }

    @Test
    void toResponseDTO_pricingPlanFieldsAreNull_whenEventHasNoPlanReference() {
        Event event = Event.builder()
                .id(1L).name("Open BJJ").date(LocalDate.now()).location("Madrid")
                .basePrice(new BigDecimal("35.00"))
                .extraMatchPrice(new BigDecimal("25.00"))
                .build();

        EventResponseDTO dto = mapper.toResponseDTO(event);

        assertNull(dto.pricingPlanId());
        assertNull(dto.pricingPlanName());
    }

    @Test
    void toEntity_mapsCorrectly() {
        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ", LocalDate.now(), "Madrid", null, "Desc", null, null
        );

        Event event = mapper.toEntity(dto);

        assertEquals(dto.name(), event.getName());
        assertEquals(dto.location(), event.getLocation());
        assertNull(event.getBasePrice());
        assertNull(event.getPricingPlan());
    }

    @Test
    void toEntity_mapsRegistrationUrl() {
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris", LocalDate.now(), "Sabadell", null, "Superfights",
                "https://polarisbjj.com/events/polaris-open", 1L
        );

        Event event = mapper.toEntity(dto);

        assertEquals("https://polarisbjj.com/events/polaris-open", event.getRegistrationUrl());
    }
}
