package eapli.aisafe.app.backoffice.console.presentation.reportmanagement;

import eapli.aisafe.app.common.console.AbstractUI;

/**
 * Menu UI for report generation.
 * <p>
 * Allows the user to navigate to different report generation options.
 * </p>
 */
public class ReportsMenuUI extends AbstractUI {

    private final GenerateMonthlyReportUI generateMonthlyReportUI;

    public ReportsMenuUI(final GenerateMonthlyReportUI generateMonthlyReportUI) {
        this.generateMonthlyReportUI = generateMonthlyReportUI;
    }

    @Override
    protected boolean doShow() {
        while (true) {
            System.out.println();
            System.out.println("Reports");
            System.out.println("1. Generate Monthly Report");
            System.out.println("0. Back");
            System.out.print("\nOption: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    generateMonthlyReportUI.show();
                    break;
                case "0":
                    return true;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
