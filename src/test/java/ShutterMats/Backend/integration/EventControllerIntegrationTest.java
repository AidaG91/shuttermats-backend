package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.entity.Event;
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
    private ObjectMapper objectMapper;

    private Event seededEvent;

    @BeforeEach
    void setup() {
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

    @Test
    void createEvent_returns201_whenBodyIsValid() throws Exception {
        EventRequestDTO dto = new EventRequestDTO(
                "Polaris Barcelona", LocalDate.now().plusMonths(1), "Barcelona", null, "Superfights"
        );

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Polaris Barcelona"));
    }

    @Test
    void createEvent_returns400_whenNameIsBlank() throws Exception {
        EventRequestDTO dto = new EventRequestDTO("", LocalDate.now(), "Barcelona", null, null);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void createEvent_returns400_whenDateIsMissing() throws Exception {
        String body = """
                {"name":"Polaris Barcelona","location":"Barcelona"}
                """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.date").exists());
    }

    @Test
    void updateEvent_returns200_whenEventExists() throws Exception {
        EventRequestDTO dto = new EventRequestDTO(
                "Open BJJ Actualizado", seededEvent.getDate(), "Valencia", null, "Desc actualizada"
        );

        mockMvc.perform(put("/api/events/{id}", seededEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Open BJJ Actualizado"))
                .andExpect(jsonPath("$.location").value("Valencia"));
    }

    @Test
    void updateEvent_returns404_whenEventDoesNotExist() throws Exception {
        EventRequestDTO dto = new EventRequestDTO("X", LocalDate.now(), "Y", null, null);

        mockMvc.perform(put("/api/events/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEvent_returns204_andRemovesEvent() throws Exception {
        mockMvc.perform(delete("/api/events/{id}", seededEvent.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/events/{id}", seededEvent.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEvent_returns404_whenEventDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/events/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}
