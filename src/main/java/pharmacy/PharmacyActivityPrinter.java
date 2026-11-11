package pharmacy;

import java.util.List;

/**
 * Prints the pharmacy's activity: each dispense, restock and refusal as it happens,
 * then the end-of-day stock report. It is the only class that writes output,
 * so the business classes never print.
 */
class PharmacyActivityPrinter {

    /**
     * Money is printed with doubleValue() so the text matches the original (25.0, 28.5).
     * This is display formatting only: every calculation stays in BigDecimal, and pharmacy
     * totals are far too small for doubleValue() to lose precision.
     */
    void printDispense(DispenseRecord record) {
        Medication medication = record.medication();
        String prescribedBy = medication.getCategory().requiresPrescriber()
                ? " (prescribed by " + record.prescriber() + ")"
                : "";
        System.out.println("Dispensed " + record.quantity() + " x " + medication.getName()
                + " to " + record.customer().getName() + prescribedBy + " for GHS "
                + record.total().doubleValue());
    }

    void printRestock(RestockRecord record) {
        System.out.println("Restocked " + record.quantity() + " x "
                + record.medication().getName());
    }

    void printRefusal(String message) {
        System.out.println(message);
    }

    void printEndOfDay(Pharmacy pharmacy) {
        System.out.println();
        System.out.println("=== " + pharmacy.getName() + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication medication : pharmacy.getMedications()) {
            System.out.println("  " + medication.getName() + " (" + medication.getCategory()
                    + "): " + medication.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + pharmacy.getTransactions().size());
    }

    /** Shows staff what to order so repeat-prescription pick-ups in the planning window are met. */
    void printRestockRecommendations(List<RestockRecommendation> recommendations) {
        System.out.println("Restock needed for repeat prescriptions (next "
                + Pharmacy.PLANNING_DAYS + " days):");
        if (recommendations.isEmpty()) {
            System.out.println("  None - stock covers all upcoming pick-ups");
        }
        for (RestockRecommendation recommendation : recommendations) {
            System.out.println("  " + recommendation.medication().getName()
                    + ": need " + recommendation.needed()
                    + ", have " + recommendation.inStock()
                    + ", order " + recommendation.toOrder());
        }
    }
}