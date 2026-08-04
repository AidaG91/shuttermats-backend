package ShutterMats.Backend.repository;

import ShutterMats.Backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT DISTINCT e.location FROM Event e ORDER BY e.location")
    List<String> findDistinctLocations();

    // Used when deleting a pricing plan: events that pointed to it keep
    // their already-snapshotted price, they just lose the plan label.
    @Modifying
    @Query("UPDATE Event e SET e.pricingPlan = null WHERE e.pricingPlan.id = :planId")
    void clearPricingPlanReferences(@Param("planId") Long planId);
}
