package pharmacy;

import java.util.ArrayList;
import java.util.List;

class Pharmacy {
    public String name;
    public List<Medication> meds = new ArrayList<>();
    public List<Customer> customers = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    public Pharmacy(String name) {
        this.name = name;
    }

    public void addMedication(Medication m) {
        meds.add(m);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    // Dispense medicine to a customer. The rules depend on the kind of medicine.
    public void dispense(Customer c, Medication m, int qty, String prescriber) {
        double total = 0;
        if (m.category == Category.OTC) {
            total = m.price * qty;
            if (c.kind == CustomerKind.REGISTERED) {
                total = total * 0.95; // registered patients get 5% off
            }
            m.stock = m.stock - qty;
            System.out.println("Dispensed " + qty + " x " + m.name + " to " + c.name
                    + " for GHS " + total);
        } else if (m.category == Category.PRESCRIPTION) {
            if (prescriber == null) {
                System.out.println("Cannot dispense " + m.name + " without a prescriber");
                return;
            }
            total = m.price * qty;
            if (c.kind == CustomerKind.REGISTERED) {
                total = total * 0.95; // registered patients get 5% off
            }
            m.stock = m.stock - qty;
            System.out.println("Dispensed " + qty + " x " + m.name + " to " + c.name
                    + " (prescribed by " + prescriber + ") for GHS " + total);
        }
        c.history.add("Got " + qty + " x " + m.name);
        transactions.add(new Transaction(c.name, m.name, qty, total, "DISPENSE"));
    }

    public void restock(Medication m, int qty) {
        m.stock = m.stock + qty;
        transactions.add(new Transaction("(supplier)", m.name, qty, 0, "RESTOCK"));
        System.out.println("Restocked " + qty + " x " + m.name);
    }

    public List<Transaction> getTransactions() {
        return transactions; // hand the list back so callers can read it
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.name + " (" + m.category + "): " + m.stock + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}