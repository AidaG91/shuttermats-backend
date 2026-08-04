package ShutterMats.Backend.exception;

/**
 * Thrown when trying to delete the default pricing plan, or when creating
 * an event without picking any pricing plan at all.
 */
public class DefaultPricingPlanException extends RuntimeException {

    public DefaultPricingPlanException(String message) {
        super(message);
    }

    public static DefaultPricingPlanException cannotDeleteDefault() {
        return new DefaultPricingPlanException(
                "No puedes borrar la tarifa por defecto. Marca otra tarifa como predeterminada primero.");
    }

    public static DefaultPricingPlanException pricingPlanRequired() {
        return new DefaultPricingPlanException("Debes seleccionar una tarifa para el evento.");
    }
}
