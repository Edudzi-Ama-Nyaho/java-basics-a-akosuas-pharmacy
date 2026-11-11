package pharmacy;

import java.util.Objects;

/**
 * A registered patient's standing prescription that they collect regularly.
 * Used to plan stock for upcoming pick-ups; it does not authorise a dispense by itself.
 */
record RepeatPrescription(Customer patient, Medication medication, int quantityPerPickup,
        int daysBetweenPickups, String prescriber) {

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
        if (medication.getCategory().requiresPrescriber()
                && (prescriber == null || prescriber.isBlank())) {
            throw new IllegalArgumentException(
                    "A repeat prescription for " + medication.getName() + " needs a prescriber");
        }
    }

    /** How many pick-ups fall within a planning window of the given number of days (at least one). */
    int pickupsWithin(int days) {
        return (days + daysBetweenPickups - 1) / daysBetweenPickups;
    }
}