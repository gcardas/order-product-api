package cardas.gabriel.orderproductapi.query;

public enum OrderItemField {

    NAME("items.name"),
    PRICE("items.price");

    private final String path;

    OrderItemField(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public static OrderItemField fromParam(String raw) {
        for (OrderItemField field : values()) {
            if (field.name().equalsIgnoreCase(raw)) {
                return field;
            }
        }
        // placeholder — swapped for a proper exception type once we build error handling
        throw new IllegalArgumentException("Unsupported field: " + raw);
    }
}
