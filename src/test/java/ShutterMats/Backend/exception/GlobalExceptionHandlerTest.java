package ShutterMats.Backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404WithMessage() {
        ResponseEntity<ApiError> response = handler.handleNotFound(new EventNotFoundException(42L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().message().contains("42"));
    }

    @Test
    void handleUnexpected_returns500WithGenericMessage() {
        ResponseEntity<ApiError> response = handler.handleUnexpected(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ha ocurrido un error inesperado", response.getBody().message());
    }

    // handleMalformedBody is covered by
    // AdminRequestsIntegrationTest#updateStatus_returns400_whenStatusIsInvalid:
    // HttpMessageNotReadableException doesn't have a simple public
    // constructor that's stable across versions to instantiate by hand here.
}
