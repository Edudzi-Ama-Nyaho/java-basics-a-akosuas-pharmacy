package pharmacy;

import java.util.ArrayList;
import java.util.List;

class Customer {
    public String name;
    public CustomerKind kind;
    public String phone;
    public int id;
    public List<String> history = new ArrayList<>();
    static int counter = 1;

    public Customer(String name, CustomerKind kind) {
        this.id = counter;
        counter++;
        this.name = name;
        this.kind = kind;
    }
}
