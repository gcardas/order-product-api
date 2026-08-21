package cardas.gabriel.orderproductapi.query.filter;

public enum FilterType {
    EQ,
    NEQ,
    STARTS_WITH,
    CONTAINS;

    public static FilterType fromParam(String raw) {
        return switch (raw.trim().toLowerCase()) {
            case "eq" -> EQ;
            case "neq" -> NEQ;
            case "startswith" -> STARTS_WITH;
            case "contains" -> CONTAINS;
            default -> throw new IllegalArgumentException("Unsupported filter operator: " + raw);
        };
    }
}
