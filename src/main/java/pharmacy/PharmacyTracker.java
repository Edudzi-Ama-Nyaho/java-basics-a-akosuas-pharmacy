// Akosua's Pharmacy - stock and dispensing tracker
// Original program. Written in a hurry by the intern who has since left.
// It works, but the team is nervous about changing it. That is your job.
//
// Run it as-is first to see what it currently does before you change anything.

package pharmacy;

import java.math.BigDecimal;

public class PharmacyTracker {

    public static void main(String[] args) {
        Pharmacy pharmacy = new Pharmacy("Akosua's Pharmacy");
        PharmacyActivityPrinter printer = new PharmacyActivityPrinter();

        Medication paracetamol = new Medication("Paracetamol 500mg", new BigDecimal("12.50"), 100, Category.OTC);
        Medication amoxicillin = new Medication("Amoxicillin 250mg", new BigDecimal("30.00"), 40, Category.PRESCRIPTION);
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
        dispense(pharmacy, printer, kofi, amoxicillin, 100, "Dr. Boateng"); // far more than we have in stock

        printer.printEndOfDay(pharmacy);
    }

    // Tries one dispense; a refusal is printed and the day carries on.
    private static void dispense(Pharmacy pharmacy, PharmacyActivityPrinter printer, Customer c,
            Medication m, int qty, String prescriber) {
        try {
            printer.printDispense(pharmacy.dispense(c, m, qty, prescriber));
        } catch (DispenseRefusedException e) {
            printer.printRefusal(e.getMessage());
        }
    }
}