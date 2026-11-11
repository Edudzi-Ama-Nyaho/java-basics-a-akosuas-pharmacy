package pharmacy;

import java.util.Objects;

/**
 * A registered patient's standing prescription that they collect regularly.
 * Used to plan stock for upcoming pick-ups; it does not authorise a dispense by itself
 * (each actual dispense still goes through the normal prescriber check).
 */
record RepeatPrescription(Customer patient, Medication medication, int quantityPerPickup,
        int daysBetweenPickups, String prescriber) {

    /** Runs before the record exists, so an invalid repeat prescription can never be created. */
    RepeatPrescription {
        Objects.requireNonNull(patient, "patient");
        Objects.requireNonNull(medication, "medication");
        if (patient.getKind() != CustomerKind.REGISTERED) {
            throw new IllegalArgumentException(
                    "Repeat prescriptions are only kept for registered patients");
        }
        if (quantityPerPickup <= 0) {
            throw new IllegalArgumentException(
                    "Quantity per pick-up must be positive, got " + quantityPerPickup);
        }
        if (daysBetweenPickups <= 0) {
            throw new IllegalArgumentException(
                    "Days between pick-ups must be positive, got " + daysBetweenPickups);
        }
        // The prescriber rule comes from the category, so it is defined in one place only.
        if (medication.getCategory().requiresPrescriber()
                && (prescriber == null || prescriber.isBlank())) {
            throw new IllegalArgumentException(
                    "A repeat prescription for " + medication.getName() + " needs a prescriber");
        }
    }

    /**
     * How many pick-ups fall within a planning window of the given number of days, rounded up
     * so there is always at least one (every 3 days in a 7-day window gives 3; monthly gives 1).
     */
    int pickupsWithin(int days) {
        return (days + daysBetweenPickups - 1) / daysBetweenPickups;
    }
}