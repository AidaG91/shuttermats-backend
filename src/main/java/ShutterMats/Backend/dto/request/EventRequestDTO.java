package ShutterMats.Backend.dto.request;

import java.time.LocalDate;

public record EventRequestDTO (
    String name,
    LocalDate date,
    String location,
    String imageUrl,
    String description
){}
