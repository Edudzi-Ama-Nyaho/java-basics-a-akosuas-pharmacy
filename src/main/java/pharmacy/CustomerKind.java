package pharmacy;

import java.math.BigDecimal;

enum CustomerKind {
    WALK_IN(BigDecimal.ONE),
    REGISTERED(new BigDecimal("0.95"));

    private final BigDecimal priceMultiplier;

    CustomerKind(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    BigDecimal applyDiscount(BigDecimal total) {
        return total.multiply(priceMultiplier);
    }
}