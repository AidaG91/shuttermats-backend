package ShutterMats.Backend.repository.specifications;

import ShutterMats.Backend.entity.Event;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventSpecificationsTest {

    @Test
    void hasStatus_returnsNonNullSpecification_forUpcomingPastAndOther() {
        assertNotNull(EventSpecifications.hasStatus("upcoming"));
        assertNotNull(EventSpecifications.hasStatus("past"));
        assertNotNull(EventSpecifications.hasStatus("all"));
        assertNotNull(EventSpecifications.hasStatus(null));
    }

    @Test
    void hasLocation_returnsNonNullSpecification_forBlankAndNonBlankValues() {
        Specification<Event> blank = EventSpecifications.hasLocation("");
        Specification<Event> nullLocation = EventSpecifications.hasLocation(null);
        Specification<Event> withValue = EventSpecifications.hasLocation("Madrid");

        assertNotNull(blank);
        assertNotNull(nullLocation);
        assertNotNull(withValue);
    }
}
