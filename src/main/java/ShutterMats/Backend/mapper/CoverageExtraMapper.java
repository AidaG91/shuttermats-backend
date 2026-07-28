package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import org.springframework.stereotype.Component;

@Component
public class CoverageExtraMapper {

    public CoverageExtraResponseDTO toResponseDTO(CoverageExtra extra) {
        return new CoverageExtraResponseDTO(
                extra.getId(),
                extra.getName(),
                extra.getPrice()
        );
    }
}
