package ShutterMats.Backend.dto.response;

public record AdminLoginResponseDTO(
        String token,
        String tokenType,
        long expiresInMs
) {
    public static AdminLoginResponseDTO of(String token, long expiresInMs) {
        return new AdminLoginResponseDTO(token, "Bearer", expiresInMs);
    }
}
