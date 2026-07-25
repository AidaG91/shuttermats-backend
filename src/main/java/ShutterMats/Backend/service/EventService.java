package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    Page<EventResponseDTO> findAll(String status, String location, Pageable pageable);

    List<String> findAllLocations();

    EventResponseDTO findById(Long id);

    EventResponseDTO create(EventRequestDTO dto);

    EventResponseDTO update(Long id, EventRequestDTO dto);

    void delete(Long id);
}
