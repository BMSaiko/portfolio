package pt.isep.sem4pi.lprog.model;

import java.util.ArrayList;
import java.util.List;

public class FlightPlan {
    private String id;
    private String type;
    private int line;
    private int column;
    private List<FlightLeg> legs = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }

    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }

    public List<FlightLeg> getLegs() { return legs; }
    public void addLeg(FlightLeg leg) { this.legs.add(leg); }
}