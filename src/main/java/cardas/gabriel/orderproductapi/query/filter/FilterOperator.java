package cardas.gabriel.orderproductapi.query.filter;

import org.springframework.data.mongodb.core.query.Criteria;

public interface FilterOperator {

    FilterType type();

    Criteria toCriteria(String fieldPath, String value);
}
