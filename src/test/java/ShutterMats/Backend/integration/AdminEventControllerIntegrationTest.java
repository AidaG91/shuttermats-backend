package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.AdminLoginResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
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

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Filtros activos (no addFilters = false): igual que AdminRequestsIntegrationTest,
 * necesitamos que el JwtAuthorizationFilter actue de verdad para probar que
 * /api/admin/events exige un token admin valido.
 */
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

    @Value("${app.admin.username}")
    private String adminUsername;

    private Event seededEvent;

    @BeforeEach
    void setup() {
        // Ver EventControllerIntegrationTest: limpiar CoverageRequest antes de
        // events evita romper la FK si otra clase dejo alguna colgando.
        coverageRequestRepository.deleteAll();
        eventRepository.deleteAll();

        seededEvent = eventRepository.save(Event.builder()
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .description("Desc")
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
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights"
        );

        mockMvc.perform(multipart("/api/admin/events")
                        .file(eventPart(dto))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Polaris Barcelona"));
    }

    @Test
    void createEvent_isRejected_withoutToken() throws Exception {
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights"
        );

        mockMvc.perform(multipart("/api/admin/events").file(eventPart(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createEvent_returns400_whenNameIsBlank() throws Exception {
        String token = obtainAdminToken();
        EventRequestDTO dto = new EventRequestDTO("", LocalDate.now(), "Barcelona", null, null);

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
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights"
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
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights"
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
                "Open BJJ Actualizado", seededEvent.getDate(), "Valencia", null, "Desc actualizada"
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
    void updateEvent_removesImage_whenImageUrlSentAsEmptyString() throws Exception {
        String token = obtainAdminToken();
        seededEvent.setImageUrl("/uploads/events/existing.jpg");
        eventRepository.save(seededEvent);

        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ Actualizado", seededEvent.getDate(), "Valencia", "", "Desc actualizada"
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
        EventRequestDTO dto = new EventRequestDTO("X", LocalDate.now(), "Y", null, null);

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
