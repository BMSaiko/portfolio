package pt.ipp.isep.dei.domain;

import java.util.List;

public class Path {
    private List<Object> points;
    private double length;

    public Path(List<Object> points, double length) {
        this.points = points;
        this.length = length;
    }

    public List<Object> getPoints() {
        return points;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setPoints(List<Object> points) {
        this.points = points;
    }
}
