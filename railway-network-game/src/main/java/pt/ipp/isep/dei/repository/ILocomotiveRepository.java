package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;

import java.util.Date;
import java.util.List;

public interface ILocomotiveRepository {
    List<Locomotive> getAvailableLocomotives(Date currentDate);
    Locomotive getLocomotiveById(String locomotiveId);
    List<Locomotive> getAllLocomotives();
    List<Locomotive> getLocomotivesByType(String type);
    List<Locomotive> getLocomotivesByPowerRange(double minPower, double maxPower);
}
