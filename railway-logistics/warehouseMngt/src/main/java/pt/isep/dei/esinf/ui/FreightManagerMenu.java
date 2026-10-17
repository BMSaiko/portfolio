package pt.isep.dei.esinf.ui;

import java.util.List;


public class FreightManagerMenu extends Menu {
    public FreightManagerMenu() {
        super(
            List.of(
                "Plan route",
                "View all freights",
                "View pending freights"
            )
        );
    }
}
