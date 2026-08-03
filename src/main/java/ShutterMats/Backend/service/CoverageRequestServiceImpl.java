package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.UpdateRequestStatusDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.exception.CoverageRequestNotFoundException;
import ShutterMats.Backend.mapper.CoverageRequestMapper;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.specifications.CoverageRequestSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
public class CoverageRequestServiceImpl implements CoverageRequestService {

    private final CoverageRequestRepository coverageRequestRepository;
    private final EventService eventService;
    private final CoverageExtraService coverageExtraService;
    private final CoverageRequestMapper coverageRequestMapper;

    public CoverageRequestServiceImpl(CoverageRequestRepository coverageRequestRepository,
                                       EventService eventService,
                                       CoverageExtraService coverageExtraService,
                                       CoverageRequestMapper coverageRequestMapper) {
        this.coverageRequestRepository = coverageRequestRepository;
        this.eventService = eventService;
        this.coverageExtraService = coverageExtraService;
        this.coverageRequestMapper = coverageRequestMapper;
    }

    @Override
    public CoverageRequestResponseDTO create(CoverageRequestRequestDTO dto) {
        Event event = eventService.getEntityById(dto.championship().eventId());

        Set<CoverageExtra> extras = coverageExtraService.resolveByIds(
                dto.coverage() != null ? dto.coverage().extraIds() : null);

        CoverageRequest request = coverageRequestMapper.toEntity(dto, event, extras);
        CoverageRequest saved = coverageRequestRepository.save(request);

        return coverageRequestMapper.toResponseDTO(saved);
    }

    @Override
    public Page<CoverageRequestResponseDTO> findAll(String status, Long eventId, Pageable pageable) {
        Specification<CoverageRequest> spec = Specification.allOf(
                CoverageRequestSpecifications.hasStatus(status),
                CoverageRequestSpecifications.hasEventId(eventId)
        );

        return coverageRequestRepository.findAll(spec, pageable)
                .map(coverageRequestMapper::toResponseDTO);
    }

    @Override
    public CoverageRequestResponseDTO findById(Long id) {
        CoverageRequest request = coverageRequestRepository.findById(id)
                .orElseThrow(() -> new CoverageRequestNotFoundException(id));

        return coverageRequestMapper.toResponseDTO(request);
    }

    @Override
    public CoverageRequestResponseDTO updateStatus(Long id, UpdateRequestStatusDTO dto) {
        CoverageRequest request = coverageRequestRepository.findById(id)
                .orElseThrow(() -> new CoverageRequestNotFoundException(id));

        request.setStatus(dto.status());

        // adminResponse is always optional (KAN-96): null -> leave untouched,
        // explicit "" -> clear it. Same convention as resolveImage in
        // AdminEventController for imageUrl.
        if (dto.adminResponse() != null) {
            request.setAdminResponse(StringUtils.hasText(dto.adminResponse()) ? dto.adminResponse() : null);
        }

        CoverageRequest saved = coverageRequestRepository.save(request);

        // TODO(coverage-requests): adminResponse is only persisted today,
        // the athlete isn't notified through any channel. Reuse EmailService
        // (already wired up for contact messages) to email the athlete when
        // the status changes, ideally with per-status templates
        // (CONFIRMED/REJECTED/...) the admin can pick/edit before sending.
        // Right now this is just a plain textarea in the frontend
        // (AdminRequestDetailPage).
        return coverageRequestMapper.toResponseDTO(saved);
    }
}
