package pharmacy;

/**
 * Something that happened to stock: a dispense or a restock.
 * Sealed, so these two records are the only kinds of transaction that can exist.
 */
sealed interface Transaction permits DispenseRecord, RestockRecord {

    /** The medicine whose stock changed. */
    Medication medication();

    /** How many units moved. */
    int quantity();
}