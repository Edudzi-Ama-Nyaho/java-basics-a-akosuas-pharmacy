package pharmacy;

/**
 * Refused because the dispense would take stock below zero.
 * The message tells staff how much was asked for and how much is actually in stock.
 */
class InsufficientStockException extends DispenseRefusedException {
    private static final long serialVersionUID = 1L;

    InsufficientStockException(String medicationName, int requested, int available) {
        super("Cannot dispense " + requested + " x " + medicationName
                + ": only " + available + " in stock");
    }
}