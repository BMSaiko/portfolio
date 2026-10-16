package pt.isep.sem4pi.lprog.testing;

/**
 * Represents a single physical validation issue reported by the C test component.
 *
 * <p>Mirrors one element of the {@code "issues"} array in the JSON output of
 * {@code flight_tester}:
 * <pre>
 *   { "field": "leg[0].fuel", "message": "Fuel quantity must be strictly positive..." }
 * </pre>
 */
public class PhysicalIssue {

    private final String field;
    private final String message;

    public PhysicalIssue(String field, String message) {
        if (field   == null || field.isBlank())   throw new IllegalArgumentException("field must not be blank");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("message must not be blank");
        this.field   = field;
        this.message = message;
    }

    /** The JSON path of the offending field, e.g. {@code "leg[0].segment[1].altitude"}. */
    public String getField()   { return field;   }

    /** Human-readable description of the problem. */
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "[" + field + "] " + message;
    }
}
