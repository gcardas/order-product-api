package cardas.gabriel.orderproductapi.query.filter;

import cardas.gabriel.orderproductapi.query.filter.operator.EqualsFilterOperator;
import cardas.gabriel.orderproductapi.query.filter.operator.NeqFilterOperator;
import cardas.gabriel.orderproductapi.query.filter.operator.StartsWithFilterOperator;
import cardas.gabriel.orderproductapi.query.filter.operator.ContainsFilterOperator;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class FilterOperatorRegistrySanityTest {
    @Test
    void printsGeneratedCriteriaForEachOperator() {
        FilterOperatorRegistry registry = new FilterOperatorRegistry(List.of(
                new EqualsFilterOperator(),
                new NeqFilterOperator(),
                new StartsWithFilterOperator(),
                new ContainsFilterOperator()
        ));

        printCriteria(registry.resolve(FilterType.EQ, "items.name", "Wireless Mouse"));
        printCriteria(registry.resolve(FilterType.NEQ, "items.name", "Wireless Mouse"));
        printCriteria(registry.resolve(FilterType.STARTS_WITH, "items.name", "Wireless"));
        printCriteria(registry.resolve(FilterType.CONTAINS, "items.name", "Mouse"));
    }

    private void printCriteria(Criteria criteria) {
        System.out.println(criteria.getCriteriaObject().toJson());
    }
}
