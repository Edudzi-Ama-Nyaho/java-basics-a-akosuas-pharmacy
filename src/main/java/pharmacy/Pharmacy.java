package pharmacy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Coordinates the pharmacy's work: registering medicines and customers, dispensing,
 * restocking, and planning stock for repeat prescriptions. It never prints; it returns records.
 */
class Pharmacy {
    private final String name;
    private final List<Medication> medications = new ArrayList<>();
    // The customer register: each customer ID points to one customer.
    private final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<RepeatPrescription> repeatPrescriptions = new ArrayList<>();
    private int nextCustomerId = 1;

    /** Repeat-prescription stock is planned this many days ahead. */
    static final int PLANNING_DAYS = 7;

    Pharmacy(String name) {
        this.name = name;
    }

    void addMedication(Medication medication) {
        medications.add(Objects.requireNonNull(medication, "medication"));
    }

    /** Registers a customer and returns their customer ID (1, 2, 3, ... in this pharmacy). */
    int addCustomer(Customer customer) {
        Objects.requireNonNull(customer, "customer");
        if (customers.containsValue(customer)) {
            throw new IllegalArgumentException(customer.getName() + " is already registered");
        }
        int id = nextCustomerId;
        nextCustomerId++;
        customers.put(id, customer);
        return id;
    }

    /** Looks a customer up by their ID; refuses an ID that isn't registered. */
    Customer findCustomer(int id) {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new IllegalArgumentException("No customer with ID " + id);
        }
        return customer;
    }

    /**
     * Adds a repeat prescription for one of this pharmacy's customers.
     * Refuses a patient who isn't registered here, and a second one for the same patient and
     * medicine, because a duplicate would double-count what is needed and cause over-ordering.
     */
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

    /** Removes a patient's repeat prescription for a medicine; refuses if there isn't one. */
    void removeRepeatPrescription(Customer patient, Medication medication) {
        boolean removed = repeatPrescriptions.removeIf(
                prescription -> prescription.patient() == patient
                        && prescription.medication() == medication);
        if (!removed) {
            throw new IllegalArgumentException("No repeat prescription to remove");
        }
    }

    List<RepeatPrescription> getRepeatPrescriptions() {
        return List.copyOf(repeatPrescriptions);
    }

    /**
     * Medicines whose stock won't cover the repeat-prescription pick-ups due in the
     * planning window, with how many of each to order.
     */
    List<RestockRecommendation> restockRecommendations() {
        // Add up, per medicine, everything needed for the pick-ups in the planning window.
        Map<Medication, Integer> needed = new LinkedHashMap<>();
        for (RepeatPrescription prescription : repeatPrescriptions) {
            int forWindow = prescription.quantityPerPickup()
                    * prescription.pickupsWithin(PLANNING_DAYS);
            needed.merge(prescription.medication(), forWindow, Integer::sum);
        }
        // Recommend ordering exactly the shortfall, so nothing extra sits on the shelf.
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

    /** A read-only copy of the medicines, in the order they were added. */
    List<Medication> getMedications() {
        return List.copyOf(medications);
    }

    /** A read-only copy of today's transactions; the real list can't be changed from outside. */
    List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    /**
     * Dispenses medicine: one path for every category, and the category decides its own rule.
     * The order matters: the prescriber and stock checks run before anything is recorded,
     * so a refused dispense changes nothing.
     */
    DispenseRecord dispense(Customer customer, Medication medication, int quantity,
            String prescriber) throws DispenseRefusedException {
        Objects.requireNonNull(customer, "customer");
        Objects.requireNonNull(medication, "medication");
        medication.getCategory().checkCanDispense(medication.getName(), prescriber);
        BigDecimal total = customer.getKind().applyDiscount(
                medication.getPrice().multiply(BigDecimal.valueOf(quantity)));
        medication.removeStock(quantity);
        customer.recordInHistory("Got " + quantity + " x " + medication.getName());
        DispenseRecord record =
                new DispenseRecord(customer, medication, quantity, total, prescriber);
        transactions.add(record);
        return record;
    }

    /** Adds stock and records the restock. */
    RestockRecord restock(Medication medication, int quantity) {
        Objects.requireNonNull(medication, "medication");
        medication.addStock(quantity);
        RestockRecord record = new RestockRecord(medication, quantity);
        transactions.add(record);
        return record;
    }
}