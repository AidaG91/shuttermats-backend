package ShutterMats.Backend.repository.specifications;

import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.enums.RequestStatus;
import org.springframework.data.jpa.domain.Specification;

public class CoverageRequestSpecifications {

    private CoverageRequestSpecifications() {}

    public static Specification<CoverageRequest> hasStatus(String status) {
        if (status == null || status.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        RequestStatus parsed;
        try {
            parsed = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // estado desconocido -> no filtramos por status en vez de petar
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> cb.equal(root.get("status"), parsed);
    }

    public static Specification<CoverageRequest> hasEventId(Long eventId) {
        if (eventId == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> cb.equal(root.get("event").get("id"), eventId);
    }
}
