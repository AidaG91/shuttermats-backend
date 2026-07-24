package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;

import java.util.List;

public interface EventService {
    List<EventResponseDTO> findAll();

    EventResponseDTO findById(Long id);

    EventResponseDTO create(EventRequestDTO dto);

    EventResponseDTO update(Long id, EventRequestDTO dto);

    void delete(Long id);
}
