package pt.isep.sem4pi.lprog.error;

/**
 * Represents a single error found during lexical, syntactic or semantic
 * validation of a Flight DSL file.
 */
public class DSLError {

    public enum ErrorType {
        LEXICAL,
        SYNTACTIC,
        SEMANTIC
    }

    private final ErrorType type;
    private final int line;
    private final int column;
    private final String message;

    public DSLError(ErrorType type, int line, int column, String message) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] line %d:%d — %s", type, line, column, message);
    }
}
