package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.Cargo;
import pt.ipp.isep.dei.domain.map.Station;

import java.util.List;
import java.util.Optional;

public interface CargoRepository {
    boolean saveCargo(Object cargo);
    boolean saveAllCargo(List<Cargo> cargoes);
    List<Cargo> getAllCargo();
    List<Station> getCargoByStation(Station station);
    Optional<Cargo> getCargoById(String id);
}
