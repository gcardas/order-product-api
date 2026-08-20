package cardas.gabriel.orderproductapi.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Getter
public class Product {

    @Id
    private String id;

    private String name;

    private BigDecimal price;

    @Indexed(unique = true)
    private String sku;

    private ProductStatus status;

    private List<StatusChange> statusHistory;

    private int stockQuantity;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Product(String name, BigDecimal price, String sku, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.sku = sku;
        this.stockQuantity = stockQuantity;
        this.status = ProductStatus.DRAFT;
        this.statusHistory = new ArrayList<>();
        this.statusHistory.add(new StatusChange(ProductStatus.DRAFT, Instant.now()));
    }
}