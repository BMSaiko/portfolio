package pt.isep.sem4pi.lprog;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.visitor.FlightDSLVisitor;
import pt.isep.sem4pi.lprog.error.DSLErrorListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for Flight DSL validation.
 *
 * Orchestrates the full pipeline:
 *   1. Lexical analysis  — FlightDSLLexer  + DSLErrorListener (LEXICAL)
 *   2. Syntactic analysis — FlightDSLParser + DSLErrorListener (SYNTACTIC)
 *   3. Semantic analysis  — delegated to the SemanticValidator (colleague)
 *
 * If lexical or syntactic errors are found the pipeline stops early and
 * returns those errors without attempting semantic validation.
 */
public class FlightDSLValidator {

    /**
     * Validates the Flight DSL file at the given path.
     *
     * @param filePath path to the .dsl file
     * @return a {@link ValidationResult} containing either the parse tree
     *         context (success) or a list of errors (failure)
     * @throws IOException if the file cannot be read
     */
    public ValidationResult validate(Path filePath) throws IOException {
        CharStream input = CharStreams.fromPath(filePath);
        return validate(input);
    }

    /**
     * Validates a Flight DSL string directly (useful for unit tests).
     *
     * @param source the DSL source code as a string
     * @return a {@link ValidationResult}
     */
    public ValidationResult validateString(String source) {
        CharStream input = CharStreams.fromString(source);
        return validate(input);
    }

    // ── Private pipeline ────────────────────────────────────────────────────

    private ValidationResult validate(CharStream input) {
        List<DSLError> allErrors = new ArrayList<>();

        // ── Step 1: Lexical analysis ─────────────────────────────────────
        FlightDSLLexer lexer = new FlightDSLLexer(input);

        // Remove the default ANTLR error listener (prints to stderr)
        // and attach our custom one that collects errors into a list
        lexer.removeErrorListeners();
        DSLErrorListener lexerErrorListener =
                new DSLErrorListener(DSLError.ErrorType.LEXICAL);
        lexer.addErrorListener(lexerErrorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // ── Step 2: Syntactic analysis ───────────────────────────────────
        FlightDSLParser parser = new FlightDSLParser(tokens);

        parser.removeErrorListeners();
        DSLErrorListener parserErrorListener =
                new DSLErrorListener(DSLError.ErrorType.SYNTACTIC);
        parser.addErrorListener(parserErrorListener);

        // Trigger parsing — starts from the root rule
        FlightDSLParser.FlightFileContext parseTree = parser.flightFile();

        // Collect lexical and syntactic errors
        allErrors.addAll(lexerErrorListener.getErrors());
        allErrors.addAll(parserErrorListener.getErrors());

        // If there are lexical or syntactic errors, stop here.
        // Semantic validation on a broken parse tree produces misleading results.
        if (!allErrors.isEmpty()) {
            return ValidationResult.failure(allErrors);
        }

        // ── Step 3: Semantic analysis ────────────────────────────────────
        pt.isep.sem4pi.lprog.semantic.SemanticValidator semanticValidator = new pt.isep.sem4pi.lprog.semantic.SemanticValidator();
        List<DSLError> semanticErrors = semanticValidator.validate(parseTree);

        if (!semanticErrors.isEmpty()) {
            return ValidationResult.failure(semanticErrors);
        }

        // Return the parse tree so that the caller (e.g. US081) can
        // hand it to the Visitor to build the domain objects.
        return ValidationResult.success(parseTree);
    }

    // ── Inner result type ────────────────────────────────────────────────────

    /**
     * Holds the result of a validation run.
     * Either a successful parse tree or a non-empty list of errors.
     */
    public static class ValidationResult {

        private final FlightDSLParser.FlightFileContext parseTree;
        private final List<DSLError> errors;

        private ValidationResult(FlightDSLParser.FlightFileContext parseTree,
                                 List<DSLError> errors) {
            this.parseTree = parseTree;
            this.errors = errors;
        }

        public static ValidationResult success(
                FlightDSLParser.FlightFileContext parseTree) {
            return new ValidationResult(parseTree, List.of());
        }

        public static ValidationResult failure(List<DSLError> errors) {
            return new ValidationResult(null, errors);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        /**
         * Returns the parse tree root. Only call this if {@link #isValid()} is true.
         */
        public FlightDSLParser.FlightFileContext getParseTree() {
            return parseTree;
        }

        /**
         * Returns the list of errors. Empty if validation was successful.
         */
        public List<FlightPlan> getFlightPlans() {
            if (parseTree == null) return List.of();
            FlightDSLVisitor visitor = new FlightDSLVisitor();
            return visitor.visitFlightFile(parseTree);
        }

        public List<DSLError> getErrors() {
            return errors;
        }

        /**
         * Prints a human-readable summary to stdout.
         */
        public void printSummary() {
            if (isValid()) {
                System.out.println("Validation successful — no errors found.");
            } else {
                System.out.printf("Validation failed — %d error(s) found:%n",
                        errors.size());
                errors.forEach(e -> System.out.println("  " + e));
            }
        }
    }
}
