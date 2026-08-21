package cardas.gabriel.orderproductapi.query.sort;

import org.springframework.data.domain.Sort;

import java.util.List;

public final class SortInstructions {

    private SortInstructions() {
    }

    public static Sort toSort(List<SortInstruction> instructions) {
        if (instructions.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = instructions.stream()
                .map(instruction -> new Sort.Order(instruction.direction(), instruction.field().path()))
                .toList();
        return Sort.by(orders);
    }
}
