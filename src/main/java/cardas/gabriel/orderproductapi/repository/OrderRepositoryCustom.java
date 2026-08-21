package cardas.gabriel.orderproductapi.repository;

import cardas.gabriel.orderproductapi.query.PagedProducts;
import cardas.gabriel.orderproductapi.query.filter.FilterInstruction;
import cardas.gabriel.orderproductapi.query.sort.SortInstruction;

import java.util.List;

public interface OrderRepositoryCustom {

    PagedProducts findProductsInOrder(
            String orderId,
            List<FilterInstruction> filters,
            List<SortInstruction> sorts,
            int page,
            int limit
    );
}
