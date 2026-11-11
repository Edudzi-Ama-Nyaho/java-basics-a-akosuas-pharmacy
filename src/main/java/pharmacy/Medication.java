package pharmacy;

import java.math.BigDecimal;
import java.util.Objects;

class Medication {
    private final String name;
    private final BigDecimal price;
    private final Category category;
    private int stock;

    Medication(String name, BigDecimal price, int initialStock, Category category) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Medication name is required");
        }
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative, got " + price);
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative, got " + initialStock);
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

    void removeStock(int qty) throws InsufficientStockException {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got " + qty);
        }
        if (qty > stock) {
            throw new InsufficientStockException(name, qty, stock);
        }
        stock = stock - qty;
    }

    void addStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive, got " + qty);
        }
        stock = stock + qty;
    }
}