package ShutterMats.Backend.integration;

import ShutterMats.Backend.dto.response.AdminLoginResponseDTO;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import ShutterMats.Backend.repository.PricingPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminPricingPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PricingPlanRepository pricingPlanRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoverageRequestRepository coverageRequestRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    private Long defaultPlanId;

    @BeforeEach
    void setup() {
        coverageRequestRepository.deleteAll();
        eventRepository.deleteAll();
        pricingPlanRepository.deleteAll();

        PricingPlan defaultPlan = new PricingPlan();
        defaultPlan.setName("General");
        defaultPlan.setBasePrice(new BigDecimal("35.00"));
        defaultPlan.setExtraMatchPrice(new BigDecimal("25.00"));
        defaultPlan.setIsDefault(true);
        defaultPlanId = pricingPlanRepository.save(defaultPlan).getId();
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
    void getPricingPlans_returnsSeededDefault_withValidToken() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(get("/api/admin/pricing-plans")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("General"))
                .andExpect(jsonPath("$[0].isDefault").value(true));
    }

    @Test
    void getPricingPlans_isRejected_withoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/pricing-plans"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createPricingPlan_returns201_andDoesNotTouchDefault_whenNotMarkedDefault() throws Exception {
        String token = obtainAdminToken();
        Map<String, Object> body = Map.of(
                "name", "Polaris",
                "basePrice", new BigDecimal("60.00"),
                "extraMatchPrice", new BigDecimal("40.00"),
                "isDefault", false
        );

        mockMvc.perform(post("/api/admin/pricing-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Polaris"))
                .andExpect(jsonPath("$.isDefault").value(false));

        // findAll orders isDefault desc, so General (still the default) stays first.
        mockMvc.perform(get("/api/admin/pricing-plans").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$[0].name").value("General"))
                .andExpect(jsonPath("$[0].isDefault").value(true));
    }

    @Test
    void createPricingPlan_returns400_whenNameIsBlank() throws Exception {
        String token = obtainAdminToken();
        Map<String, Object> body = Map.of(
                "name", "",
                "basePrice", new BigDecimal("60.00"),
                "extraMatchPrice", new BigDecimal("40.00")
        );

        mockMvc.perform(post("/api/admin/pricing-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void updatePricingPlan_promotesToDefault_andDemotesPrevious() throws Exception {
        String token = obtainAdminToken();
        Map<String, Object> createBody = Map.of(
                "name", "Polaris",
                "basePrice", new BigDecimal("60.00"),
                "extraMatchPrice", new BigDecimal("40.00")
        );
        String createResponse = mockMvc.perform(post("/api/admin/pricing-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andReturn().getResponse().getContentAsString();
        Long polarisId = objectMapper.readTree(createResponse).get("id").asLong();

        Map<String, Object> updateBody = Map.of(
                "name", "Polaris",
                "basePrice", new BigDecimal("60.00"),
                "extraMatchPrice", new BigDecimal("40.00"),
                "isDefault", true
        );
        mockMvc.perform(put("/api/admin/pricing-plans/{id}", polarisId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));

        assertTrue(pricingPlanRepository.findById(defaultPlanId).orElseThrow().getIsDefault().equals(false));
    }

    @Test
    void deletePricingPlan_returns400_whenPlanIsDefault() throws Exception {
        String token = obtainAdminToken();

        mockMvc.perform(delete("/api/admin/pricing-plans/{id}", defaultPlanId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePricingPlan_succeeds_andKeepsEventPrice_butClearsPlanReference() throws Exception {
        String token = obtainAdminToken();
        PricingPlan polaris = new PricingPlan();
        polaris.setName("Polaris");
        polaris.setBasePrice(new BigDecimal("60.00"));
        polaris.setExtraMatchPrice(new BigDecimal("40.00"));
        polaris.setIsDefault(false);
        polaris = pricingPlanRepository.save(polaris);

        Event event = eventRepository.save(Event.builder()
                .name("Polaris Open").date(LocalDate.now()).location("Sabadell")
                .basePrice(new BigDecimal("60.00")).extraMatchPrice(new BigDecimal("40.00"))
                .pricingPlan(polaris)
                .build());

        mockMvc.perform(delete("/api/admin/pricing-plans/{id}", polaris.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Event reloaded = eventRepository.findById(event.getId()).orElseThrow();
        assertTrue(reloaded.getPricingPlan() == null);
        assertTrue(new BigDecimal("60.00").compareTo(reloaded.getBasePrice()) == 0);
        assertTrue(new BigDecimal("40.00").compareTo(reloaded.getExtraMatchPrice()) == 0);
    }

    @Test
    void deletePricingPlan_isRejected_withoutToken() throws Exception {
        mockMvc.perform(delete("/api/admin/pricing-plans/{id}", defaultPlanId))
                .andExpect(status().is4xxClientError());
    }
}
