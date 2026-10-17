package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * Flight-level semantic rules (section 3.4.5 — Flight-level rules):
 *
 *  R1 — The flight identifier must be unique within the file.
 *        NOTE: uniqueness across multiple FlightPlans is enforced by the
 *        FlightDSLListener (which has access to all plans at once).
 *        This rule validates that the identifier is non-blank as a
 *        per-plan guard.
 *
 *  R2 — The flight type must be one of the allowed values (REGULAR / CHARTER).
 *        The grammar already restricts this lexically, but an explicit
 *        semantic check produces a clear, domain-level error message.
 */
public class FlightLevelRules implements SemanticRule {

    private static final List<String> ALLOWED_TYPES =
            List.of("regular", "charter");

    @Override
    public List<DSLError> validate(FlightPlan plan) {
        List<DSLError> errors = new ArrayList<>();

        // R1 — identifier must not be blank
        if (plan.getId() == null || plan.getId().isBlank()) {
            errors.add(build(plan.getLine(), plan.getColumn(),
                    "O identificador do voo não pode estar vazio."));
        }

        // R2 — type must be REGULAR or CHARTER (case-insensitive)
        if (plan.getType() == null ||
                !ALLOWED_TYPES.contains(plan.getType().toLowerCase())) {
            errors.add(build(plan.getLine(), plan.getColumn(),
                    "Tipo de voo inválido: '" + plan.getType() +
                            "'. Valores permitidos: REGULAR, CHARTER."));
        }

        return errors;
    }

    private DSLError build(int line, int col, String msg) {
        return new DSLError(DSLError.ErrorType.SEMANTIC, line, col, msg);
    }
}