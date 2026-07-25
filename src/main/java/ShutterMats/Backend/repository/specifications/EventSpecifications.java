package ShutterMats.Backend.repository.specifications;

import ShutterMats.Backend.entity.Event;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EventSpecifications {

    private EventSpecifications() {}

    public static Specification<Event> hasStatus(String status) {
        LocalDate today = LocalDate.now();

        if ("upcoming".equals(status)) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), today);
        }
        if ("past".equals(status)) {
            return (root, query, cb) -> cb.lessThan(root.get("date"), today);
        }
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Event> hasLocation(String location) {
        if (location == null || location.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("location"), location);
    }
}