package ShutterMats.Backend.dto.request;

import ShutterMats.Backend.dto.request.coveragerequest.AthleteInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.BillingInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CategoryInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ChampionshipInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.ConfirmationsDTO;
import ShutterMats.Backend.dto.request.coveragerequest.CoverageInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.LocateInfoDTO;
import ShutterMats.Backend.dto.request.coveragerequest.PreferencesDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CoverageRequestRequestDTO(

        @NotNull(message = "Los datos del atleta son obligatorios")
        @Valid
        AthleteInfoDTO athlete,

        @NotNull(message = "Los datos del campeonato son obligatorios")
        @Valid
        ChampionshipInfoDTO championship,

        @NotNull(message = "Los datos de categoría son obligatorios")
        @Valid
        CategoryInfoDTO category,

        @Valid
        LocateInfoDTO locate,

        @Valid
        CoverageInfoDTO coverage,

        @Valid
        PreferencesDTO preferences,

        @Valid
        BillingInfoDTO billing,

        @NotNull(message = "Debes confirmar las condiciones")
        @Valid
        ConfirmationsDTO confirmations
) {
}
