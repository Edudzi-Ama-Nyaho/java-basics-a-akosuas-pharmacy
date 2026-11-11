package pharmacy;

enum Category {
    OTC(false),
    PRESCRIPTION(true);

    private final boolean requiresPrescriber;

    Category(boolean requiresPrescriber) {
        this.requiresPrescriber = requiresPrescriber;
    }

    boolean requiresPrescriber() {
        return requiresPrescriber;
    }

    void checkCanDispense(String medicationName, String prescriber)
            throws MissingPrescriberException {
        if (requiresPrescriber && (prescriber == null || prescriber.isBlank())) {
            throw new MissingPrescriberException(medicationName);
        }
    }
}