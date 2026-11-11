package pharmacy;

sealed interface Transaction permits DispenseRecord, RestockRecord {
    Medication medication();

    int quantity();
}