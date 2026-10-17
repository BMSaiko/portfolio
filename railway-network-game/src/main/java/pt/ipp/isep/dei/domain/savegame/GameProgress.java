package pt.ipp.isep.dei.domain.savegame;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents game progress and achievements
 */
public class GameProgress {
    private int totalCargoDelivered;
    private double totalRevenue;
    private int totalTrainsOwned;
    private int totalStationsBuilt;
    private Map<String, Object> customProgress;

    public GameProgress() {
        this.totalCargoDelivered = 0;
        this.totalRevenue = 0.0;
        this.totalTrainsOwned = 0;
        this.totalStationsBuilt = 0;
        this.customProgress = new HashMap<>();
    }

    // Getters
    public int getTotalCargoDelivered() {
        return totalCargoDelivered;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalTrainsOwned() {
        return totalTrainsOwned;
    }

    public int getTotalStationsBuilt() {
        return totalStationsBuilt;
    }

    public Map<String, Object> getCustomProgress() {
        return customProgress;
    }

    // Setters
    public void setTotalCargoDelivered(int totalCargoDelivered) {
        this.totalCargoDelivered = totalCargoDelivered;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void setTotalTrainsOwned(int totalTrainsOwned) {
        this.totalTrainsOwned = totalTrainsOwned;
    }

    public void setTotalStationsBuilt(int totalStationsBuilt) {
        this.totalStationsBuilt = totalStationsBuilt;
    }

    public void setCustomProgress(Map<String, Object> customProgress) {
        this.customProgress = customProgress;
    }

    // Utility methods
    public void addCustomProgress(String key, Object value) {
        this.customProgress.put(key, value);
    }

    public Object getCustomProgress(String key) {
        return this.customProgress.get(key);
    }
}
