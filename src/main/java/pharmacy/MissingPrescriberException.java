package pharmacy;

class MissingPrescriberException extends DispenseRefusedException {
    private static final long serialVersionUID = 1L;

    MissingPrescriberException(String medicationName) {
        super("Cannot dispense " + medicationName + " without a prescriber");
    }
}