package pharmacy;

/**
 * Refused because the medicine needs a prescriber and none was given.
 * The message is worded exactly as the original program printed it.
 */
class MissingPrescriberException extends DispenseRefusedException {
    private static final long serialVersionUID = 1L;

    MissingPrescriberException(String medicationName) {
        super("Cannot dispense " + medicationName + " without a prescriber");
    }
}