package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;
import ShutterMats.Backend.mapper.CoverageExtraMapper;
import ShutterMats.Backend.repository.CoverageExtraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
