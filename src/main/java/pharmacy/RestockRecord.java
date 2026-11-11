package pharmacy;

record RestockRecord(Medication medication, int quantity) implements Transaction {
}