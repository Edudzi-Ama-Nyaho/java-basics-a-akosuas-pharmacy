package pharmacy;

import java.math.BigDecimal;

/**
 * An immutable record of one dispense. It holds the actual customer and medicine (not just
 * their names) and the prescriber, so every dispense can be traced and audited later.
 */
record DispenseRecord(Customer customer, Medication medication, int quantity,
        BigDecimal total, String prescriber) implements Transaction {
}