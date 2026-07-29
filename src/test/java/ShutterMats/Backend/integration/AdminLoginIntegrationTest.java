package ShutterMats.Backend.integration;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Test
    void login_returns200AndToken_whenCredentialsAreValid() throws Exception {
        Map<String, String> body = Map.of("username", adminUsername, "password", "ShutterMats2026!");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_returns401_whenPasswordIsWrong() throws Exception {
        Map<String, String> body = Map.of("username", adminUsername, "password", "wrong-password");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void login_returns401_whenUsernameDoesNotExist() throws Exception {
        Map<String, String> body = Map.of("username", "no-existe", "password", "ShutterMats2026!");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedAdminRoute_isRejected_withoutToken() throws Exception {
             mockMvc.perform(get("/api/admin/anything"))
                .andExpect(status().is4xxClientError());
    }
}
