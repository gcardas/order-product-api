package cardas.gabriel.orderproductapi.controller;

import cardas.gabriel.orderproductapi.query.PagedProducts;
import cardas.gabriel.orderproductapi.query.filter.FilterInstruction;
import cardas.gabriel.orderproductapi.query.filter.FilterParamParser;
import cardas.gabriel.orderproductapi.query.sort.SortInstruction;
import cardas.gabriel.orderproductapi.query.sort.SortParamParser;
import cardas.gabriel.orderproductapi.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/orders/{orderId}/products")
    public PagedProducts getProducts(
            @PathVariable String orderId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        List<FilterInstruction> filters = FilterParamParser.parse(filter);
        List<SortInstruction> sorts = SortParamParser.parse(sort);

        return orderService.getProductsForOrder(orderId, filters, sorts, page, limit);
    }
}
