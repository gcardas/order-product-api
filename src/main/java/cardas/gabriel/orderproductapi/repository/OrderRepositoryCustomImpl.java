package cardas.gabriel.orderproductapi.repository;

import cardas.gabriel.orderproductapi.domain.OrderItem;
import cardas.gabriel.orderproductapi.query.PagedProducts;
import cardas.gabriel.orderproductapi.query.filter.FilterInstruction;
import cardas.gabriel.orderproductapi.query.sort.SortInstructions;
import cardas.gabriel.orderproductapi.query.filter.FilterOperatorRegistry;
import cardas.gabriel.orderproductapi.query.sort.SortInstruction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    private final FilterOperatorRegistry filterOperatorRegistry;

    public OrderRepositoryCustomImpl(MongoTemplate mongoTemplate, FilterOperatorRegistry filterOperatorRegistry) {
        this.mongoTemplate = mongoTemplate;
        this.filterOperatorRegistry = filterOperatorRegistry;
    }

    @Override
    public PagedProducts findProductsInOrder(
            String orderId, List<FilterInstruction> filters, List<SortInstruction> sorts, int page, int limit
    ) {
        // shared by both queries below: narrow to this order, unwind, apply filters
        List<AggregationOperation> baseStages = new ArrayList<>();
        baseStages.add(Aggregation.match(Criteria.where("_id").is(orderId)));
        baseStages.add(Aggregation.unwind("items"));
        for (FilterInstruction filter : filters) {
            Criteria criteria = filterOperatorRegistry.resolve(
                    filter.operator(), filter.field().path(), filter.value());
            baseStages.add(Aggregation.match(criteria));
        }

        long totalCount = countMatches(baseStages);
        List<OrderItem> items = fetchPage(baseStages, sorts, page, limit);

        return new PagedProducts(items, page, limit, totalCount);
    }

    private long countMatches(List<AggregationOperation> baseStages) {
        List<AggregationOperation> countStages = new ArrayList<>(baseStages);
        countStages.add(Aggregation.count().as("total")); // counts documents surviving so far — before skip/limit

        AggregationResults<CountResult> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(countStages), "orders", CountResult.class);

        CountResult result = results.getUniqueMappedResult();
        return result != null ? result.total() : 0;
    }

    private List<OrderItem> fetchPage(
            List<AggregationOperation> baseStages, List<SortInstruction> sorts, int page, int limit
    ) {
        List<AggregationOperation> dataStages = new ArrayList<>(baseStages);
        if (!sorts.isEmpty()) {
            dataStages.add(Aggregation.sort(SortInstructions.toSort(sorts)));
        }
        dataStages.add(Aggregation.skip((long) page * limit));
        dataStages.add(Aggregation.limit((long) limit));
        dataStages.add(Aggregation.replaceRoot("items"));

        AggregationResults<OrderItem> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(dataStages), "orders", OrderItem.class);
        return results.getMappedResults();
    }

    private record CountResult(long total) {
    }

}
