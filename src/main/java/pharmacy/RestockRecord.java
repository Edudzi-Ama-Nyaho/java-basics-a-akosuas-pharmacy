package pharmacy;

/**
 * An immutable record of one restock. Unlike a dispense it has no customer, price or
 * prescriber, so it doesn't need placeholder values like the original's "(supplier)".
 */
record RestockRecord(Medication medication, int quantity) implements Transaction {
}