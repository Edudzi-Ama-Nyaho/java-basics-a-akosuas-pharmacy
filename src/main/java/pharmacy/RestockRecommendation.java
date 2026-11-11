package pharmacy;

/** How many of a medicine to order so that upcoming repeat-prescription pick-ups can be met. */
record RestockRecommendation(Medication medication, int needed, int inStock, int toOrder) {
}