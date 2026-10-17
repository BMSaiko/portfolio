package pt.isep.dei.esinf.domain.warehouse;

import java.util.List;

public class Wagon {

    private String wagonId;
    private List<Box> boxes;


    public Wagon(String wagonId, List<Box> boxes) {
        this.wagonId = wagonId;
        this.boxes = boxes;
    }

    public String getWagonId() {
        return wagonId;
    }

    public void setWagonId(String wagonId) {
        this.wagonId = wagonId;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    @Override
    public String
    toString() {
        return "Wagon{" +
                "wagonId='" + wagonId + '\'' +
                ", boxes=" + boxes +
                '}';
    }
}
