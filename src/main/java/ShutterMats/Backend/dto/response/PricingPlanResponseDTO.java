package ShutterMats.Backend.dto.response;

import java.math.BigDecimal;

public record PricingPlanResponseDTO(
        Long id,
        String name,
        BigDecimal basePrice,
        BigDecimal extraMatchPrice,
        Boolean isDefault
) {
}
