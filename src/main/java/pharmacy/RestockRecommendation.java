package pharmacy;

/**
 * How many of a medicine to order so that upcoming repeat-prescription pick-ups can be met.
 * For example: need 50, have 39, order 11.
 */
record RestockRecommendation(Medication medication, int needed, int inStock, int toOrder) {
}