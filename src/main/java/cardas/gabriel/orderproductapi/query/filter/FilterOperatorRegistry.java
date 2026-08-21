package cardas.gabriel.orderproductapi.query.filter;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FilterOperatorRegistry {

    private final Map<FilterType, FilterOperator> operators;

    public FilterOperatorRegistry(List<FilterOperator> operatorBeans) {
        this.operators = operatorBeans.stream()
                .collect(Collectors.toMap(FilterOperator::type, Function.identity()));
    }

    public Criteria resolve(FilterType type, String fieldPath, String value) {
        FilterOperator operator = operators.get(type);
        if (operator == null) {
            throw new UnsupportedOperationException("Unsupported filter operator: " + type);
        }
        return operator.toCriteria(fieldPath, value);
    }
}
