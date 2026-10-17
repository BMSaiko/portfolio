package pt.ipp.isep.dei.domain;


import java.util.List;

import pt.ipp.isep.dei.domain.map.Station;





public class Route {
    private int id;
    private String name;
    private List<Station> station;
    

    public Route(int id, String name, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.station = stations;
    }

    public List<Station> getStation() {
        return station;
    }
}