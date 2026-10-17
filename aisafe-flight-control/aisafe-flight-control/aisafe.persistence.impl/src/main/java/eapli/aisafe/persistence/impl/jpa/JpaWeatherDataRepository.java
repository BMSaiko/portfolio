package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;

import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JPA implementation of {@link WeatherDataRepository}.
 *
 * <p>
 * This repository persists and retrieves {@link WeatherData} aggregate roots
 * using the configured JPA persistence unit. It provides the generic operations
 * required by the repository contract and a weather-specific query for
 * consulting data by air control area and period start interval.
 * </p>
 *
 * <p>
 * The aggregate identity is the generated {@link Long} identifier stored in the
 * {@code id} field of {@link WeatherData}.
 * </p>
 */
public class JpaWeatherDataRepository extends JpaAutoTxRepository<WeatherData, Long, Long> implements WeatherDataRepository {

    /**
     * Creates a repository using the default AISafe persistence unit.
     */
    public JpaWeatherDataRepository() {
        super("aisafePU", "id");
    }

    /**
     * Creates a repository using a specific persistence unit.
     *
     * @param persistenceUnit name of the JPA persistence unit to use
     */
    public JpaWeatherDataRepository(String persistenceUnit) {
        super(persistenceUnit, "id");
    }

    /**
     * Persists or updates a weather data aggregate.
     *
     * @param data weather data aggregate to persist
     * @return the persisted weather data aggregate
     */
    @Override
    public WeatherData save(WeatherData data) {
        return super.save(data);
    }

    /**
     * Finds a weather data aggregate by its generated identifier.
     *
     * @param id generated weather data identifier
     * @return an optional containing the weather data if found, or empty
     * otherwise
     */
    @Override
    public Optional<WeatherData> findById(Long id) {
        return Optional.ofNullable(entityManager().find(WeatherData.class, id));
    }

    /**
     * Returns all weather data records stored in the system.
     *
     * @return iterable collection with all weather data records
     */
    @Override
    public Iterable<WeatherData> findAll() {
        TypedQuery<WeatherData> query = entityManager().createQuery("SELECT w FROM WeatherData w", WeatherData.class);
        return query.getResultList();
    }

    /**
     * Deletes a weather data aggregate.
     *
     * @param data weather data aggregate to delete
     */
    @Override
    public void delete(WeatherData data) {
        entityManager().remove(data);
    }

    /**
     * Finds weather data records for an air control area whose period starts
     * within the given interval.
     *
     * <p>
     * The lower bound {@code start} is inclusive and the upper bound
     * {@code end} is exclusive. Results are ordered by the weather period start
     * timestamp.
     * </p>
     *
     * @param area air control area to filter by
     * @param start inclusive start of the search interval
     * @param end exclusive end of the search interval
     * @return iterable collection of matching weather data records ordered by
     * period start
     */
    @Override
    public Iterable<WeatherData> findByAreaAndPeriodStartBetween(
            AirControlArea area,
            LocalDateTime start,
            LocalDateTime end) {

        TypedQuery<WeatherData> query = entityManager().createQuery(
                "SELECT w FROM WeatherData w " +
                        "WHERE w.area = :area " +
                        "AND w.period.start >= :start " +
                        "AND w.period.start < :end " +
                        "ORDER BY w.period.start",
                WeatherData.class);

        query.setParameter("area", area);
        query.setParameter("start", start);
        query.setParameter("end", end);

        return query.getResultList();
    }
}