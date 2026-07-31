package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.CoverageRequestRequestDTO;
import ShutterMats.Backend.dto.request.UpdateRequestStatusDTO;
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
import ShutterMats.Backend.exception.CoverageRequestNotFoundException;
import ShutterMats.Backend.mapper.CoverageRequestMapper;
import ShutterMats.Backend.repository.CoverageRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoverageRequestServiceImplTest {

    @Mock
    private CoverageRequestRepository coverageRequestRepository;

    @Mock
    private EventService eventService;

    @Mock
    private CoverageExtraService coverageExtraService;

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
                List.of("Calentamiento"), null, null
        );

        when(eventService.getEntityById(1L)).thenReturn(event);
        when(coverageExtraService.resolveByIds(List.of(1L))).thenReturn(Set.of(warmup));
        when(coverageRequestMapper.toEntity(dto, event, Set.of(warmup))).thenReturn(entityToSave);
        when(coverageRequestRepository.save(entityToSave)).thenReturn(savedEntity);
        when(coverageRequestMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        CoverageRequestResponseDTO result = coverageRequestService.create(dto);

        assertEquals(expectedResponse, result);
        assertEquals(RequestStatus.PENDING, result.status());
        verify(coverageRequestRepository).save(entityToSave);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_returnsMappedPage_whenCalled() {
        CoverageRequest entity = new CoverageRequest();
        entity.setId(1L);
        entity.setStatus(RequestStatus.PENDING);

        CoverageRequestResponseDTO dto = new CoverageRequestResponseDTO(
                1L, RequestStatus.PENDING, "Laia Puig", "laia@example.com",
                null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                List.of(), null, null
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<CoverageRequest> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(coverageRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(coverageRequestMapper.toResponseDTO(entity)).thenReturn(dto);

        Page<CoverageRequestResponseDTO> result = coverageRequestService.findAll("PENDING", null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_returnsMappedPage_whenFilteredByEventId() {
        CoverageRequest entity = new CoverageRequest();
        entity.setId(1L);
        entity.setStatus(RequestStatus.PENDING);

        CoverageRequestResponseDTO dto = new CoverageRequestResponseDTO(
                1L, RequestStatus.PENDING, "Laia Puig", "laia@example.com",
                null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                List.of(), null, null
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<CoverageRequest> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(coverageRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(coverageRequestMapper.toResponseDTO(entity)).thenReturn(dto);

        Page<CoverageRequestResponseDTO> result = coverageRequestService.findAll(null, 5L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void findById_returnsMappedDTO_whenRequestExists() {
        CoverageRequest entity = new CoverageRequest();
        entity.setId(1L);
        entity.setStatus(RequestStatus.PENDING);

        CoverageRequestResponseDTO dto = new CoverageRequestResponseDTO(
                1L, RequestStatus.PENDING, "Laia Puig", "laia@example.com",
                null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                List.of(), null, null
        );

        when(coverageRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(coverageRequestMapper.toResponseDTO(entity)).thenReturn(dto);

        CoverageRequestResponseDTO result = coverageRequestService.findById(1L);

        assertEquals(dto, result);
    }

    @Test
    void findById_throwsNotFound_whenRequestDoesNotExist() {
        when(coverageRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CoverageRequestNotFoundException.class,
                () -> coverageRequestService.findById(99L));
    }

    @Test
    void updateStatus_updatesStatusAndResponse_whenRequestExists() {
        CoverageRequest entity = new CoverageRequest();
        entity.setId(1L);
        entity.setStatus(RequestStatus.PENDING);

        UpdateRequestStatusDTO statusDto = new UpdateRequestStatusDTO(RequestStatus.CONFIRMED, "Todo listo, nos vemos allí");

        CoverageRequestResponseDTO expected = new CoverageRequestResponseDTO(
                1L, RequestStatus.CONFIRMED, "Laia Puig", "laia@example.com",
                null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                List.of(), null, "Todo listo, nos vemos allí"
        );

        when(coverageRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(coverageRequestRepository.save(entity)).thenReturn(entity);
        when(coverageRequestMapper.toResponseDTO(entity)).thenReturn(expected);

        CoverageRequestResponseDTO result = coverageRequestService.updateStatus(1L, statusDto);

        assertEquals(expected, result);
        assertEquals(RequestStatus.CONFIRMED, entity.getStatus());
        assertEquals("Todo listo, nos vemos allí", entity.getAdminResponse());
        verify(coverageRequestRepository).save(entity);
    }

    @Test
    void updateStatus_keepsExistingAdminResponse_whenAdminResponseIsNull() {
        CoverageRequest entity = new CoverageRequest();
        entity.setId(1L);
        entity.setStatus(RequestStatus.PENDING);
        entity.setAdminResponse("Respuesta previa");

        UpdateRequestStatusDTO statusDto = new UpdateRequestStatusDTO(RequestStatus.RECEIVED, null);

        when(coverageRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(coverageRequestRepository.save(entity)).thenReturn(entity);
        when(coverageRequestMapper.toResponseDTO(entity)).thenReturn(
                new CoverageRequestResponseDTO(1L, RequestStatus.RECEIVED, "Laia Puig", "laia@example.com",
                        null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                        List.of(), null, "Respuesta previa"));

        coverageRequestService.updateStatus(1L, statusDto);

        assertEquals("Respuesta previa", entity.getAdminResponse());
    }

    @Test
    void updateStatus_clearsAdminResponse_whenAdminResponseIsBlank() {
        CoverageRequest entity = new CoverageRequest();
        entity.setId(1L);
        entity.setStatus(RequestStatus.PENDING);
        entity.setAdminResponse("Respuesta previa");

        UpdateRequestStatusDTO statusDto = new UpdateRequestStatusDTO(RequestStatus.RECEIVED, "");

        when(coverageRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(coverageRequestRepository.save(entity)).thenReturn(entity);
        when(coverageRequestMapper.toResponseDTO(entity)).thenReturn(
                new CoverageRequestResponseDTO(1L, RequestStatus.RECEIVED, "Laia Puig", "laia@example.com",
                        null, Division.ADULT, CompetitionModality.BOTH, BeltCategory.BLUE, "Pluma",
                        List.of(), null, null));

        coverageRequestService.updateStatus(1L, statusDto);

        assertEquals(null, entity.getAdminResponse());
    }

    @Test
    void updateStatus_throwsNotFound_whenRequestDoesNotExist() {
        when(coverageRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CoverageRequestNotFoundException.class,
                () -> coverageRequestService.updateStatus(99L, new UpdateRequestStatusDTO(RequestStatus.CONFIRMED, null)));
    }
}
