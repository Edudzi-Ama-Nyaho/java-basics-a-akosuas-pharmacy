package pharmacy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class Pharmacy {
    private final String name;
    private final List<Medication> medications = new ArrayList<>();
    private final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<RepeatPrescription> repeatPrescriptions = new ArrayList<>();
    private int nextCustomerId = 1;

    /** Repeat-prescription stock is planned this many days ahead. */
    static final int PLANNING_DAYS = 7;

    Pharmacy(String name) {
        this.name = name;
    }

    void addMedication(Medication m) {
        medications.add(Objects.requireNonNull(m, "medication"));
    }

    /** Registers a customer and returns their customer ID (1, 2, 3, ... in this pharmacy). */
    int addCustomer(Customer c) {
        Objects.requireNonNull(c, "customer");
        if (customers.containsValue(c)) {
            throw new IllegalArgumentException(c.getName() + " is already registered");
        }
        int id = nextCustomerId;
        nextCustomerId++;
        customers.put(id, c);
        return id;
    }

    Customer findCustomer(int id) {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new IllegalArgumentException("No customer with ID " + id);
        }
        return customer;
    }

    void addRepeatPrescription(RepeatPrescription prescription) {
        Objects.requireNonNull(prescription, "prescription");
        if (!customers.containsValue(prescription.patient())) {
            throw new IllegalArgumentException(
                    prescription.patient().getName() + " is not a customer of this pharmacy");
        }
        for (RepeatPrescription existing : repeatPrescriptions) {
            if (existing.patient() == prescription.patient()
                    && existing.medication() == prescription.medication()) {
                throw new IllegalArgumentException(prescription.patient().getName()
                        + " already has a repeat prescription for "
                        + prescription.medication().getName());
            }
        }
        repeatPrescriptions.add(prescription);
    }

    void removeRepeatPrescription(Customer patient, Medication medication) {
        boolean removed = repeatPrescriptions.removeIf(
                p -> p.patient() == patient && p.medication() == medication);
        if (!removed) {
            throw new IllegalArgumentException("No repeat prescription to remove");
        }
    }

    List<RepeatPrescription> getRepeatPrescriptions() {
        return List.copyOf(repeatPrescriptions);
    }

    /** Medicines whose stock won't cover the repeat-prescription pick-ups due in the planning window. */
    List<RestockRecommendation> restockRecommendations() {
        Map<Medication, Integer> needed = new LinkedHashMap<>();
        for (RepeatPrescription p : repeatPrescriptions) {
            int forWindow = p.quantityPerPickup() * p.pickupsWithin(PLANNING_DAYS);
            needed.merge(p.medication(), forWindow, Integer::sum);
        }
        List<RestockRecommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Medication, Integer> entry : needed.entrySet()) {
            Medication medication = entry.getKey();
            int neededQuantity = entry.getValue();
            if (neededQuantity > medication.getStock()) {
                recommendations.add(new RestockRecommendation(medication, neededQuantity,
                        medication.getStock(), neededQuantity - medication.getStock()));
            }
        }
        return List.copyOf(recommendations);
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