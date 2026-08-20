package cardas.gabriel.orderproductapi.domain;

import java.time.Instant;

public record StatusChange(ProductStatus status, Instant changedAt) {
}
