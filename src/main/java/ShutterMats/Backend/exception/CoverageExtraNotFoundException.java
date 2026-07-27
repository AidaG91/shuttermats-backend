package ShutterMats.Backend.exception;

import java.util.List;

public class CoverageExtraNotFoundException extends RuntimeException {

    public CoverageExtraNotFoundException(List<Long> ids) {
        super("No se han encontrado extras con los siguientes ids: " + ids);
    }
}
