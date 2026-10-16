package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.domain.map.City;

public interface IMapRepository {
    Map getCurrentMap();
    City findClosestCity(int x, int y);
    boolean validatePosition(int x, int y, String stationType);
}
