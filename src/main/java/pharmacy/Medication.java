package pharmacy;

import java.math.BigDecimal;

class Medication {
    private final String name;
    private final BigDecimal price;
    private final Category category;
    private int stock;

    Medication(String name, BigDecimal price, int initialStock, Category category) {
        this.name = name;
        this.price = price;
        this.stock = initialStock;
        this.category = category;
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