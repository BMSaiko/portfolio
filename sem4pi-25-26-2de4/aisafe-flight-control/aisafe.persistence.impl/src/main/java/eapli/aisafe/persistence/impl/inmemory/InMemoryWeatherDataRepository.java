package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.repositories.WeatherDataRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.*;

/**
 * In-memory implementation of {@link WeatherDataRepository}.
 *
 * <p>
 * This repository stores {@link WeatherData} instances in a local
 * {@link Map}. It is mainly useful for automated tests and lightweight
 * execution scenarios where a database-backed repository is not required.
 * </p>
 *
 * <p>
 * The repository simulates generated identifiers with an internal incremental
 * counter. Since {@link WeatherData} identifiers are normally assigned by the
 * persistence provider, this implementation uses the entity identifier when it
 * already exists and otherwise stores the entity under the next internal ID.
 * </p>
 */
public class InMemoryWeatherDataRepository implements WeatherDataRepository {

    /**
     * Internal storage indexed by weather data identifier.
     */
    private final Map<Long, WeatherData> store = new HashMap<>();

    /**
     * Next internal identifier used when a weather data record has no persisted
     * identifier yet.
     */
    private long nextId = 1;

    /**
     * Saves a weather data aggregate in memory.
     *
     * <p>
     * If the aggregate already has an identifier, that identifier is used as
     * the storage key. Otherwise, an internal sequential identifier is used.
     * </p>
     *
     * @param entity weather data aggregate to save
     * @return the saved weather data aggregate
     */
    @Override
    public WeatherData save(WeatherData entity) {
        Long id = entity.id() != null ? entity.id() : nextId++;
        store.put(id, entity);
        return entity;
    }

    /**
     * Finds a weather data aggregate by identifier.
     *
     * @param id weather data identifier
     * @return optional containing the weather data if found, or empty otherwise
     */
    @Override
    public Optional<WeatherData> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Returns all weather data records currently stored in memory.
     *
     * @return iterable collection of all stored weather data records
     */
    @Override
    public Iterable<WeatherData> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Deletes a weather data aggregate from memory.
     *
     * <p>
     * If the aggregate has no identifier, no record is removed.
     * </p>
     *
     * @param entity weather data aggregate to delete
     */
    @Override
    public void delete(WeatherData entity) {
        if (entity.id() != null) store.remove(entity.id());
    }

    /**
     * Deletes a weather data aggregate by identifier.
     *
     * @param id weather data identifier
     */
    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    /**
     * Returns the number of stored weather data records.
     *
     * @return repository size
     */
    @Override
    public long count() {
        return store.size();
    }

    /**
     * Finds weather data records for an air control area whose period starts
     * inside the given interval.
     *
     * <p>
     * The lower bound {@code start} is inclusive and the upper bound
     * {@code end} is exclusive. Results are ordered by period start date-time.
     * </p>
     *
     * @param area air control area to filter by
     * @param start inclusive start of the search interval
     * @param end exclusive end of the search interval
     * @return iterable collection of matching weather data records
     */
    @Override
    public Iterable<WeatherData> findByAreaAndPeriodStartBetween(
            AirControlArea area,
            LocalDateTime start,
            LocalDateTime end) {

        return store.values().stream()
                .filter(w -> w.area().sameAs(area))
                .filter(w -> !w.period().start().isBefore(start))
                .filter(w -> w.period().start().isBefore(end))
                .sorted(Comparator.comparing(w -> w.period().start()))
                .collect(Collectors.toList());
    }

    /**
     * Framework-compatible alias for {@link #findById(Long)}.
     *
     * @param id weather data identifier
     * @return optional containing the weather data if found
     */
    public Optional<WeatherData> ofIdentity(Long id) {
        return findById(id);
    }

    /**
     * Framework-compatible alias for {@link #findAll()}.
     *
     * @return iterable collection of all stored weather data records
     */
    public Iterable<WeatherData> all() {
        return findAll();
    }
}