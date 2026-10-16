package pt.isep.sem4pi.lprog.application;

import pt.isep.sem4pi.lprog.FlightDSLValidator;
import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FlightPlanImportService {

    private final FlightDSLValidator validator;

    public FlightPlanImportService() {
        this.validator = new FlightDSLValidator();
    }

    public ImportResult importFlightPlan(Path filePath) {

        try {
            FlightDSLValidator.ValidationResult result = validator.validate(filePath);

            if (!result.isValid()) {
                return ImportResult.failure(result.getErrors());
            }

            List<FlightPlan> flightPlans = result.getFlightPlans();
            return ImportResult.success(flightPlans);

        } catch (IOException e) {

            return ImportResult.failure(
                    List.of(
                            new DSLError(
                                    DSLError.ErrorType.LEXICAL,
                                    0,
                                    0,
                                    "Erro ao ler ficheiro: " + e.getMessage()
                            )
                    )
            );
        }
    }
}