package cardas.gabriel.orderproductapi.query.sort;

import cardas.gabriel.orderproductapi.query.OrderItemField;
import org.springframework.data.domain.Sort;

public record SortInstruction(OrderItemField field, Sort.Direction direction) {
}
