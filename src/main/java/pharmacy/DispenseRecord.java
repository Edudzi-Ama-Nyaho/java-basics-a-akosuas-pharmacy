package pharmacy;

import java.math.BigDecimal;

record DispenseRecord(Customer customer, Medication medication, int quantity,
        BigDecimal total, String prescriber) implements Transaction {
}