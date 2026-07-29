package ShutterMats.Backend.integration;

import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoverageRequestRepository coverageRequestRepository;

    private Event seededEvent;

    @BeforeEach
    void setup() {
        coverageRequestRepository.deleteAll();
        eventRepository.deleteAll();

        seededEvent = eventRepository.save(Event.builder()
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .description("Desc")
                .build());
    }

    @Test
    void getEvents_returns200AndList() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Open BJJ"));
    }

    @Test
    void getEvent_returns200_whenEventExists() throws Exception {
        mockMvc.perform(get("/api/events/{id}", seededEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Open BJJ"));
    }

    @Test
    void getEvent_returns404_whenEventDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/events/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
