package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;

import java.util.Date;
import java.util.List;

public class LocomotiveRepository implements ILocomotiveRepository {
    @Override
    public List<Locomotive> getAvailableLocomotives(Date currentDate) {
        return null;
    }

    @Override
    public Locomotive getLocomotiveById(String locomotiveId) {
        return null;
    }

    @Override
    public List<Locomotive> getAllLocomotives() {
        return null;
    }

    @Override
    public List<Locomotive> getLocomotivesByType(String type) {
        return null;
    }

    @Override
    public List<Locomotive> getLocomotivesByPowerRange(double minPower, double maxPower) {
        return null;
    }

    private List<Locomotive> filterByAvailabilityDate(List<Locomotive> locomotives, Date currentDate) {
        return null;
    }
}
