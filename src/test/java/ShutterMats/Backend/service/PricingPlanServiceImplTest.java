package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.PricingPlanRequestDTO;
import ShutterMats.Backend.dto.response.PricingPlanResponseDTO;
import ShutterMats.Backend.entity.PricingPlan;
import ShutterMats.Backend.exception.DefaultPricingPlanException;
import ShutterMats.Backend.exception.PricingPlanNotFoundException;
import ShutterMats.Backend.repository.EventRepository;
import ShutterMats.Backend.repository.PricingPlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingPlanServiceImplTest {

    @Mock
    private PricingPlanRepository pricingPlanRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private PricingPlanServiceImpl pricingPlanService;

    private static PricingPlan plan(Long id, String name, boolean isDefault) {
        PricingPlan plan = new PricingPlan();
        plan.setId(id);
        plan.setName(name);
        plan.setBasePrice(new BigDecimal("35.00"));
        plan.setExtraMatchPrice(new BigDecimal("25.00"));
        plan.setIsDefault(isDefault);
        return plan;
    }

    @Test
    void findAll_returnsMappedPlans() {
        when(pricingPlanRepository.findAllByOrderByIsDefaultDescNameAsc())
                .thenReturn(List.of(plan(1L, "General", true), plan(2L, "Polaris", false)));

        List<PricingPlanResponseDTO> result = pricingPlanService.findAll();

        assertEquals(2, result.size());
        assertEquals("General", result.get(0).name());
    }

    @Test
    void create_savesPlan_withoutTouchingDefault_whenNotMarkedDefault() {
        PricingPlanRequestDTO dto = new PricingPlanRequestDTO("Polaris", new BigDecimal("60.00"), new BigDecimal("40.00"), false);
        when(pricingPlanRepository.save(any(PricingPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        pricingPlanService.create(dto);

        verify(pricingPlanRepository, never()).findFirstByIsDefaultTrue();
    }

    @Test
    void create_promotesToDefault_andDemotesPreviousDefault_whenMarkedDefault() {
        PricingPlan previousDefault = plan(1L, "General", true);
        when(pricingPlanRepository.findFirstByIsDefaultTrue()).thenReturn(Optional.of(previousDefault));
        when(pricingPlanRepository.save(any(PricingPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        PricingPlanRequestDTO dto = new PricingPlanRequestDTO("Polaris", new BigDecimal("60.00"), new BigDecimal("40.00"), true);
        PricingPlanResponseDTO result = pricingPlanService.create(dto);

        assertTrue(result.isDefault());
        ArgumentCaptor<PricingPlan> captor = ArgumentCaptor.forClass(PricingPlan.class);
        verify(pricingPlanRepository, times(2)).save(captor.capture());
        assertEquals(false, captor.getAllValues().get(0).getIsDefault()); // previous default demoted first
    }

    @Test
    void update_throwsNotFound_whenPlanDoesNotExist() {
        when(pricingPlanRepository.findById(99L)).thenReturn(Optional.empty());

        PricingPlanRequestDTO dto = new PricingPlanRequestDTO("X", new BigDecimal("1.00"), new BigDecimal("1.00"), null);
        assertThrows(PricingPlanNotFoundException.class, () -> pricingPlanService.update(99L, dto));
    }

    @Test
    void delete_throwsDefaultPricingPlanException_whenPlanIsDefault() {
        when(pricingPlanRepository.findById(1L)).thenReturn(Optional.of(plan(1L, "General", true)));

        assertThrows(DefaultPricingPlanException.class, () -> pricingPlanService.delete(1L));
        verify(eventRepository, never()).clearPricingPlanReferences(anyLong());
        verify(pricingPlanRepository, never()).delete(any(PricingPlan.class));
    }

    @Test
    void delete_clearsEventReferences_thenDeletesPlan_whenNotDefault() {
        PricingPlan polaris = plan(2L, "Polaris", false);
        when(pricingPlanRepository.findById(2L)).thenReturn(Optional.of(polaris));

        pricingPlanService.delete(2L);

        verify(eventRepository).clearPricingPlanReferences(2L);
        verify(pricingPlanRepository).delete(polaris);
    }

    @Test
    void getDefaultEntity_returnsExisting_whenPresent() {
        PricingPlan existing = plan(1L, "General", true);
        when(pricingPlanRepository.findFirstByIsDefaultTrue()).thenReturn(Optional.of(existing));

        PricingPlan result = pricingPlanService.getDefaultEntity();

        assertEquals(existing, result);
        verify(pricingPlanRepository, never()).save(any());
    }

    @Test
    void getDefaultEntity_createsFallback_whenNoneExists() {
        when(pricingPlanRepository.findFirstByIsDefaultTrue()).thenReturn(Optional.empty());
        when(pricingPlanRepository.save(any(PricingPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        PricingPlan result = pricingPlanService.getDefaultEntity();

        assertEquals("General", result.getName());
        assertEquals(new BigDecimal("35.00"), result.getBasePrice());
        assertTrue(result.getIsDefault());
    }
}
