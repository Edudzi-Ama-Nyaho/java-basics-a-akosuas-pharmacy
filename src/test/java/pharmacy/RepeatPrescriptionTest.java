package pharmacy;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** The improvement: planning stock for registered patients' repeat prescriptions. */
class RepeatPrescriptionTest {

    private Pharmacy pharmacy;
    private Medication amoxicillin;
    private Medication paracetamol;
    private Customer ama;
    private Customer yaw;
    private Customer kofi;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy("Akosua's Pharmacy");
        amoxicillin = new Medication("Amoxicillin 250mg", new BigDecimal("30.00"), 39, Category.PRESCRIPTION);
        paracetamol = new Medication("Paracetamol 500mg", new BigDecimal("12.50"), 148, Category.OTC);
        pharmacy.addMedication(amoxicillin);
        pharmacy.addMedication(paracetamol);
        ama = new Customer("Ama Owusu", CustomerKind.REGISTERED, "0244 000 000");
        yaw = new Customer("Yaw Boadu", CustomerKind.REGISTERED);
        kofi = new Customer("Kofi Mensah", CustomerKind.WALK_IN);
        pharmacy.addCustomer(ama);
        pharmacy.addCustomer(yaw);
        pharmacy.addCustomer(kofi);
    }

    @Test
    @DisplayName("Recommends ordering exactly the shortfall for the next 7 days")
    void recommendsShortfall() {
        // Ama: 10 every 3 days = 3 pick-ups in 7 days = 30. Yaw: 20 weekly = 20. Total 50, have 39.
        pharmacy.addRepeatPrescription(new RepeatPrescription(ama, amoxicillin, 10, 3, "Dr. Boateng"));
        pharmacy.addRepeatPrescription(new RepeatPrescription(yaw, amoxicillin, 20, 7, "Dr. Mensah"));

        List<RestockRecommendation> recommendations = pharmacy.restockRecommendations();

        assertEquals(1, recommendations.size());
        RestockRecommendation r = recommendations.get(0);
        assertTrue(r.medication() == amoxicillin);
        assertEquals(50, r.needed());
        assertEquals(39, r.inStock());
        assertEquals(11, r.toOrder());
    }

    @Test
    @DisplayName("No recommendation when stock already covers the pick-ups")
    void noRecommendationWhenStockCovers() {
        pharmacy.addRepeatPrescription(new RepeatPrescription(ama, paracetamol, 20, 30, null));
        assertEquals(0, pharmacy.restockRecommendations().size());
    }

    @Test
    @DisplayName("Removing a repeat prescription stops it counting")
    void removeStopsCounting() {
        pharmacy.addRepeatPrescription(new RepeatPrescription(ama, amoxicillin, 10, 3, "Dr. Boateng"));
        pharmacy.addRepeatPrescription(new RepeatPrescription(yaw, amoxicillin, 20, 7, "Dr. Mensah"));
        pharmacy.removeRepeatPrescription(yaw, amoxicillin);
        assertEquals(1, pharmacy.getRepeatPrescriptions().size());
        assertEquals(0, pharmacy.restockRecommendations().size());
        assertThrows(IllegalArgumentException.class,
                () -> pharmacy.removeRepeatPrescription(yaw, amoxicillin));
    }

    @Test
    @DisplayName("The same patient cannot have the same repeat prescription twice")
    void duplicateRejected() {
        pharmacy.addRepeatPrescription(new RepeatPrescription(ama, amoxicillin, 10, 3, "Dr. Boateng"));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> pharmacy.addRepeatPrescription(
                        new RepeatPrescription(ama, amoxicillin, 5, 7, "Dr. Boateng")));
        assertEquals("Ama Owusu already has a repeat prescription for Amoxicillin 250mg", e.getMessage());
    }

    @Test
    @DisplayName("Only registered customers of this pharmacy can have repeat prescriptions")
    void onlyRegisteredCustomersOfThisPharmacy() {
        assertThrows(IllegalArgumentException.class,
                () -> new RepeatPrescription(kofi, paracetamol, 10, 7, null));
        Customer stranger = new Customer("Esi Asante", CustomerKind.REGISTERED);
        assertThrows(IllegalArgumentException.class,
                () -> pharmacy.addRepeatPrescription(new RepeatPrescription(stranger, paracetamol, 10, 7, null)));
    }

    @Test
    @DisplayName("Invalid repeat prescriptions cannot be created")
    void invalidRepeatPrescriptionsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RepeatPrescription(ama, amoxicillin, 10, 7, " "));
        assertThrows(IllegalArgumentException.class,
                () -> new RepeatPrescription(ama, paracetamol, 0, 7, null));
        assertThrows(IllegalArgumentException.class,
                () -> new RepeatPrescription(ama, paracetamol, 10, 0, null));
    }

    @Test
    @DisplayName("Pick-ups in 7 days: daily 7, every 3 days 3, weekly 1, monthly 1")
    void pickupsWithinWeek() {
        assertEquals(7, new RepeatPrescription(ama, paracetamol, 1, 1, null).pickupsWithin(7));
        assertEquals(3, new RepeatPrescription(ama, paracetamol, 1, 3, null).pickupsWithin(7));
        assertEquals(1, new RepeatPrescription(ama, paracetamol, 1, 7, null).pickupsWithin(7));
        assertEquals(1, new RepeatPrescription(ama, paracetamol, 1, 30, null).pickupsWithin(7));
    }
}