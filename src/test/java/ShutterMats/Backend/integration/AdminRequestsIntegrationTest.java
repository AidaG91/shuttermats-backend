package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.response.AdminLoginResponseDTO;
import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import ShutterMats.Backend.entity.enums.RequestStatus;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Filtros activos (no addFilters = false): necesitamos que el
 * JwtAuthorizationFilter actue de verdad para probar que /api/admin/requests
 * exige un token admin valido.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminRequestsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoverageRequestRepository coverageRequestRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    private Event seededEvent;

    @BeforeEach
    void setup() {
        coverageRequestRepository.deleteAll();
        eventRepository.deleteAll();

        seededEvent = eventRepository.save(Event.builder()
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .build());

        CoverageRequest request = new CoverageRequest();
        request.setAthleteName("Laia Puig");
        request.setAthleteEmail("laia@example.com");
        request.setAthletePhone("600111222");
        request.setEvent(seededEvent);
        request.setDivision(Division.ADULT);
        request.setModality(CompetitionModality.BOTH);
        request.setTermsAccepted(true);
        request.setStatus(RequestStatus.PENDING);
        coverageRequestRepository.save(request);
    }

    private CoverageRequest firstSavedRequest() {
        return coverageRequestRepository.findAll().get(0);
    }

    private CoverageRequest saveRequestForNewEvent(String athleteName, String eventName) {
        Event event = eventRepository.save(Event.builder()
                .name(eventName)
                .date(LocalDate.now().plusDays(30))
                .location("Sabadell")
                .build());

        CoverageRequest request = new CoverageRequest();
        request.setAthleteName(athleteName);
        request.setAthleteEmail("marc@example.com");
        request.setAthletePhone("600333444");
        request.setEvent(event);
        request.setDivision(Division.ADULT);
        request.setModality(CompetitionModality.BOTH);
        request.setTermsAccepted(true);
        request.setStatus(RequestStatus.PENDING);
        return coverageRequestRepository.save(request);
    }

    private String obtainAdminToken() throws Exception {
        Map<String, String> body = Map.of("username", adminUsername, "password", "ShutterMats2026!");

        String response = mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AdminLoginResponseDTO loginResponse = objectMapper.readValue(response, AdminLoginResponseDTO.class);
        return loginResponse.token();
    }

    @Test
    void getRequests_returns200AndList_withValidAdminToken() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/requests")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].athleteName").value("Laia Puig"));
    }

    @Test
    void getRequests_isRejected_withoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/requests"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getRequests_filtersByStatus_whenStatusMatches() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/requests")
                        .param("status", "PENDING")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getRequests_returnsEmpty_whenStatusDoesNotMatch() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/requests")
                        .param("status", "CONFIRMED")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getRequests_filtersByEventId_whenEventIdMatches() throws Exception {
        String token = obtainAdminToken();
        saveRequestForNewEvent("Marc Solé", "Polaris Open");

        mockMvc.perform(get("/api/admin/requests")
                        .param("eventId", seededEvent.getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].athleteName").value("Laia Puig"));
    }

    @Test
    void getRequests_returnsEmpty_whenEventIdDoesNotMatch() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/requests")
                        .param("eventId", "999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getRequest_returns200AndRequest_withValidIdAndToken() throws Exception {
        String token = obtainAdminToken();
        Long id = firstSavedRequest().getId();

        mockMvc.perform(get("/api/admin/requests/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteName").value("Laia Puig"));
    }

    @Test
    void getRequest_returns404_whenIdDoesNotExist() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/requests/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequest_isRejected_withoutToken() throws Exception {
        Long id = firstSavedRequest().getId();

        mockMvc.perform(get("/api/admin/requests/" + id))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateStatus_returns200AndUpdatesStatusAndResponse_withValidBody() throws Exception {
        String token = obtainAdminToken();
        Long id = firstSavedRequest().getId();

        Map<String, String> body = Map.of("status", "CONFIRMED", "adminResponse", "Todo listo, nos vemos allí");

        mockMvc.perform(patch("/api/admin/requests/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.adminResponse").value("Todo listo, nos vemos allí"));

        CoverageRequest updated = coverageRequestRepository.findById(id).orElseThrow();
        assertEquals(RequestStatus.CONFIRMED, updated.getStatus());
        assertEquals("Todo listo, nos vemos allí", updated.getAdminResponse());
    }

    @Test
    void updateStatus_allowsAnyTransition_withoutAdminResponse() throws Exception {
        String token = obtainAdminToken();
        Long id = firstSavedRequest().getId();

        Map<String, String> body = Map.of("status", "DELIVERED");

        mockMvc.perform(patch("/api/admin/requests/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void updateStatus_returns400_whenStatusIsInvalid() throws Exception {
        String token = obtainAdminToken();
        Long id = firstSavedRequest().getId();

        Map<String, String> body = Map.of("status", "NOT_A_REAL_STATUS");

        mockMvc.perform(patch("/api/admin/requests/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_returns400_whenStatusIsMissing() throws Exception {
        String token = obtainAdminToken();
        Long id = firstSavedRequest().getId();

        mockMvc.perform(patch("/api/admin/requests/" + id + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_returns404_whenIdDoesNotExist() throws Exception {
        String token = obtainAdminToken();

        Map<String, String> body = Map.of("status", "CONFIRMED");

        mockMvc.perform(patch("/api/admin/requests/999999/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_isRejected_withoutToken() throws Exception {
        Long id = firstSavedRequest().getId();
        Map<String, String> body = Map.of("status", "CONFIRMED");

        mockMvc.perform(patch("/api/admin/requests/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is4xxClientError());
    }
}
