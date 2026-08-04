package ShutterMats.Backend.exception;

public class PricingPlanNotFoundException extends RuntimeException {

    public PricingPlanNotFoundException(Long id) {
        super("No se ha encontrado ninguna tarifa con id " + id);
    }
}
