package ShutterMats.Backend.repository;

import ShutterMats.Backend.entity.CoverageExtra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoverageExtraRepository extends JpaRepository<CoverageExtra, Long> {

    List<CoverageExtra> findByActiveTrue();
}
