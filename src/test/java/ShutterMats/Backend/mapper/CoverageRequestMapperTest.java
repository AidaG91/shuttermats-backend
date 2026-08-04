package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.coveragerequest.AthleteInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.BillingInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CategoryInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ChampionshipInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ConfirmationsDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CoverageInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.LocateInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.PreferencesDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.enums.BeltCategory;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import ShutterMats.Backend.service.PricingPlanService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CoverageRequestMapperTest {

    // toEntity (the only method this class tests) never touches pricing,
    // so the mock doesn't need any stubbing.
    private final CoverageRequestMapper mapper =
            new CoverageRequestMapper(new EventMapper(mock(PricingPlanService.class)));

    @Test
    void toEntity_mapsAllSectionsCorrectly_whenAllSectionsPresent() {
        Event event = Event.builder()
                .id(1L)
                .name("Open BJJ")
                .date(LocalDate.now())
                .location("Madrid")
                .build();

        CoverageExtra warmup = new CoverageExtra();
        warmup.setId(1L);
        warmup.setName("Calentamiento");
        warmup.setPrice(new BigDecimal("15.00"));
        warmup.setActive(true);

        CoverageRequestRequestDTO dto = new CoverageRequestRequestDTO(
                new AthleteInfoDTO("Laia Puig", "laia@example.com", "600111222", "@laiapuig", "Gràcia BJJ", "Barcelona", "España"),
                new ChampionshipInfoDTO(1L, "IBJJF", "https://smoothcomp.com/es/event/..."),
                new CategoryInfoDTO("Pluma", BeltCategory.BLUE, Division.ADULT, CompetitionModality.BOTH),
                new LocateInfoDTO("Laia Puig", null, "10:30"),
                new CoverageInfoDTO(java.util.List.of(1L)),
                new PreferencesDTO("Acción y podio", "Entrada al tatami", null),
                new BillingInfoDTO(false, null, null, null, null),
                new ConfirmationsDTO(true, true)
        );

        CoverageRequest entity = mapper.toEntity(dto, event, Set.of(warmup));

        assertEquals("Laia Puig", entity.getAthleteName());
        assertEquals("laia@example.com", entity.getAthleteEmail());
        assertEquals(event, entity.getEvent());
        assertEquals(Division.ADULT, entity.getDivision());
        assertEquals(CompetitionModality.BOTH, entity.getModality());
        assertEquals(BeltCategory.BLUE, entity.getBelt());
        assertTrue(entity.getExtras().contains(warmup));
        assertTrue(entity.getTermsAccepted());
    }
}
