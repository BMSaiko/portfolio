package pt.isep.dei.esinf.domain.train;

import pt.isep.dei.esinf.domain.railway.TrainPath;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Train {
    private int id;
    private LocalDateTime departureDateTime;
    private TrainPath path;
    private List<Locomotive> locomotives;
    private List<Wagon> wagons;
    private List<Integer> freightIds; // associated freight IDs

    public Train(int id, LocalDateTime departureDateTime, TrainPath path) {
        this.id = id;
        this.departureDateTime = departureDateTime;
        this.path = path;
        this.locomotives = new ArrayList<>();
        this.wagons = new ArrayList<>();
        this.freightIds = new ArrayList<>();
    }

    public int getId() { return id; }
    public LocalDateTime getDepartureDateTime() { return departureDateTime; }
    public TrainPath getPath() { return path; }
    public List<Locomotive> getLocomotives() { return locomotives; }
    public List<Wagon> getWagons() { return wagons; }
    public List<Integer> getFreightIds() { return freightIds; }

    public void addLocomotive(Locomotive locomotive) {
        locomotives.add(locomotive);
    }

    public void addWagon(Wagon wagon) {
        wagons.add(wagon);
    }

    public void addFreightId(Integer freightId) {
        freightIds.add(freightId);
    }

    public int getTotalPowerKw() {
        return locomotives.stream()
                .mapToInt(l -> l.getModel().getPower())
                .sum();
    }

    public int getTotalWeightTons() {
        int locoWeight = locomotives.stream()
                .mapToInt(l -> l.getModel().getWeight())
                .sum();
        int wagonWeight = wagons.stream()
                .mapToInt(Wagon::getTotalWeight)
                .sum();
        return locoWeight + wagonWeight;
    }

    public int getMaxWagonSpeedKmh() {
        return wagons.stream()
                .mapToInt(w -> w.getModel().getMaximumSpeed())
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    public int getMaxLocomotiveSpeedKmh() {
        return locomotives.stream()
                .mapToInt(l -> l.getModel().getMaximumSpeed())
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    public int getLoadedWagonCount() {
        return (int) wagons.stream().filter(Wagon::isLoaded).count();
    }

    public int getEmptyWagonCount() {
        return wagons.size() - getLoadedWagonCount();
    }

    public String getName() {
        return "Train " + id;
    }
}
