package pt.isep.sem4pi.lprog.application;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.util.List;

public class ImportResult {

    private final boolean success;
    private final List<DSLError> errors;
    private final List<FlightPlan> flightPlans;

    private ImportResult(boolean success, List<DSLError> errors, List<FlightPlan> flightPlans) {
        this.success = success;
        this.errors = errors;
        this.flightPlans = flightPlans;
    }

    public static ImportResult success(List<FlightPlan> flightPlans) {
        return new ImportResult(true, List.of(), flightPlans);
    }

    public static ImportResult success() {
        return new ImportResult(true, List.of(), List.of());
    }

    public static ImportResult failure(List<DSLError> errors) {
        return new ImportResult(false, errors, List.of());
    }

    public boolean isSuccess() {
        return success;
    }

    public List<DSLError> getErrors() {
        return errors;
    }

    public List<FlightPlan> getFlightPlans() {
        return flightPlans;
    }
}