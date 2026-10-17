package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.Player;

public class PlayerDTO {
    private int id;
    private String name;
    private double budget;
    private int trainCount;

    public PlayerDTO(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.budget = player.getFunds();
        this.trainCount= player.getTrainCount();
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getBudget() { return budget; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBudget(double budget) { this.budget = budget; }
    public int getTrainCount() { return trainCount; }
}
