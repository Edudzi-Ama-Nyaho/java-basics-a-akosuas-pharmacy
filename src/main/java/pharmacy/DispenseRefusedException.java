package pharmacy;

/**
 * The pharmacy refused a dispense for a business reason, and nothing was changed.
 * It is checked, so the compiler makes every caller decide what to do with a refusal.
 */
class DispenseRefusedException extends Exception {
    private static final long serialVersionUID = 1L;

    DispenseRefusedException(String message) {
        super(message);
    }
}