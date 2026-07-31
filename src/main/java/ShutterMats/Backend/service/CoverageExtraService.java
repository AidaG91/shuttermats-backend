package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;

import java.util.List;
import java.util.Set;

public interface CoverageExtraService {

    List<CoverageExtraResponseDTO> findAllActive();

    /**
     * Resolves the managed entities for the given ids, for use by other
     * services that need to build a relation (e.g. CoverageRequest).
     * Returns an empty set when ids is null or empty.
     * Throws CoverageExtraNotFoundException if any id doesn't exist.
     */
    Set<CoverageExtra> resolveByIds(List<Long> ids);
}
