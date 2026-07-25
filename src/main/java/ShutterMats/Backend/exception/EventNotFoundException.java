package ShutterMats.Backend.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long id) {
        super("No se ha encontrado ningún evento con id " + id);
    }
}
