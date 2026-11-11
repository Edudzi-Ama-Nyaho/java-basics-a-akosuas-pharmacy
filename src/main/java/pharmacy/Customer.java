package pharmacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Customer {
    private final String name;
    private final CustomerKind kind;
    private final String phone;
    private final List<String> history = new ArrayList<>();


    Customer(String name, CustomerKind kind, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        this.name = name;
        this.kind = Objects.requireNonNull(kind, "kind");
        this.phone = phone;
    }

    Customer(String name, CustomerKind kind) {
        this(name, kind, null);
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