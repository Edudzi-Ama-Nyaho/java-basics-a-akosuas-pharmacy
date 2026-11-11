package pharmacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pins down the original pharmacy behaviour and the rules the rebuild enforces. */
class PharmacyTest {

    private Pharmacy pharmacy;
    private Medication paracetamol;
    private Medication amoxicillin;
    private Customer kofi;
    private Customer ama;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy("Akosua's Pharmacy");
        paracetamol = new Medication("Paracetamol 500mg", new BigDecimal("12.50"), 100, Category.OTC);
        amoxicillin = new Medication("Amoxicillin 250mg", new BigDecimal("30.00"), 40, Category.PRESCRIPTION);
        pharmacy.addMedication(paracetamol);
        pharmacy.addMedication(amoxicillin);
        kofi = new Customer("Kofi Mensah", CustomerKind.WALK_IN);
        ama = new Customer("Ama Owusu", CustomerKind.REGISTERED, "0244 000 000");
        pharmacy.addCustomer(kofi);
        pharmacy.addCustomer(ama);
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    // ---- original behaviour ----

    @Test
    @DisplayName("A walk-in pays full price and stock goes down")
    void walkInPaysFullPrice() throws DispenseRefusedException {
        DispenseRecord record = pharmacy.dispense(kofi, paracetamol, 2, null);
        assertMoney("25.00", record.total());
        assertEquals(98, paracetamol.getStock());
    }

    @Test
    @DisplayName("A registered patient gets 5% off")
    void registeredPatientGetsDiscount() throws DispenseRefusedException {
        DispenseRecord record = pharmacy.dispense(ama, amoxicillin, 1, "Dr. Boateng");
        assertMoney("28.50", record.total());
        assertEquals("Dr. Boateng", record.prescriber());
    }

    @Test
    @DisplayName("A restock adds to stock and is recorded")
    void restockAddsStock() {
        pharmacy.restock(paracetamol, 50);
        assertEquals(150, paracetamol.getStock());
        assertEquals(1, pharmacy.getTransactions().size());
    }

    // ---- the never-below-zero rule ----

    @Test
    @DisplayName("A dispense larger than the stock is refused and nothing changes")
    void cannotDispenseMoreThanStock() {
        InsufficientStockException e = assertThrows(InsufficientStockException.class,
                () -> pharmacy.dispense(kofi, amoxicillin, 41, "Dr. Boateng"));
        assertEquals("Cannot dispense 41 x Amoxicillin 250mg: only 40 in stock", e.getMessage());
        assertEquals(40, amoxicillin.getStock());
        assertEquals(0, pharmacy.getTransactions().size());
        assertEquals(0, kofi.getHistory().size());
    }

    @Test
    @DisplayName("Dispensing exactly the remaining stock leaves zero")
    void canDispenseAllRemainingStock() throws DispenseRefusedException {
        pharmacy.dispense(kofi, amoxicillin, 40, "Dr. Boateng");
        assertEquals(0, amoxicillin.getStock());
    }

    @Test
    @DisplayName("Zero or negative quantities are rejected and stock is unchanged")
    void invalidQuantitiesRejected() {
        assertThrows(IllegalArgumentException.class, () -> pharmacy.dispense(kofi, paracetamol, 0, null));
        assertThrows(IllegalArgumentException.class, () -> pharmacy.dispense(kofi, paracetamol, -5, null));
        assertThrows(IllegalArgumentException.class, () -> pharmacy.restock(paracetamol, -50));
        assertEquals(100, paracetamol.getStock());
        assertEquals(0, pharmacy.getTransactions().size());
    }

    // ---- prescriptions ----

    @Test
    @DisplayName("Prescription medicine without a prescriber is refused (missing or blank)")
    void prescriptionNeedsPrescriber() {
        MissingPrescriberException e = assertThrows(MissingPrescriberException.class,
                () -> pharmacy.dispense(ama, amoxicillin, 1, null));
        assertEquals("Cannot dispense Amoxicillin 250mg without a prescriber", e.getMessage());
        assertThrows(MissingPrescriberException.class, () -> pharmacy.dispense(ama, amoxicillin, 1, "  "));
        assertEquals(40, amoxicillin.getStock());
        assertEquals(0, pharmacy.getTransactions().size());
    }

    @Test
    @DisplayName("Over-the-counter medicine needs no prescriber")
    void overTheCounterNeedsNoPrescriber() throws DispenseRefusedException {
        pharmacy.dispense(kofi, paracetamol, 1, null);
        assertEquals(99, paracetamol.getStock());
    }

    // ---- encapsulation: nothing outside can change protected state ----

    @Test
    @DisplayName("Patient history is recorded and cannot be edited from outside")
    void historyIsReadOnly() throws DispenseRefusedException {
        pharmacy.dispense(ama, amoxicillin, 1, "Dr. Boateng");
        List<String> history = ama.getHistory();
        assertEquals(List.of("Got 1 x Amoxicillin 250mg"), history);
        assertThrows(UnsupportedOperationException.class, () -> history.add("fake entry"));
    }

    @Test
    @DisplayName("The transaction list handed out cannot be edited")
    void transactionsAreReadOnly() {
        pharmacy.restock(paracetamol, 10);
        List<Transaction> transactions = pharmacy.getTransactions();
        assertThrows(UnsupportedOperationException.class, transactions::clear);
        assertEquals(1, pharmacy.getTransactions().size());
    }

    @Test
    @DisplayName("A registered patient's phone number is kept")
    void phoneNumberIsKept() {
        assertEquals("0244 000 000", ama.getPhone());
    }

    // ---- objects cannot be created invalid ----

    @Test
    @DisplayName("Invalid medications and customers cannot be created")
    void invalidObjectsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medication("X", new BigDecimal("-1"), 1, Category.OTC));
        assertThrows(IllegalArgumentException.class,
                () -> new Medication(" ", BigDecimal.ONE, 1, Category.OTC));
        assertThrows(IllegalArgumentException.class,
                () -> new Medication("X", BigDecimal.ONE, -1, Category.OTC));
        assertThrows(NullPointerException.class, () -> new Customer("Yaw", null));
        assertThrows(IllegalArgumentException.class, () -> new Customer("", CustomerKind.WALK_IN));
    }

    // ---- customer IDs ----

    @Test
    @DisplayName("Customers get IDs and can be found by them")
    void customersFoundById() {
        Customer yaw = new Customer("Yaw Boadu", CustomerKind.REGISTERED);
        int id = pharmacy.addCustomer(yaw);
        assertEquals(3, id);
        assertTrue(pharmacy.findCustomer(id) == yaw);
        assertThrows(IllegalArgumentException.class, () -> pharmacy.findCustomer(99));
        assertThrows(IllegalArgumentException.class, () -> pharmacy.addCustomer(yaw));
    }
}