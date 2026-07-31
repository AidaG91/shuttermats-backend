package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.response.AdminLoginResponseDTO;
import ShutterMats.Backend.entity.ContactMessage;
import ShutterMats.Backend.entity.enums.ContactSubject;
import ShutterMats.Backend.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminContactMessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    private ContactMessage seededMessage;

    @BeforeEach
    void setup() {
        contactMessageRepository.deleteAll();

        ContactMessage message = new ContactMessage();
        message.setName("Marc Solé");
        message.setEmail("marc@example.com");
        message.setPhone("600333444");
        message.setSubject(ContactSubject.COVERAGE_INQUIRY);
        message.setMessage("Consulta general sobre cobertura de un evento.");
        message.setPrivacyAccepted(true);
        message.setRead(false);
        seededMessage = contactMessageRepository.save(message);
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
    void getContactMessages_returns200AndList_withValidAdminToken() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/contact-messages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Marc Solé"));
    }

    @Test
    void getContactMessages_isRejected_withoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/contact-messages"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getContactMessages_filtersByRead_whenReadIsFalse() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/contact-messages")
                        .param("read", "false")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getContactMessages_returnsEmpty_whenReadIsTrueAndNoneMatch() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/contact-messages")
                        .param("read", "true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getContactMessage_returns200AndMessage_withValidIdAndToken() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/contact-messages/" + seededMessage.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marc Solé"));
    }

    @Test
    void getContactMessage_returns404_whenIdDoesNotExist() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/contact-messages/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getContactMessage_isRejected_withoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/contact-messages/" + seededMessage.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void markAsRead_returns200AndMarksMessageAsRead_withValidToken() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(patch("/api/admin/contact-messages/" + seededMessage.getId() + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));

        ContactMessage updated = contactMessageRepository.findById(seededMessage.getId()).orElseThrow();
        assertTrue(updated.getRead());
    }

    @Test
    void markAsRead_returns404_whenIdDoesNotExist() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(patch("/api/admin/contact-messages/999999/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsRead_isRejected_withoutToken() throws Exception {
        mockMvc.perform(patch("/api/admin/contact-messages/" + seededMessage.getId() + "/read"))
                .andExpect(status().is4xxClientError());
    }
}
