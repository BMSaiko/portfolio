package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.Train;

import java.util.List;

public interface IRailwayNetworkRepository {
    boolean addTrainToNetwork(Train train);
    boolean removeTrainFromNetwork(String trainId);
    List<Train> getNetworkTrains();
    boolean isTrainInNetwork(String trainId);
}
