package pt.ipp.isep.dei.dto;

public class UpgradeResultDTO {
    private boolean success;
    private String message;
    private StationDTO updatedStation;
    private double newBudget;

    public UpgradeResultDTO(boolean success, String message, StationDTO station, double budget) {
        this.success = success;
        this.message = message;
        this.updatedStation = station;
        this.newBudget = budget;
    }

    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public StationDTO getUpdatedStation() { return updatedStation; }
    public void setUpdatedStation(StationDTO updatedStation) { this.updatedStation = updatedStation; }
    public double getNewBudget() { return newBudget; }
    public void setNewBudget(double newBudget) { this.newBudget = newBudget; }
}
