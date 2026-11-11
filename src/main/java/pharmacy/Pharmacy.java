package pharmacy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class Pharmacy {
    private final String name;
    private final List<Medication> medications = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public Pharmacy(String name) {
        this.name = name;
    }

    public void addMedication(Medication m) {
        medications.add(m);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    // Dispense medicine to a customer. The rules depend on the kind of medicine.
    public void dispense(Customer c, Medication m, int qty, String prescriber) {
        BigDecimal total = BigDecimal.ZERO;
        if (m.getCategory() == Category.OTC) {
            total = c.getKind().applyDiscount(m.getPrice().multiply(BigDecimal.valueOf(qty)));
            m.removeStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.getName()
                    + " for GHS " + total.doubleValue());
        } else if (m.getCategory() == Category.PRESCRIPTION) {
            if (prescriber == null) {
                System.out.println("Cannot dispense " + m.getName() + " without a prescriber");
                return;
            }
            total = c.getKind().applyDiscount(m.getPrice().multiply(BigDecimal.valueOf(qty)));
            m.removeStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.getName()
                    + " (prescribed by " + prescriber + ") for GHS " + total.doubleValue());
        }
        c.recordInHistory("Got " + qty + " x " + m.getName());
        transactions.add(new DispenseRecord(c, m, qty, total, prescriber));
    }

    public void restock(Medication m, int qty) {
        m.addStock(qty);
        transactions.add(new RestockRecord(m, qty));
        System.out.println("Restocked " + qty + " x " + m.getName());
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : medications) {
            System.out.println("  " + m.getName() + " (" + m.getCategory() + "): " + m.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}