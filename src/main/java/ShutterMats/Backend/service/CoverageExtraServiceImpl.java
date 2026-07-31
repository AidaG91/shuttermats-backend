package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import ShutterMats.Backend.exception.CoverageExtraNotFoundException;
import ShutterMats.Backend.mapper.CoverageExtraMapper;
import ShutterMats.Backend.repository.CoverageExtraRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CoverageExtraServiceImpl implements CoverageExtraService {

    private final CoverageExtraRepository coverageExtraRepository;
    private final CoverageExtraMapper coverageExtraMapper;

    public CoverageExtraServiceImpl(CoverageExtraRepository coverageExtraRepository,
                                     CoverageExtraMapper coverageExtraMapper) {
        this.coverageExtraRepository = coverageExtraRepository;
        this.coverageExtraMapper = coverageExtraMapper;
    }

    @Override
    public List<CoverageExtraResponseDTO> findAllActive() {
        return coverageExtraRepository.findByActiveTrue().stream()
                .map(coverageExtraMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Set<CoverageExtra> resolveByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }

        List<CoverageExtra> found = coverageExtraRepository.findAllById(ids);

        if (found.size() != ids.size()) {
            throw new CoverageExtraNotFoundException(ids);
        }

        return new HashSet<>(found);
    }
}
