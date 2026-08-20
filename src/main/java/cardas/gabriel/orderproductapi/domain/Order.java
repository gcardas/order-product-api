package cardas.gabriel.orderproductapi.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
@Getter
public class Order {

    @Id
    private String id;

    @CreatedDate
    private Instant createdDate;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private List<OrderItem> items;

    public Order(List<OrderItem> items) {
        this.items = items;
        this.status = OrderStatus.PLACED;
        this.totalAmount = items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
