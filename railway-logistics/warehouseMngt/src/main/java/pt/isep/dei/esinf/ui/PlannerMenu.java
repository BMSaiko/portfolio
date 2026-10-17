package pt.isep.dei.esinf.ui;

import java.util.List;

public class PlannerMenu extends Menu {
    public PlannerMenu() {
        super(
            List.of(
                "View bay contents",
                "Dispatch SKU",
                "Relocate box",
                "View inventory by SKU",
                "Process orders",
                "Pack allocations",
                "Search stations",
                "Railway upgrade",
                "Compute minimal backbone",
                "Comput centrality measures"
            )
        );
    }
}
