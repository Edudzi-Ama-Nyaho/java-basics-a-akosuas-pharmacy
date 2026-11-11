package pharmacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A customer of the pharmacy. Registered patients may have a phone number on file.
 * The history can only be added to by the pharmacy; callers get a read-only copy.
 */
class Customer {
    private final String name;
    private final CustomerKind kind;
    private final String phone;
    private final List<String> history = new ArrayList<>();

    /** A customer with no phone number on file, such as a walk-in. */
    Customer(String name, CustomerKind kind) {
        this(name, kind, null);
    }

    Customer(String name, CustomerKind kind, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        this.name = name;
        this.kind = Objects.requireNonNull(kind, "kind");
        this.phone = phone;
    }

    String getName() {
        return name;
    }

    CustomerKind getKind() {
        return kind;
    }

    /** The phone number on file, or null if there is none. */
    String getPhone() {
        return phone;
    }

    /** Package-private so that only the pharmacy can add to a customer's history. */
    void recordInHistory(String entry) {
        history.add(entry);
    }

    /** A read-only copy; trying to change it throws an exception. */
    List<String> getHistory() {
        return List.copyOf(history);
    }
}