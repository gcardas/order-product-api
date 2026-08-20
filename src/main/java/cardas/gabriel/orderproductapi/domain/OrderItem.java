package cardas.gabriel.orderproductapi.domain;

import java.math.BigDecimal;

public record OrderItem(String productId, String name, BigDecimal price, int quantity) {
}
