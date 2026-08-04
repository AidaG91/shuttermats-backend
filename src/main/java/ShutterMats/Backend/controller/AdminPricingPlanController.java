package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.request.PricingPlanRequestDTO;
import ShutterMats.Backend.dto.response.PricingPlanResponseDTO;
import ShutterMats.Backend.service.PricingPlanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pricing-plans")
@Validated
public class AdminPricingPlanController {

    private final PricingPlanService pricingPlanService;

    public AdminPricingPlanController(PricingPlanService pricingPlanService) {
        this.pricingPlanService = pricingPlanService;
    }

    @GetMapping
    public List<PricingPlanResponseDTO> getPricingPlans() {
        return pricingPlanService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PricingPlanResponseDTO createPricingPlan(@Valid @RequestBody PricingPlanRequestDTO dto) {
        return pricingPlanService.create(dto);
    }

    @PutMapping("/{id}")
    public PricingPlanResponseDTO updatePricingPlan(
            @PathVariable @Positive Long id,
            @Valid @RequestBody PricingPlanRequestDTO dto
    ) {
        return pricingPlanService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePricingPlan(@PathVariable @Positive Long id) {
        pricingPlanService.delete(id);
    }
}
