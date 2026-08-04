package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.PricingPlanRequestDTO;
import ShutterMats.Backend.dto.response.PricingPlanResponseDTO;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.exception.DefaultPricingPlanException;
import ShutterMats.Backend.exception.PricingPlanNotFoundException;
import ShutterMats.Backend.repository.EventRepository;
import ShutterMats.Backend.repository.PricingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingPlanServiceImpl implements PricingPlanService {

    // Only used to seed a plan the very first time getDefaultEntity() is
    // called and no plan exists at all yet (fresh/test database). Once a
    // default plan exists, it - not these constants - is the source of
    // truth.
    private static final String FALLBACK_NAME = "General";
    private static final BigDecimal FALLBACK_BASE_PRICE = new BigDecimal("35.00");
    private static final BigDecimal FALLBACK_EXTRA_MATCH_PRICE = new BigDecimal("25.00");

    private final PricingPlanRepository pricingPlanRepository;
    private final EventRepository eventRepository;

    public PricingPlanServiceImpl(PricingPlanRepository pricingPlanRepository, EventRepository eventRepository) {
        this.pricingPlanRepository = pricingPlanRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<PricingPlanResponseDTO> findAll() {
        return pricingPlanRepository.findAllByOrderByIsDefaultDescNameAsc().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public PricingPlanResponseDTO create(PricingPlanRequestDTO dto) {
        PricingPlan plan = new PricingPlan();
        plan.setName(dto.name());
        plan.setBasePrice(dto.basePrice());
        plan.setExtraMatchPrice(dto.extraMatchPrice());
        plan.setIsDefault(false);

        if (Boolean.TRUE.equals(dto.isDefault())) {
            promoteToDefault(plan);
        }

        return toResponseDTO(pricingPlanRepository.save(plan));
    }

    @Override
    @Transactional
    public PricingPlanResponseDTO update(Long id, PricingPlanRequestDTO dto) {
        PricingPlan plan = getEntityById(id);
        plan.setName(dto.name());
        plan.setBasePrice(dto.basePrice());
        plan.setExtraMatchPrice(dto.extraMatchPrice());

        if (Boolean.TRUE.equals(dto.isDefault())) {
            promoteToDefault(plan);
        }

        return toResponseDTO(pricingPlanRepository.save(plan));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PricingPlan plan = getEntityById(id);
        if (Boolean.TRUE.equals(plan.getIsDefault())) {
            throw DefaultPricingPlanException.cannotDeleteDefault();
        }

        eventRepository.clearPricingPlanReferences(id);
        pricingPlanRepository.delete(plan);
    }

    @Override
    public PricingPlan getEntityById(Long id) {
        return pricingPlanRepository.findById(id)
                .orElseThrow(() -> new PricingPlanNotFoundException(id));
    }

    @Override
    @Transactional
    public PricingPlan getDefaultEntity() {
        return pricingPlanRepository.findFirstByIsDefaultTrue()
                .orElseGet(() -> {
                    PricingPlan fallback = new PricingPlan();
                    fallback.setName(FALLBACK_NAME);
                    fallback.setBasePrice(FALLBACK_BASE_PRICE);
                    fallback.setExtraMatchPrice(FALLBACK_EXTRA_MATCH_PRICE);
                    fallback.setIsDefault(true);
                    return pricingPlanRepository.save(fallback);
                });
    }

    // Unsets whichever plan is currently default (if any, and if it isn't
    // this same plan) before marking `plan` as the new default - keeps the
    // "exactly one default plan" invariant.
    private void promoteToDefault(PricingPlan plan) {
        pricingPlanRepository.findFirstByIsDefaultTrue().ifPresent(currentDefault -> {
            if (!currentDefault.getId().equals(plan.getId())) {
                currentDefault.setIsDefault(false);
                pricingPlanRepository.save(currentDefault);
            }
        });
        plan.setIsDefault(true);
    }

    private PricingPlanResponseDTO toResponseDTO(PricingPlan plan) {
        return new PricingPlanResponseDTO(
                plan.getId(), plan.getName(), plan.getBasePrice(), plan.getExtraMatchPrice(), plan.getIsDefault()
        );
    }
}
