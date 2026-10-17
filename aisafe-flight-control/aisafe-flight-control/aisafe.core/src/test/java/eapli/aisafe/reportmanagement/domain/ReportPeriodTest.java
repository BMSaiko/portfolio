package eapli.aisafe.reportmanagement.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReportPeriod Value Object.
 */
class ReportPeriodTest {

    @Test
    void of_validValues_createsInstance() {
        ReportPeriod period = ReportPeriod.of(2026, 4);
        assertEquals(2026, period.year());
        assertEquals(4, period.month());
    }

    @Test
    void of_monthBelow1_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReportPeriod.of(2026, 0));
    }

    @Test
    void of_monthAbove12_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReportPeriod.of(2026, 13));
    }

    @Test
    void of_yearBelow2000_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReportPeriod.of(1999, 6));
    }

    @Test
    void startDate_returnsFirstDayOfMonth() {
        ReportPeriod period = ReportPeriod.of(2026, 4);
        assertEquals(1, period.startDate().getDayOfMonth());
        assertEquals(4, period.startDate().getMonthValue());
        assertEquals(2026, period.startDate().getYear());
    }

    @Test
    void endDate_returnsLastDayOfMonth() {
        ReportPeriod period = ReportPeriod.of(2026, 2);
        assertEquals(28, period.endDate().getDayOfMonth());
    }

    @Test
    void toString_returnsFormattedString() {
        ReportPeriod period = ReportPeriod.of(2026, 4);
        assertEquals("2026-04", period.toString());
    }

    @Test
    void equals_sameValues_areEqual() {
        ReportPeriod p1 = ReportPeriod.of(2026, 4);
        ReportPeriod p2 = ReportPeriod.of(2026, 4);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void equals_differentValues_notEqual() {
        ReportPeriod p1 = ReportPeriod.of(2026, 4);
        ReportPeriod p2 = ReportPeriod.of(2026, 5);
        assertNotEquals(p1, p2);
    }
}
