package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import ShutterMats.Backend.service.CoverageRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@Validated
public class CoverageRequestController {

    private final CoverageRequestService coverageRequestService;

    public CoverageRequestController(CoverageRequestService coverageRequestService) {
        this.coverageRequestService = coverageRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoverageRequestResponseDTO createRequest(@Valid @RequestBody CoverageRequestRequestDTO dto) {
        return coverageRequestService.create(dto);
    }
}
