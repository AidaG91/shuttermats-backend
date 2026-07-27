package ShutterMats.Backend.controller;

import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;
import ShutterMats.Backend.service.CoverageExtraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/extras")
public class CoverageExtraController {

    private final CoverageExtraService coverageExtraService;

    public CoverageExtraController(CoverageExtraService coverageExtraService) {
        this.coverageExtraService = coverageExtraService;
    }

    @GetMapping
    public List<CoverageExtraResponseDTO> getExtras() {
        return coverageExtraService.findAllActive();
    }
}
