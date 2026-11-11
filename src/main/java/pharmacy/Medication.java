package pharmacy;

class Medication {
    private final String name;
    private final double price;
    private final Category category;
    private int stock;

    Medication(String name, double price, int initialStock, Category category) {
        this.name = name;
        this.price = price;
        this.stock = initialStock;
        this.category = category;
    }

    String getName() {
        return name;
    }

    double getPrice() {
        return price;
    }

    Category getCategory() {
        return category;
    }

    int getStock() {
        return stock;
    }

    void removeStock(int qty) {
        stock = stock - qty;
    }

    void addStock(int qty) {
        stock = stock + qty;
    }
}