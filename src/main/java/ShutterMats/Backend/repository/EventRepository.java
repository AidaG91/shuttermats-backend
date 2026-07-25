package ShutterMats.Backend.repository;

import ShutterMats.Backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT DISTINCT e.location FROM Event e ORDER BY e.location")
    List<String> findDistinctLocations();
}
