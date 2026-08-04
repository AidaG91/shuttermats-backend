package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.exception.DefaultPricingPlanException;
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
    private final PricingPlanService pricingPlanService;

    public EventServiceImpl(
            EventRepository eventRepository,
            EventMapper eventMapper,
            PricingPlanService pricingPlanService
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.pricingPlanService = pricingPlanService;
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
        applyPricingPlan(event, dto.pricingPlanId(), true);
        Event saved = eventRepository.save(event);
        return eventMapper.toResponseDTO(saved);
    }

    @Override
    public EventResponseDTO update(Long id, EventRequestDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        eventMapper.updateEntity(event, dto);
        applyPricingPlan(event, dto.pricingPlanId(), false);

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

    // Snapshots the chosen plan's current prices onto the event. On create,
    // a plan is mandatory. On update, a null pricingPlanId means "leave the
    // event's price as it already is" - it does NOT reset to the default.
    private void applyPricingPlan(Event event, Long pricingPlanId, boolean required) {
        if (pricingPlanId == null) {
            if (required) {
                throw DefaultPricingPlanException.pricingPlanRequired();
            }
            return;
        }

        PricingPlan plan = pricingPlanService.getEntityById(pricingPlanId);
        event.setBasePrice(plan.getBasePrice());
        event.setExtraMatchPrice(plan.getExtraMatchPrice());
        event.setPricingPlan(plan);
    }
}
