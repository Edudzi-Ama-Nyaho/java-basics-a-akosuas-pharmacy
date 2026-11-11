package pharmacy;

import java.math.BigDecimal;

/**
 * Shows what the repeat-prescription stock check looks like to pharmacy staff.
 * Lives in the test folder so the main program's output is unchanged.
 * How to run it is in the README ("Improvement demo").
 */
public final class RepeatPrescriptionDemo {

    private RepeatPrescriptionDemo() {
    }

    public static void main(String[] args) {
        Pharmacy pharmacy = new Pharmacy("Akosua's Pharmacy");
        Medication amoxicillin = new Medication(
                "Amoxicillin 250mg", new BigDecimal("30.00"), 39, Category.PRESCRIPTION);
        pharmacy.addMedication(amoxicillin);

        int amaId = pharmacy.addCustomer(
                new Customer("Ama Owusu", CustomerKind.REGISTERED, "0244 000 000"));
        int yawId = pharmacy.addCustomer(new Customer("Yaw Boadu", CustomerKind.REGISTERED));

        // Ama collects 10 every 3 days; Yaw collects 20 every week.
        Customer ama = pharmacy.findCustomer(amaId);
        Customer yaw = pharmacy.findCustomer(yawId);
        pharmacy.addRepeatPrescription(
                new RepeatPrescription(ama, amoxicillin, 10, 3, "Dr. Boateng"));
        pharmacy.addRepeatPrescription(
                new RepeatPrescription(yaw, amoxicillin, 20, 7, "Dr. Mensah"));

        PharmacyActivityPrinter printer = new PharmacyActivityPrinter();
        printer.printRestockRecommendations(pharmacy.restockRecommendations());
    }
}