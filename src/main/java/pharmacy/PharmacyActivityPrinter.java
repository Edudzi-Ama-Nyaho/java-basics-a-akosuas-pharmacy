package pharmacy;

import java.util.List;

/**
 * Prints the pharmacy's activity: each dispense, restock and refusal as it happens,
 * then the end-of-day stock report. It is the only class that writes output,
 * so the business classes never print.
 */
class PharmacyActivityPrinter {

    void printDispense(DispenseRecord record) {
        Medication m = record.medication();
        String prescribedBy = m.getCategory().requiresPrescriber()
                ? " (prescribed by " + record.prescriber() + ")"
                : "";
        System.out.println("Dispensed " + record.quantity() + " x " + m.getName() + " to "
                + record.customer().getName() + prescribedBy + " for GHS "
                + record.total().doubleValue());
    }

    void printRestock(RestockRecord record) {
        System.out.println("Restocked " + record.quantity() + " x " + record.medication().getName());
    }

    void printRefusal(String message) {
        System.out.println(message);
    }

    void printEndOfDay(Pharmacy pharmacy) {
        System.out.println();
        System.out.println("=== " + pharmacy.getName() + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : pharmacy.getMedications()) {
            System.out.println("  " + m.getName() + " (" + m.getCategory() + "): " + m.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + pharmacy.getTransactions().size());
    }

    void printRestockRecommendations(List<RestockRecommendation> recommendations) {
        System.out.println("Restock needed for repeat prescriptions (next "
                + Pharmacy.PLANNING_DAYS + " days):");
        if (recommendations.isEmpty()) {
            System.out.println("  None - stock covers all upcoming pick-ups");
        }
        for (RestockRecommendation r : recommendations) {
            System.out.println("  " + r.medication().getName() + ": need " + r.needed()
                    + ", have " + r.inStock() + ", order " + r.toOrder());
        }
    }
}