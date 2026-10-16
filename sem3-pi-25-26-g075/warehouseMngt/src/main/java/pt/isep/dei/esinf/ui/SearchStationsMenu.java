package pt.isep.dei.esinf.ui;

import java.util.List;

public class SearchStationsMenu extends Menu {

    public SearchStationsMenu() {
        super(
            List.of(
                "Search by time zone group",
                "Search by coordinates",
                "Search by country",
                "Search by coordinates range",
                "View statistics"
            )
        );
    }
}
