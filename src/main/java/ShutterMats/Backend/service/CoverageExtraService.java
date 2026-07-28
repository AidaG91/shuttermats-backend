package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;

import java.util.List;

public interface CoverageExtraService {

    List<CoverageExtraResponseDTO> findAllActive();
}
