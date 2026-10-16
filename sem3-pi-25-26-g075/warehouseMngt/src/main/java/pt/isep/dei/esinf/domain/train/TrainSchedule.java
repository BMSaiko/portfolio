package pt.isep.dei.esinf.domain.train;

import pt.isep.dei.esinf.domain.railway.SegmentPassage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainSchedule {
    private Train train;
    private List<SegmentPassage> passages;

    public TrainSchedule(Train train) {
        this.train = train;
        this.passages = new ArrayList<>();
    }

    public Train getTrain() { return train; }
    public List<SegmentPassage> getPassages() { return passages; }

    public void addPassage(SegmentPassage passage) {
        passages.add(passage);
    }

    public LocalDateTime getEstimatedArrival() {
        if (passages.isEmpty()) return null;
        return passages.get(passages.size() - 1).getDepartureTime();
    }


    /**
     * Gets the ordered list of facilities (stations) from the train's path.
     */
    public List<pt.isep.dei.esinf.domain.railway.Facility> getPathFacilities() {
        return train.getPath().getFacilities();
    }


    public long getTotalTravelTimeMinutes() {
        if (passages.isEmpty()) return 0;
        return java.time.Duration.between(
                train.getDepartureDateTime(),
                getEstimatedArrival()
        ).toMinutes();
    }
}
