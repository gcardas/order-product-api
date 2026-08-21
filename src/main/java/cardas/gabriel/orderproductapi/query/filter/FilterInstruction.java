package cardas.gabriel.orderproductapi.query.filter;

import cardas.gabriel.orderproductapi.query.OrderItemField;

public record FilterInstruction(OrderItemField field, FilterType operator, String value) {
}
