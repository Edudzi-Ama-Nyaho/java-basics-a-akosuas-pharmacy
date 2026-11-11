package pharmacy;

import java.math.BigDecimal;

/**
 * Akosua's Pharmacy stock and dispensing tracker: runs one day's activity
 * (the same demo as the original program) and prints the end-of-day report.
 */
public class PharmacyTracker {

    public static void main(String[] args) {
        Pharmacy pharmacy = new Pharmacy("Akosua's Pharmacy");
        PharmacyActivityPrinter printer = new PharmacyActivityPrinter();

        Medication paracetamol =
                new Medication("Paracetamol 500mg", new BigDecimal("12.50"), 100, Category.OTC);
        Medication amoxicillin = new Medication(
                "Amoxicillin 250mg", new BigDecimal("30.00"), 40, Category.PRESCRIPTION);
        pharmacy.addMedication(paracetamol);
        pharmacy.addMedication(amoxicillin);

        Customer kofi = new Customer("Kofi Mensah", CustomerKind.WALK_IN);
        // registered patients have contact details on file
        Customer ama = new Customer("Ama Owusu", CustomerKind.REGISTERED, "0244 000 000");
        pharmacy.addCustomer(kofi);
        pharmacy.addCustomer(ama);

        // a normal morning
        dispense(pharmacy, printer, kofi, paracetamol, 2, null);
        dispense(pharmacy, printer, ama, amoxicillin, 1, "Dr. Boateng"); // prescription-only
        printer.printRestock(pharmacy.restock(paracetamol, 50));

        // ... and the sort of thing that keeps Akosua up at night:
        // far more than we have in stock, so this is now refused instead of going negative
        dispense(pharmacy, printer, kofi, amoxicillin, 100, "Dr. Boateng");

        printer.printEndOfDay(pharmacy);
    }

    /** Tries one dispense; a refusal is printed and the day carries on. */
    private static void dispense(Pharmacy pharmacy, PharmacyActivityPrinter printer,
            Customer customer, Medication medication, int quantity, String prescriber) {
        try {
            printer.printDispense(pharmacy.dispense(customer, medication, quantity, prescriber));
        } catch (DispenseRefusedException e) {
            printer.printRefusal(e.getMessage());
        }
    }
}