package pt.isep.sem4pi.lprog.listener;

import pt.isep.sem4pi.lprog.FlightDSLBaseListener;
import pt.isep.sem4pi.lprog.FlightDSLParser;
import pt.isep.sem4pi.lprog.error.DSLError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlightDSLListener extends FlightDSLBaseListener {
    private final Set<String> flightIds = new HashSet<>();
    private final List<DSLError> semanticErrors = new ArrayList<>();

    @Override
    public void enterFlightPlan(FlightDSLParser.FlightPlanContext ctx) {
        String id = ctx.identifier().getText();
        if (!flightIds.add(id)) {
            semanticErrors.add(new DSLError(
                    DSLError.ErrorType.SEMANTIC, // Assumindo que o teu colega criou este Enum
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine(),
                    "Flight identifier tem de ser único no ficheiro. Duplicado: " + id
            ));
        }
    }

    public List<DSLError> getSemanticErrors() {
        return semanticErrors;
    }
}