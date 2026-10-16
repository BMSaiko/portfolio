package pt.ipp.isep.dei.domain.map;

import pt.ipp.isep.dei.domain.locomotive.Cargo;

import java.util.List;
import java.util.ArrayList;

public class Station {
    private int id;
    private String name;
    private Object location;

    private double storageCapacity;
    private List<Cargo> currentCargoes;

    public Station(int id, String name, Object location, double storageCapacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.storageCapacity = storageCapacity;
        this.currentCargoes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public Object getLocation() {
        return location;
    }

    public double getStorageCapacity() {
        return storageCapacity;
    }

    public List<Cargo> getCurrentCargoes() {
        return new ArrayList<>(currentCargoes);
    }

    public boolean addCargoes(List<Cargo> cargoes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void updateStorage(Cargo cargo) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean canStore(Cargo cargo) {
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public void setStorageCapacity(double storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public void setCurrentCargoes(List<Cargo> currentCargoes) {
        this.currentCargoes = currentCargoes;
    }

    
}

