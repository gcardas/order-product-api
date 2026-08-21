package cardas.gabriel.orderproductapi.query;

import cardas.gabriel.orderproductapi.domain.OrderItem;

import java.util.List;

public record PagedProducts(List<OrderItem> items, int page, int limit, long totalCount) {
}
