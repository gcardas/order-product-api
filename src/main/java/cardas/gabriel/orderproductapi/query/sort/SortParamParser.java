package cardas.gabriel.orderproductapi.query.sort;

import cardas.gabriel.orderproductapi.query.OrderItemField;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public final class SortParamParser {

    private SortParamParser() {
    }

    public static List<SortInstruction> parse(String rawSortParam) {
        if (rawSortParam == null || rawSortParam.isBlank()) {
            return List.of();
        }

        List<SortInstruction> instructions = new ArrayList<>();
        for (String part : rawSortParam.split(",")) {
            String[] pieces = part.split(":");
            if (pieces.length != 2) {
                throw new IllegalArgumentException("Invalid sort parameter: " + part);
            }
            OrderItemField field = OrderItemField.fromParam(pieces[0].trim());
            Sort.Direction direction = Sort.Direction.fromString(pieces[1].trim());
            instructions.add(new SortInstruction(field, direction));
        }
        return instructions;
    }
}
