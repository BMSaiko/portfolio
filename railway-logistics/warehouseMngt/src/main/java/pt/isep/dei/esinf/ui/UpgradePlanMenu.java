package pt.isep.dei.esinf.ui;

import java.util.List;

public class UpgradePlanMenu extends Menu {
    public UpgradePlanMenu() {
        super(List.of(
            "Compute topological upgrade order",
            "Show detailed cycle analysis",
            "Show network statistics",
            "Validate graph structure"
        ));
    }
}
