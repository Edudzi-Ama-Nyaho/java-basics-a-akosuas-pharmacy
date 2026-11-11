package pharmacy;

import java.util.ArrayList;
import java.util.List;

class Customer {
    private final String name;
    private final CustomerKind kind;
    private final String phone;
    private final List<String> history = new ArrayList<>();

    Customer(String name, CustomerKind kind) {
        this(name, kind, null);
    }

    Customer(String name, CustomerKind kind, String phone) {
        this.name = name;
        this.kind = kind;
        this.phone = phone;
    }

    String getName() {
        return name;
    }

    CustomerKind getKind() {
        return kind;
    }

    String getPhone() {
        return phone;
    }

    void recordInHistory(String entry) {
        history.add(entry);
    }

    List<String> getHistory() {
        return List.copyOf(history);
    }
}