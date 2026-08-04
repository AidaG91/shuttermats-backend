package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.PricingPlanRequestDTO;
import ShutterMats.Backend.dto.response.PricingPlanResponseDTO;
import ShutterMats.Backend.entity.PricingPlan;

import java.util.List;

public interface PricingPlanService {

    List<PricingPlanResponseDTO> findAll();

    PricingPlanResponseDTO create(PricingPlanRequestDTO dto);

    PricingPlanResponseDTO update(Long id, PricingPlanRequestDTO dto);

    void delete(Long id);

    /** Entity lookup for other services (e.g. EventServiceImpl) to snapshot a plan's prices. */
    PricingPlan getEntityById(Long id);

    /** The current default plan, lazily creating a 35/25 "General" one if none exists yet. */
    PricingPlan getDefaultEntity();
}
