package ShutterMats.Backend.repository;

import ShutterMats.Backend.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PricingPlanRepository extends JpaRepository<PricingPlan, Long> {

    List<PricingPlan> findAllByOrderByIsDefaultDescNameAsc();

    Optional<PricingPlan> findFirstByIsDefaultTrue();
}
