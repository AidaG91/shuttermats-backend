package ShutterMats.Backend.service;

import ShutterMats.Backend.dto.request.EventRequestDTO;
import ShutterMats.Backend.dto.response.EventResponseDTO;
import ShutterMats.Backend.entity.Event;
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

    /**
     * For use by other services that need the managed entity (e.g. to build
     * a relation) rather than the public response DTO.
     */
    Event getEntityById(Long id);
}
