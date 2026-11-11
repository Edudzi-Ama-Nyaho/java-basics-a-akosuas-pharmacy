package pharmacy;

class Medication {
    public String name;
    public double price;
    public int stock;
    public Category category;
    public int id;
    static int counter = 1;

    public Medication(String name, double price, int stock, Category category) {
        this.id = counter;
        counter++;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}