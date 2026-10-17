package pt.isep.sem4pi.lprog;

import pt.isep.sem4pi.lprog.application.FlightPlanImportService;
import pt.isep.sem4pi.lprog.application.ImportResult;
import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestInput;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestInputBuilder;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestResult;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestRunner;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        boolean fullValidation = args.length > 0 && "--validate".equalsIgnoreCase(args[0]);

        FlightPlanImportService importService = new FlightPlanImportService();

        // Caminho padrão do ficheiro DSL para testes rápidos
        Path dslPath = Path.of("lprog/src/main/resources/examples/invalid_intraleg_times.dsl");

        System.out.println("=== Step 1: Parsing DSL ===");
        ImportResult importResult = importService.importFlightPlan(dslPath);

        if (!importResult.isSuccess()) {
            System.err.println("DSL Syntax/Semantic Errors found:");
            importResult.getErrors().forEach(System.err::println);
            System.exit(1);
        }

        System.out.println("DSL Import successful! No syntax or basic semantic errors.");

        // 2. Se o argumento --validate foi passado, corre o EIXO 1 (Mundo C) para cada plano (US085)
        if (fullValidation) {
            System.out.println("\n=== Step 2: Running Physics Validation (C Component) ===");

            List<FlightPlan> plans = importResult.getFlightPlans();
            if (plans == null || plans.isEmpty()) {
                System.out.println("No flight plans found to validate.");
                return;
            }

            FlightPlanTestRunner runner = new FlightPlanTestRunner();
            boolean anyFailed = false;

            for (FlightPlan plan : plans) {

                System.out.println("\nValidating flight: " + plan.getId());

                FlightPlanTestInput input = FlightPlanTestInputBuilder.from(plan);

                FlightPlanTestResult cResult = runner.run(input);

                if (cResult.isExecutionError()) {
                    System.err.println("  [Error] Failed to call C binary: " + cResult.getExecutionErrorMessage());
                    anyFailed = true;
                    continue;
                }

                if (cResult.isPassed()) {
                    System.out.println("  [SUCCESS] Passed all physical constraints in C.");
                } else {
                    System.err.println("  [FAILED] Physical validation issues found:");
                    cResult.getIssues().forEach(issue ->
                            System.err.println("    - [" + issue.getField() + "]: " + issue.getMessage())
                    );
                    anyFailed = true;
                }
            }

            if (anyFailed) {
                System.exit(2);
            }

        } else {
            System.out.println("\nHint: To run full physics validation against the C component, run with '--validate' argument.");
        }
    }
}