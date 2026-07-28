package ShutterMats.Backend.dto.response;

import java.math.BigDecimal;

public record CoverageExtraResponseDTO(
        Long id,
        String name,
        BigDecimal price
) {
}
