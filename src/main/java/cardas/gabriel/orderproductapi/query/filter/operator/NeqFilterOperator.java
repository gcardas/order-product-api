package cardas.gabriel.orderproductapi.query.filter.operator;

import cardas.gabriel.orderproductapi.query.filter.FilterOperator;
import cardas.gabriel.orderproductapi.query.filter.FilterType;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class NeqFilterOperator implements FilterOperator {
    @Override
    public FilterType type() {
        return FilterType.NEQ;
    }

    @Override
    public Criteria toCriteria(String fieldPath, String value) {
        return Criteria.where(fieldPath).ne(value);
    }
}
