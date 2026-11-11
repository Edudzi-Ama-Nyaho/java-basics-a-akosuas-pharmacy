package pharmacy;

class DispenseRefusedException extends Exception {
    private static final long serialVersionUID = 1L;

    DispenseRefusedException(String message) {
        super(message);
    }
}