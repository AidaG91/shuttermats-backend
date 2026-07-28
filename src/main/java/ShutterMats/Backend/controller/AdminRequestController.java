package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import ShutterMats.Backend.service.CoverageRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bajo /api/admin/**, protegido por SecurityConfig (hasRole ADMIN) - no
 * hace falta @PreAuthorize aqui, la cadena de filtros ya lo bloquea.
 */
@RestController
@RequestMapping("/api/admin/requests")
public class AdminRequestController {

    private final CoverageRequestService coverageRequestService;

    public AdminRequestController(CoverageRequestService coverageRequestService) {
        this.coverageRequestService = coverageRequestService;
    }

    @GetMapping
    public Page<CoverageRequestResponseDTO> getRequests(
            @RequestParam(required = false) String status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return coverageRequestService.findAll(status, pageable);
    }

    @GetMapping("/{id}")
    public CoverageRequestResponseDTO getRequest(@PathVariable Long id) {
        return coverageRequestService.findById(id);
    }
}
