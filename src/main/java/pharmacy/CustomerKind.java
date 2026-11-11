package pharmacy;

import java.math.BigDecimal;

/** Walk-in customers or registered patients; each kind carries its own price multiplier. */
enum CustomerKind {
    WALK_IN(BigDecimal.ONE),
    // Built from a String so the value is exactly 0.95 (new BigDecimal(0.95) would not be).
    REGISTERED(new BigDecimal("0.95"));

    private final BigDecimal priceMultiplier;

    CustomerKind(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    /**
     * Applies this kind's discount to a total: walk-ins pay full price,
     * registered patients 5% less.
     */
    BigDecimal applyDiscount(BigDecimal total) {
        return total.multiply(priceMultiplier);
    }
}