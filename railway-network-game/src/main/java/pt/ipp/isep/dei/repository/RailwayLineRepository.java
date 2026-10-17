package pt.ipp.isep.dei.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.locomotive.RailwayLine;

public interface RailwayLineRepository {
    boolean saveRailwayLine(RailwayLine railwayLine);
    List<RailwayLine> getRailwayLinesByPlayer(Player player);
    Optional<Player> getRailwayLineById(String id);
//    private List<RailwayLine> railwayLines;
//
//    public  RailwayLineRepository() {
//        this.railwayLines = new ArrayList<>();
//    }

    public static List<RailwayLine> getAllRailwayLines() {
    
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static double getTotalKilometers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
