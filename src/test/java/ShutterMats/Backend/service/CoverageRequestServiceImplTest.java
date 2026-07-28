package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.coveragerequest.AthleteInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.BillingInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CategoryInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ChampionshipInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ConfirmationsDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CoverageInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.LocateInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.PreferencesDTO;
import ShutterMats.Backend.dto.response.CoverageRequestResponseDTO;
import ShutterMats.Backend.entity.CoverageExtra;
import ShutterMats.Backend.entity.CoverageRequest;
import ShutterMats.Backend.entity.Event;
import ShutterMats.Backend.entity.enums.BeltCategory;
import ShutterMats.Backend.entity.enums.CompetitionModality;
import ShutterMats.Backend.entity.enums.Division;
import ShutterMats.Backend.entity.enums.RequestStatus;
import ShutterMats.Backend.mapper.CoverageRequestMapper;
import ShutterMats.Backend.repository.CoverageExtraRepository;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import ShutterMats.Backend.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoverageRequestServiceImplTest {

    @Mock
    private CoverageRequestRepository coverageRequestRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CoverageExtraRepository coverageExtraRepository;

    @Mock
    private CoverageRequestMapper coverageRequestMapper;

    @InjectMocks
    private CoverageRequestServiceImpl coverageRequestService;

    @Test
    void create_savesRequest_andReturnsResponseDTO_whenEventAndExtrasExist() {
        Event event = Event.builder().id(1L).name("Open BJJ").date(LocalDate.now()).location("Madrid").build();

        CoverageExtra warmup = new CoverageExtra();
        warmup.setId(1L);
        warmup.setName("Calentamiento");

        CoverageRequestRequestDTO dto = new CoverageRequestRequestDTO(
                new AthleteInfoDTO("Laia Puig", "laia@example.com", "600111222", null, null, null, null),
                new ChampionshipInfoDTO(1L, null, null),
                new CategoryInfoDTO("Pluma", BeltCategory.BLUE, Division.ADULT, CompetitionModality.BOTH),
                new LocateInfoDTO(null, null, null),
                new CoverageInfoDTO(List.of(1L)),
                new PreferencesDTO(null, null, null),
                new BillingInfoDTO(false, null, null, null, null),
                new ConfirmationsDTO(true, true)
        );

        CoverageRequest entityToSave = new CoverageRequest();
        CoverageRequest savedEntity = new CoverageRequest();
        savedEntity.setId(1L);
        savedEntity.setStatus(RequestStatus.PENDING);

        CoverageRequestResponseDTO expectedResponse = new CoverageRequestResponseDTO(
                1L, RequestStatus.PENDING, "Laia Puig", "laia@example.com",
                null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                List.of("Calentamiento"), null
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(coverageExtraRepository.findAllById(List.of(1L))).thenReturn(List.of(warmup));
        when(coverageRequestMapper.toEntity(dto, event, Set.of(warmup))).thenReturn(entityToSave);
        when(coverageRequestRepository.save(entityToSave)).thenReturn(savedEntity);
        when(coverageRequestMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        CoverageRequestResponseDTO result = coverageRequestService.create(dto);

        assertEquals(expectedResponse, result);
        assertEquals(RequestStatus.PENDING, result.status());
        verify(coverageRequestRepository).save(entityToSave);
    }
}
