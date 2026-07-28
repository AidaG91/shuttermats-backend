package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;

public interface CoverageRequestService {

    CoverageRequestResponseDTO create(CoverageRequestRequestDTO dto);
}
