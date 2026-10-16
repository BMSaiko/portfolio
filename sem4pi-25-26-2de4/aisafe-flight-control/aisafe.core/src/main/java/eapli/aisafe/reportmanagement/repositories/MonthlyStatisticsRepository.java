package eapli.aisafe.reportmanagement.repositories;

import eapli.aisafe.reportmanagement.domain.MonthlyStatistics;

import java.util.Optional;

/**
 * Repository interface for MonthlyStatistics entities.
 */
public interface MonthlyStatisticsRepository {

    /**
     * Finds statistics for a specific year and month.
     *
     * @param year  the year
     * @param month the month (1-12)
     * @return an Optional containing the statistics, or empty if not found
     */
    Optional<MonthlyStatistics> findByYearAndMonth(int year, int month);

    /**
     * Saves or updates the given statistics.
     *
     * @param statistics the statistics to save
     * @return the saved entity
     */
    MonthlyStatistics save(MonthlyStatistics statistics);

    /**
     * Returns the total number of statistics records.
     */
    long count();
}
