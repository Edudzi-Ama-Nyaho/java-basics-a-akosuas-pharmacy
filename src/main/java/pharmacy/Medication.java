package pharmacy;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A medicine the pharmacy stocks. This class is the only owner of its stock count:
 * stock can only change through removeStock and addStock, which never let it go below zero.
 */
class Medication {
    private final String name;
    private final BigDecimal price;
    private final Category category;
    private int stock;

    /** Refuses a blank name, a missing or negative price, negative stock, or a missing category. */
    Medication(String name, BigDecimal price, int initialStock, Category category) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Medication name is required");
        }
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative, got " + price);
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException(
                    "Initial stock cannot be negative, got " + initialStock);
        }
        this.name = name;
        this.price = price;
        this.stock = initialStock;
        this.category = Objects.requireNonNull(category, "category");
    }

    String getName() {
        return name;
    }

    BigDecimal getPrice() {
        return price;
    }

    Category getCategory() {
        return category;
    }

    int getStock() {
        return stock;
    }

    /**
     * Takes stock away for a dispense. This is the never-below-zero rule: if more is asked for
     * than is in stock, it refuses before anything changes.
     */
    void removeStock(int quantity) throws InsufficientStockException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got " + quantity);
        }
        if (quantity > stock) {
            throw new InsufficientStockException(name, quantity, stock);
        }
        stock = stock - quantity;
    }

    /**
     * Adds stock for a restock. A zero or negative quantity is refused,
     * so a "restock" can never quietly reduce stock.
     */
    void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Restock quantity must be positive, got " + quantity);
        }
        stock = stock + quantity;
    }
}