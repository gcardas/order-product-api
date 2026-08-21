package cardas.gabriel.orderproductapi.query.filter;

import cardas.gabriel.orderproductapi.query.OrderItemField;

import java.util.ArrayList;
import java.util.List;

public final class FilterParamParser {

    private FilterParamParser() {
    }

    public static List<FilterInstruction> parse(String rawFilterParam) {
        if (rawFilterParam == null || rawFilterParam.isBlank()) {
            return List.of();
        }

        List<FilterInstruction> instructions = new ArrayList<>();
        for (String part : rawFilterParam.split(",")) {
            String[] pieces = part.split(":", 3); // limit 3: value itself could contain a colon
            if (pieces.length != 3) {
                throw new IllegalArgumentException("Invalid filter parameter: " + part);
            }
            instructions.add(new FilterInstruction(
                    OrderItemField.fromParam(pieces[0].trim()),
                    FilterType.fromParam(pieces[1].trim()),
                    pieces[2].trim()
            ));
        }
        return instructions;
    }
}
