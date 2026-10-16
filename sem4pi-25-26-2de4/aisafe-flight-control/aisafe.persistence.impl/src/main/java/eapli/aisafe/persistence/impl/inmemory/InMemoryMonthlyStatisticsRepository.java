package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.reportmanagement.domain.MonthlyStatistics;
import eapli.aisafe.reportmanagement.repositories.MonthlyStatisticsRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of MonthlyStatisticsRepository for testing.
 */
public class InMemoryMonthlyStatisticsRepository implements MonthlyStatisticsRepository {

    private final Map<Long, MonthlyStatistics> store = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public Optional<MonthlyStatistics> findByYearAndMonth(final int year, final int month) {
        return store.values().stream()
                .filter(s -> s.year() == year && s.month() == month)
                .findFirst();
    }

    @Override
    public MonthlyStatistics save(final MonthlyStatistics statistics) {
        // For in-memory, we just store a copy.
        // In a real scenario we'd need a proper ID-based store; for simplicity
        // we use the statistics' identity if available.
        store.put(nextId++, statistics);
        return statistics;
    }

    @Override
    public long count() {
        return store.size();
    }
}
