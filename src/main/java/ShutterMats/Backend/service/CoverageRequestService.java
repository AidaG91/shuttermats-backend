package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoverageRequestService {

    CoverageRequestResponseDTO create(CoverageRequestRequestDTO dto);

    Page<CoverageRequestResponseDTO> findAll(String status, Long eventId, Pageable pageable);

    CoverageRequestResponseDTO findById(Long id);
}
