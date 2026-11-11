package pharmacy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Pharmacy {
    private final String name;
    private final List<Medication> medications = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    Pharmacy(String name) {
        this.name = name;
    }

    void addMedication(Medication m) {
        medications.add(Objects.requireNonNull(m, "medication"));
    }

    void addCustomer(Customer c) {
        customers.add(Objects.requireNonNull(c, "customer"));
    }

    String getName() {
        return name;
    }

    List<Medication> getMedications() {
        return List.copyOf(medications);
    }

    List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    // One path for every category; the category decides its own rule.
    DispenseRecord dispense(Customer c, Medication m, int qty, String prescriber)
            throws DispenseRefusedException {
        Objects.requireNonNull(c, "customer");
        Objects.requireNonNull(m, "medication");        
        m.getCategory().checkCanDispense(m.getName(), prescriber);
        BigDecimal total = c.getKind().applyDiscount(m.getPrice().multiply(BigDecimal.valueOf(qty)));
        m.removeStock(qty);
        c.recordInHistory("Got " + qty + " x " + m.getName());
        DispenseRecord record = new DispenseRecord(c, m, qty, total, prescriber);
        transactions.add(record);
        return record;
    }

    RestockRecord restock(Medication m, int qty) {
        Objects.requireNonNull(m, "medication");
        m.addStock(qty);
        RestockRecord record = new RestockRecord(m, qty);
        transactions.add(record);
        return record;
    }
}