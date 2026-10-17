package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.scenario.Scenario;

import java.util.Optional;

public interface ScenarioRepository {
    boolean saveScenario(Scenario scenario);
    Optional<Scenario> getScenarioByName(String name);
}
