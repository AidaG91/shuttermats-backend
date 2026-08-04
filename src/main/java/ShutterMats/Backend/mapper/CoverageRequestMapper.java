package ShutterMats.Backend.mapper;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.coveragerequest.AthleteInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.BillingInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CategoryInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ChampionshipInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ConfirmationsDTO;
import ShutterMats.Backend.dto.request.coveragerequest.LocateInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.PreferencesDTO;
import ShutterMats.Backend.dto.response.CoverageExtraResponseDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CoverageRequestMapper {

    private final EventMapper eventMapper;

    public CoverageRequestMapper(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public CoverageRequest toEntity(CoverageRequestRequestDTO dto, Event event, Set<CoverageExtra> extras) {
        CoverageRequest request = new CoverageRequest();

        AthleteInfoDTO athlete = dto.athlete();
        request.setAthleteName(athlete.name());
        request.setAthleteEmail(athlete.email());
        request.setAthletePhone(athlete.phone());
        request.setAthleteInstagram(athlete.instagram());
        request.setAthleteGym(athlete.gym());
        request.setAthleteCity(athlete.city());
        request.setAthleteCountry(athlete.country());

        request.setEvent(event);

        ChampionshipInfoDTO championship = dto.championship();
        request.setOrganizer(championship.organizer());
        request.setSmoothcompLink(championship.smoothcompLink());

        CategoryInfoDTO category = dto.category();
        request.setWeight(category.weight());
        request.setBelt(category.belt());
        request.setDivision(category.division());
        request.setModality(category.modality());

        LocateInfoDTO locate = dto.locate();
        if (locate != null) {
            request.setSmoothcompDisplayName(locate.smoothcompDisplayName());
            request.setSmoothcompProfileLink(locate.smoothcompProfileLink());
            request.setEstimatedFirstFightTime(locate.estimatedFirstFightTime());
        }

        request.setExtras(extras);

        PreferencesDTO preferences = dto.preferences();
        if (preferences != null) {
            request.setPhotoPreferences(preferences.photoPreferences());
            request.setSpecialMoments(preferences.specialMoments());
            request.setAdditionalNotes(preferences.additionalNotes());
        }

        BillingInfoDTO billing = dto.billing();
        if (billing != null) {
            request.setNeedsInvoice(billing.needsInvoice());
            request.setInvoiceName(billing.invoiceName());
            request.setInvoiceTaxId(billing.invoiceTaxId());
            request.setInvoiceAddress(billing.invoiceAddress());
            request.setInvoiceCountry(billing.invoiceCountry());
        }

        ConfirmationsDTO confirmations = dto.confirmations();
        request.setTermsAccepted(confirmations.termsAccepted());
        request.setPortfolioConsent(confirmations.portfolioConsent());

        return request;
    }

    public CoverageRequestResponseDTO toResponseDTO(CoverageRequest request) {
        List<CoverageExtraResponseDTO> extras = request.getExtras().stream()
                .map(extra -> new CoverageExtraResponseDTO(extra.getId(), extra.getName(), extra.getPrice()))
                .toList();

        return new CoverageRequestResponseDTO(
                request.getId(),
                request.getStatus(),
                request.getAthleteName(),
                request.getAthleteEmail(),
                eventMapper.toResponseDTO(request.getEvent()),
                request.getDivision(),
                request.getModality(),
                request.getBelt(),
                request.getWeight(),
                extras,
                request.getCreatedAt(),
                request.getAdminResponse()
        );
    }
}
