package pt.isep.sem4pi.lprog.error;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ANTLR error listener that captures lexical and syntactic errors
 * into a list of {@link DSLError} objects instead of printing to stderr.
 *
 * Attach this listener to both the lexer and the parser to collect all
 * errors in a single pass.
 */
public class DSLErrorListener extends BaseErrorListener {

    private final DSLError.ErrorType errorType;
    private final List<DSLError> errors = new ArrayList<>();

    /**
     * @param errorType LEXICAL when attached to the lexer,
     *                  SYNTACTIC when attached to the parser.
     */
    public DSLErrorListener(DSLError.ErrorType errorType) {
        this.errorType = errorType;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        errors.add(new DSLError(errorType, line, charPositionInLine, msg));
    }

    /**
     * Returns all errors collected so far.
     */
    public List<DSLError> getErrors() {
        return errors;
    }

    /**
     * Returns true if at least one error was collected.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
