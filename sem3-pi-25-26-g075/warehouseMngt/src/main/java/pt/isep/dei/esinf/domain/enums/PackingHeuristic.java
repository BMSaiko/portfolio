package pt.isep.dei.esinf.domain.enums;

public enum PackingHeuristic {
    FIRST_FIT("First Fit (FF)", "Place the item in the first available trolley where it fits"),
    FIRST_FIT_DECREASING("First Fit Decreasing (FFD)", "Sort by weight descending, then place in first available trolley"),
    BEST_FIT_DECREASING("Best Fit Decreasing (BFD)", "Sort by weight descending, then place in trolley with tightest fit");

    private final String displayName;
    private final String description;

    PackingHeuristic(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}