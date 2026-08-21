package cardas.gabriel.orderproductapi.service;

import cardas.gabriel.orderproductapi.exception.OrderNotFoundException;
import cardas.gabriel.orderproductapi.query.PagedProducts;
import cardas.gabriel.orderproductapi.query.filter.FilterInstruction;
import cardas.gabriel.orderproductapi.query.sort.SortInstruction;
import cardas.gabriel.orderproductapi.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PagedProducts getProductsForOrder(
            String orderId,
            List<FilterInstruction> filters,
            List<SortInstruction> sorts,
            int page,
            int limit
    ) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException(orderId);
        }

        return orderRepository.findProductsInOrder(orderId, filters, sorts, page, limit);
    }
}
