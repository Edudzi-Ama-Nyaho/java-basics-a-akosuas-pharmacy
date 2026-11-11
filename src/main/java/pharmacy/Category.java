package pharmacy;

/**
 * The kinds of medicine the pharmacy stocks. Each category carries its own dispensing rule,
 * so adding a new one (for example CONTROLLED(true)) needs no change anywhere else.
 */
enum Category {
    OTC(false),
    PRESCRIPTION(true);

    private final boolean requiresPrescriber;

    Category(boolean requiresPrescriber) {
        this.requiresPrescriber = requiresPrescriber;
    }

    /** Whether medicine in this category may only be dispensed with a named prescriber. */
    boolean requiresPrescriber() {
        return requiresPrescriber;
    }

    /**
     * Refuses the dispense if this category needs a prescriber and none was given.
     * A blank name counts as missing, so "" cannot slip through as a prescriber.
     */
    void checkCanDispense(String medicationName, String prescriber)
            throws MissingPrescriberException {
        if (requiresPrescriber && (prescriber == null || prescriber.isBlank())) {
            throw new MissingPrescriberException(medicationName);
        }
    }
}