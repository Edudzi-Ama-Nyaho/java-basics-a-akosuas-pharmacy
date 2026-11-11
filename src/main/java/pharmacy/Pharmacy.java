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
        if (m.getCategory() == Category.OTC) {
            total = m.getPrice() * qty;
            if (c.getKind() == CustomerKind.REGISTERED) {
                total = total * 0.95; // registered patients get 5% off
            }
            m.removeStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.getName()
                    + " for GHS " + total);
        } else if (m.getCategory() == Category.PRESCRIPTION) {
            if (prescriber == null) {
                System.out.println("Cannot dispense " + m.getName() + " without a prescriber");
                return;
            }
            total = m.getPrice() * qty;
            if (c.getKind() == CustomerKind.REGISTERED) {
                total = total * 0.95; // registered patients get 5% off
            }
            m.removeStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.getName()
                    + " (prescribed by " + prescriber + ") for GHS " + total);
        }
        c.recordInHistory("Got " + qty + " x " + m.getName());
        transactions.add(new Transaction(c.getName(), m.getName(), qty, total, "DISPENSE"));
    }

    public void restock(Medication m, int qty) {
        m.addStock(qty);
        transactions.add(new Transaction("(supplier)", m.getName(), qty, 0, "RESTOCK"));
        System.out.println("Restocked " + qty + " x " + m.getName());
    }

    public List<Transaction> getTransactions() {
        return transactions; // hand the list back so callers can read it
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.getName() + " (" + m.getCategory() + "): " + m.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}