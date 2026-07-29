package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.UpdateRequestStatusDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CoverageInfoDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.exception.CoverageExtraNotFoundException;
import ShutterMats.Backend.exception.CoverageRequestNotFoundException;
import ShutterMats.Backend.exception.EventNotFoundException;
import ShutterMats.Backend.mapper.CoverageRequestMapper;
import ShutterMats.Backend.repository.CoverageExtraRepository;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import ShutterMats.Backend.repository.specifications.CoverageRequestSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CoverageRequestServiceImpl implements CoverageRequestService {

    private final CoverageRequestRepository coverageRequestRepository;
    private final EventRepository eventRepository;
    private final CoverageExtraRepository coverageExtraRepository;
    private final CoverageRequestMapper coverageRequestMapper;

    public CoverageRequestServiceImpl(CoverageRequestRepository coverageRequestRepository,
                                       EventRepository eventRepository,
                                       CoverageExtraRepository coverageExtraRepository,
                                       CoverageRequestMapper coverageRequestMapper) {
        this.coverageRequestRepository = coverageRequestRepository;
        this.eventRepository = eventRepository;
        this.coverageExtraRepository = coverageExtraRepository;
        this.coverageRequestMapper = coverageRequestMapper;
    }

    @Override
    public CoverageRequestResponseDTO create(CoverageRequestRequestDTO dto) {
        Event event = eventRepository.findById(dto.championship().eventId())
                .orElseThrow(() -> new EventNotFoundException(dto.championship().eventId()));

        Set<CoverageExtra> extras = resolveExtras(dto.coverage());

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

        // adminResponse siempre opcional (KAN-96): null -> no se toca,
        // "" explicito -> se borra. Mismo criterio que resolveImage en
        // AdminEventController para imageUrl.
        if (dto.adminResponse() != null) {
            request.setAdminResponse(StringUtils.hasText(dto.adminResponse()) ? dto.adminResponse() : null);
        }

        CoverageRequest saved = coverageRequestRepository.save(request);
        return coverageRequestMapper.toResponseDTO(saved);
    }

    private Set<CoverageExtra> resolveExtras(CoverageInfoDTO coverage) {
        if (coverage == null || coverage.extraIds() == null || coverage.extraIds().isEmpty()) {
            return new HashSet<>();
        }

        List<Long> extraIds = coverage.extraIds();
        List<CoverageExtra> found = coverageExtraRepository.findAllById(extraIds);

        if (found.size() != extraIds.size()) {
            throw new CoverageExtraNotFoundException(extraIds);
        }

        return new HashSet<>(found);
    }
}
