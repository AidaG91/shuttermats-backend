package ShutterMats.Backend.dto.request.coveragerequest;

import jakarta.validation.constraints.Size;

public record BillingInfoDTO(

        Boolean needsInvoice,

        @Size(max = 150, message = "El nombre/razón social no puede superar los 150 caracteres")
        String invoiceName,

        @Size(max = 50, message = "El NIF/CIF no puede superar los 50 caracteres")
        String invoiceTaxId,

        @Size(max = 250, message = "La dirección de facturación no puede superar los 250 caracteres")
        String invoiceAddress,

        @Size(max = 100, message = "El país de facturación no puede superar los 100 caracteres")
        String invoiceCountry
) {
}
