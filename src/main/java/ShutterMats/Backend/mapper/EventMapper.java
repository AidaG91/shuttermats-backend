package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.service.PricingPlanService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EventMapper {

    private final PricingPlanService pricingPlanService;

    public EventMapper(PricingPlanService pricingPlanService) {
        this.pricingPlanService = pricingPlanService;
    }

    public EventResponseDTO toResponseDTO(Event event) {
        BigDecimal basePrice = event.getBasePrice();
        BigDecimal extraMatchPrice = event.getExtraMatchPrice();

        // Legacy safety net: events created before pricing plans existed
        // (or any other edge case that left these blank) fall back to
        // whatever the default plan currently charges, same as before.
        if (basePrice == null || extraMatchPrice == null) {
            PricingPlan defaultPlan = pricingPlanService.getDefaultEntity();
            if (basePrice == null) basePrice = defaultPlan.getBasePrice();
            if (extraMatchPrice == null) extraMatchPrice = defaultPlan.getExtraMatchPrice();
        }

        PricingPlan pricingPlan = event.getPricingPlan();

        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getLocation(),
                event.getImageUrl(),
                event.getDescription(),
                event.getRegistrationUrl(),
                basePrice,
                extraMatchPrice,
                pricingPlan != null ? pricingPlan.getId() : null,
                pricingPlan != null ? pricingPlan.getName() : null
        );
    }

    public Event toEntity(EventRequestDTO dto) {
        Event event = new Event();
        updateEntity(event, dto);
        return event;
    }

    // Only the fields that don't need extra lookups. Pricing plan snapshot
    // logic lives in EventServiceImpl - it needs to load the PricingPlan
    // entity and apply create-vs-update rules, which isn't a pure mapping
    // concern.
    public void updateEntity(Event event, EventRequestDTO dto) {
        event.setName(dto.name());
        event.setDate(dto.date());
        event.setLocation(dto.location());
        event.setImageUrl(dto.imageUrl());
        event.setDescription(dto.description());
        event.setRegistrationUrl(dto.registrationUrl());
    }
}
