package cardas.gabriel.orderproductapi.repository;

import cardas.gabriel.orderproductapi.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String>, OrderRepositoryCustom {
}
