package ShutterMats.Backend.repository;

import ShutterMats.Backend.entity.CoverageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CoverageRequestRepository
        extends JpaRepository<CoverageRequest, Long>, JpaSpecificationExecutor<CoverageRequest> {
}
