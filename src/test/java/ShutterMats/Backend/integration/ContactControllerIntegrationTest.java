package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.request.ContactMessageRequestDTO;
import ShutterMats.Backend.entity.enums.ContactSubject;
import ShutterMats.Backend.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ContactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        contactMessageRepository.deleteAll();
    }

    @Test
    void createContactMessage_returns201_whenBodyIsValid() throws Exception {
        ContactMessageRequestDTO dto = new ContactMessageRequestDTO(
                "Marc Solé", "marc@example.com", "600333444", ContactSubject.COVERAGE_INQUIRY,
                "Hola, quería preguntar por disponibilidad para un evento en octubre.", true);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Marc Solé"))
                .andExpect(jsonPath("$.email").value("marc@example.com"))
                .andExpect(jsonPath("$.subject").value("COVERAGE_INQUIRY"))
                .andExpect(jsonPath("$.read").value(false));
    }

    @Test
    void createContactMessage_returns201_whenPhoneIsMissing() throws Exception {
        ContactMessageRequestDTO dto = new ContactMessageRequestDTO(
                "Laia Puig", "laia@example.com", null, ContactSubject.OTHER,
                "Consulta general sobre precios.", true);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").doesNotExist());
    }

    @Test
    void createContactMessage_returns400_whenNameIsBlank() throws Exception {
        ContactMessageRequestDTO dto = new ContactMessageRequestDTO(
                "", "marc@example.com", null, ContactSubject.OTHER, "Mensaje de prueba.", true);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void createContactMessage_returns400_whenEmailIsInvalid() throws Exception {
        ContactMessageRequestDTO dto = new ContactMessageRequestDTO(
                "Marc Solé", "no-es-un-email", null, ContactSubject.OTHER, "Mensaje de prueba.", true);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void createContactMessage_returns400_whenPrivacyNotAccepted() throws Exception {
        ContactMessageRequestDTO dto = new ContactMessageRequestDTO(
                "Marc Solé", "marc@example.com", null, ContactSubject.OTHER, "Mensaje de prueba.", false);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.privacyAccepted").exists());
    }

    @Test
    void createContactMessage_returns400_whenSubjectIsMissing() throws Exception {
        ContactMessageRequestDTO dto = new ContactMessageRequestDTO(
                "Marc Solé", "marc@example.com", null, null, "Mensaje de prueba.", true);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.subject").exists());
    }
}
