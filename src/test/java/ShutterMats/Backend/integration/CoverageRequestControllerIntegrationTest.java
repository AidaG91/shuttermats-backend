package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.coveragerequest.AthleteInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.BillingInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CategoryInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ChampionshipInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ConfirmationsDTO;
import ShutterMats.Backend.dto.request.coveragerequest.LocateInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.PreferencesDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.enums.BeltCategory;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CoverageRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoverageRequestRepository coverageRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Event seededEvent;

    @BeforeEach
    void setup() {
        // This class creates real CoverageRequest rows via the API
        // (createRequest_returns201...), so we must clean up before touching
        // events or it breaks the FK for the next test class sharing this
        // in-memory H2 instance.
        coverageRequestRepository.deleteAll();
        eventRepository.deleteAll();

        seededEvent = eventRepository.save(Event.builder()
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .build());
    }

    @Test
    void createRequest_returns201_whenBodyIsValid() throws Exception {
        CoverageRequestRequestDTO dto = new CoverageRequestRequestDTO(
                new AthleteInfoDTO("Laia Puig", "laia@example.com", "600111222", null, null, null, null),
                new ChampionshipInfoDTO(seededEvent.getId(), "IBJJF", null),
                new CategoryInfoDTO("Pluma", BeltCategory.BLUE, Division.ADULT, CompetitionModality.BOTH),
                new LocateInfoDTO(null, null, null),
                null,
                new PreferencesDTO(null, null, null),
                new BillingInfoDTO(false, null, null, null, null),
                new ConfirmationsDTO(true, true)
        );

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.athleteName").value("Laia Puig"))
                .andExpect(jsonPath("$.event.name").value("Open BJJ"));
    }
}
