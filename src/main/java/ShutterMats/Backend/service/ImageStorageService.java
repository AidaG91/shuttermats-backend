package ShutterMats.Backend.service;

import ShutterMats.Backend.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Guarda las imagenes de los eventos en disco local, bajo {app.upload.dir}/events.
 * Se sirven despues como recurso estatico en /uploads/** (ver WebConfig).
 */
@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );

    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    private final Path eventsUploadDir;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.eventsUploadDir = Path.of(uploadDir, "events").toAbsolutePath().normalize();
        try {
            Files.createDirectories(eventsUploadDir);
        } catch (IOException e) {
            throw new UncheckedIOException("No se ha podido preparar el directorio de subida de imagenes", e);
        }
    }

    /**
     * Guarda la imagen y devuelve la URL publica relativa (para persistir en Event.imageUrl).
     */
    public String store(MultipartFile file) {
        validate(file);

        String extension = EXTENSION_BY_CONTENT_TYPE.get(file.getContentType());
        String filename = UUID.randomUUID() + extension;
        Path target = eventsUploadDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se ha podido guardar la imagen", e);
        }

        return "/uploads/events/" + filename;
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidImageException("El archivo de imagen esta vacio");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidImageException("La imagen no puede superar los 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidImageException("Formato de imagen no soportado. Usa JPG, PNG o WEBP");
        }
        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new InvalidImageException("El archivo no tiene nombre valido");
        }
    }
}
