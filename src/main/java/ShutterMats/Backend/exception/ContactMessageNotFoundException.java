package ShutterMats.Backend.exception;

public class ContactMessageNotFoundException extends RuntimeException {

    public ContactMessageNotFoundException(Long id) {
        super("No se ha encontrado ningún mensaje de contacto con id " + id);
    }
}
