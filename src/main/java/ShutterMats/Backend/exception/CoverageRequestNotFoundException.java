package ShutterMats.Backend.exception;

public class CoverageRequestNotFoundException extends RuntimeException {

    public CoverageRequestNotFoundException(Long id) {
        super("No se ha encontrado ninguna solicitud de cobertura con id " + id);
    }
}
