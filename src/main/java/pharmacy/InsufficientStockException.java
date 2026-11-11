package pharmacy;

class InsufficientStockException extends DispenseRefusedException {
    private static final long serialVersionUID = 1L;

    InsufficientStockException(String medicationName, int requested, int available) {
        super("Cannot dispense " + requested + " x " + medicationName
                + ": only " + available + " in stock");
    }
}