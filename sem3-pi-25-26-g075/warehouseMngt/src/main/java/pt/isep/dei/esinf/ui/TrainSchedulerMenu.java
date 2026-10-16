package pt.isep.dei.esinf.ui;

import java.util.List;

public class TrainSchedulerMenu extends Menu {
    public TrainSchedulerMenu() {
        super(
            List.of(
                "View All Scheduled Trains",
                "Calculate Schedule for Single Train",
                "Calculate Schedule for All Trains",
                "View Train Details"
            )
        );
    }
}
