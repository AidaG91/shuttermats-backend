package ShutterMats.Backend.repository;

import ShutterMats.Backend.entity.CoverageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverageRequestRepository extends JpaRepository<CoverageRequest, Long> {
}
