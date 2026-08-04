package ShutterMats.Backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EventResponseDTO(
        Long id,
        String name,
        LocalDate date,
        String location,
        String imageUrl,
        String description,
        String registrationUrl,

        // Always populated (falls back to the default plan's current price
        // for legacy rows created before pricing plans existed).
        BigDecimal basePrice,
        BigDecimal extraMatchPrice,

        // Which plan these prices came from, for display purposes only
        // (e.g. "Polaris" badge in the admin events table). Null if the
        // event predates pricing plans, or that plan was since deleted -
        // basePrice/extraMatchPrice above are unaffected either way.
        Long pricingPlanId,
        String pricingPlanName
) {
}
