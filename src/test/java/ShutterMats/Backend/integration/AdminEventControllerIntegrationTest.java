package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.AdminLoginResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import ShutterMats.Backend.repository.PricingPlanRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AdminEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoverageRequestRepository coverageRequestRepository;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    private Event seededEvent;
    private Long defaultPlanId;
    private Long polarisPlanId;

    @BeforeEach
    void setup() {
        coverageRequestRepository.deleteAll();
        eventRepository.deleteAll();
        // Other test classes reuse the same @SpringBootTest context (and
        // its in-memory DB), so reset pricing plans here too and reseed
        // known ones for every test in this class.
        pricingPlanRepository.deleteAll();

        PricingPlan defaultPlan = new PricingPlan();
        defaultPlan.setName("General");
        defaultPlan.setBasePrice(new BigDecimal("35.00"));
        defaultPlan.setExtraMatchPrice(new BigDecimal("25.00"));
        defaultPlan.setIsDefault(true);
        defaultPlanId = pricingPlanRepository.save(defaultPlan).getId();

        PricingPlan polarisPlan = new PricingPlan();
        polarisPlan.setName("Polaris");
        polarisPlan.setBasePrice(new BigDecimal("60.00"));
        polarisPlan.setExtraMatchPrice(new BigDecimal("40.00"));
        polarisPlan.setIsDefault(false);
        polarisPlanId = pricingPlanRepository.save(polarisPlan).getId();

        seededEvent = eventRepository.save(Event.builder()
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .description("Desc")
                .basePrice(new BigDecimal("35.00"))
                .extraMatchPrice(new BigDecimal("25.00"))
                .build());
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

    private MockMultipartFile eventPart(EventRequestDTO dto) {
        return new MockMultipartFile(
                "event", "event.json", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );
    }

    @Test
    void createEvent_returns201_withValidAdminToken() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights",
                null, defaultPlanId
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Polaris Barcelona"))
                .andExpect(jsonPath("$.basePrice").value(35.00))
                .andExpect(jsonPath("$.extraMatchPrice").value(25.00))
                .andExpect(jsonPath("$.pricingPlanName").value("General"));
    }

    @Test
    void createEvent_snapshotsPickedPlan_notJustTheDefault() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights",
                "https://polarisbjj.com/events/polaris-barcelona", polarisPlanId
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basePrice").value(60.00))
                .andExpect(jsonPath("$.extraMatchPrice").value(40.00))
                .andExpect(jsonPath("$.pricingPlanName").value("Polaris"))
                .andExpect(jsonPath("$.registrationUrl").value("https://polarisbjj.com/events/polaris-barcelona"));
    }

    @Test
    void createEvent_returns400_whenPricingPlanIdIsMissing() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights", null, null
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_returns404_whenPricingPlanIdDoesNotExist() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights", null, 999999L
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEvent_isRejected_withoutToken() throws Exception {
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights",
                null, defaultPlanId
        );

        mockMvc.perform(multipart("/api/admin/events").file(eventPart(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createEvent_returns400_whenNameIsBlank() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO("", LocalDate.now(), "Barcelona", null, null, null, defaultPlanId);

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void createEvent_storesImage_andReturnsUploadedUrl() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights",
                null, defaultPlanId
        );
        MockMultipartFile image = new MockMultipartFile(
                "image", "cover.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .file(image)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.startsWith("/uploads/events/")));
    }

    @Test
    void createEvent_returns400_whenImageTypeIsNotSupported() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights",
                null, defaultPlanId
        );
        MockMultipartFile image = new MockMultipartFile(
                "image", "cover.gif", MediaType.IMAGE_GIF_VALUE, "fake-gif-bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .file(image)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEvent_returns200_andKeepsPreviousImage_whenNoNewImageSent() throws Exception {
        String token = obtainAdminToken();
        seededEvent.setImageUrl("/uploads/events/existing.jpg");
        eventRepository.save(seededEvent);

        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ Actualizado", seededEvent.getDate(), "Valencia", null, "Desc actualizada", null, null
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/admin/events/{id}", seededEvent.getId())
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Open BJJ Actualizado"))
                .andExpect(jsonPath("$.location").value("Valencia"))
                .andExpect(jsonPath("$.imageUrl").value("/uploads/events/existing.jpg"));
    }

    @Test
    void updateEvent_setsPricingPlan_whenPricingPlanIdProvided() throws Exception {
        String token = obtainAdminToken();

        EventRequestDTO dto = new EventRequestDTO(
                seededEvent.getName(), seededEvent.getDate(), seededEvent.getLocation(), null,
                seededEvent.getDescription(), null, polarisPlanId
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/admin/events/{id}", seededEvent.getId())
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(60.00))
                .andExpect(jsonPath("$.extraMatchPrice").value(40.00))
                .andExpect(jsonPath("$.pricingPlanName").value("Polaris"));
    }

    @Test
    void updateEvent_keepsExistingPrice_whenPricingPlanIdIsOmitted() throws Exception {
        String token = obtainAdminToken();

        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ Actualizado", seededEvent.getDate(), seededEvent.getLocation(), null,
                seededEvent.getDescription(), null, null
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/admin/events/{id}", seededEvent.getId())
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Open BJJ Actualizado"))
                .andExpect(jsonPath("$.basePrice").value(35.00))
                .andExpect(jsonPath("$.extraMatchPrice").value(25.00));
    }

    @Test
    void updateEvent_removesImage_whenImageUrlSentAsEmptyString() throws Exception {
        String token = obtainAdminToken();
        seededEvent.setImageUrl("/uploads/events/existing.jpg");
        eventRepository.save(seededEvent);

        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ Actualizado", seededEvent.getDate(), "Valencia", "", "Desc actualizada", null, null
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/admin/events/{id}", seededEvent.getId())
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void updateEvent_returns404_whenEventDoesNotExist() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO("X", LocalDate.now(), "Y", null, null, null, null);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/admin/events/{id}", 999999L)
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEvent_returns204_andRemovesEvent_withValidAdminToken() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(delete("/api/admin/events/{id}", seededEvent.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/events/{id}", seededEvent.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEvent_isRejected_withoutToken() throws Exception {
        mockMvc.perform(delete("/api/admin/events/{id}", seededEvent.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteEvent_returns404_whenEventDoesNotExist() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(delete("/api/admin/events/{id}", 999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
