// Akosua's Pharmacy - stock and dispensing tracker
// Original program. Written in a hurry by the intern who has since left.
// It works, but the team is nervous about changing it. That is your job.
//
// Run it as-is first to see what it currently does before you change anything.

package pharmacy;

import java.math.BigDecimal;

public class PharmacyTracker {

    public static void main(String[] args) {
        Pharmacy p = new Pharmacy("Akosua's Pharmacy");

        Medication paracetamol = new Medication("Paracetamol 500mg", new BigDecimal("12.50"), 100, Category.OTC);
        Medication amoxicillin = new Medication("Amoxicillin 250mg", new BigDecimal("30.00"), 40, Category.PRESCRIPTION);
        p.addMedication(paracetamol);
        p.addMedication(amoxicillin);

        Customer kofi = new Customer("Kofi Mensah", CustomerKind.WALK_IN);
        // registered patients have contact details on file
        Customer ama = new Customer("Ama Owusu", CustomerKind.REGISTERED, "0244 000 000");
        p.addCustomer(kofi);
        p.addCustomer(ama);

        // a normal morning
        dispense(p, kofi, paracetamol, 2, null);
        dispense(p,ama, amoxicillin, 1, "Dr. Boateng"); // prescription-only
        p.restock(paracetamol, 50);

        // ... and the sort of thing that keeps Akosua up at night:
        dispense(p, kofi, amoxicillin, 100, "Dr. Boateng"); // far more than we have in stock

        p.printReport();
    }

    
    // Tries one dispense; a refusal is printed and the day carries on.
    private static void dispense(Pharmacy p, Customer c, Medication m, int qty, String prescriber) {
        try {
            p.dispense(c, m, qty, prescriber);
        } catch (DispenseRefusedException e) {
            System.out.println(e.getMessage());
        }
    }
}