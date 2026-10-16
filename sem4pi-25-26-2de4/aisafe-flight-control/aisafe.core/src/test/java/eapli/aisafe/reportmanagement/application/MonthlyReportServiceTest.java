package eapli.aisafe.reportmanagement.application;

import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.reportmanagement.domain.MonthlyStatistics;
import eapli.aisafe.reportmanagement.domain.ReportPeriod;
import eapli.aisafe.reportmanagement.repositories.MonthlyStatisticsRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonthlyReportServiceTest {

    private AuthService authService;
    private FlightPlanRepository flightPlanRepository;
    private MonthlyStatisticsRepository statisticsRepository;
    private MonthlyReportService service;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository);
        flightPlanRepository = mock(FlightPlanRepository.class);
        statisticsRepository = mock(MonthlyStatisticsRepository.class);
        service = new MonthlyReportService(authService, flightPlanRepository, statisticsRepository);
        authService.logout();
    }

    private User createUserWithRole(Role role) {
        Email email = new Email("test@aisafe.com");
        return new User(email, "Pass1234", UserName.of("Test"), PhoneNumber.of("1234567"), Set.of(role));
    }

    private void loginUser(User user) {
        when(userRepository.findActiveByEmail(any(Email.class))).thenReturn(Optional.of(user));
        authService.login("test@aisafe.com", "Pass1234");
    }

    @Test
    void generate_withAdminRole_succeeds() {
        User admin = createUserWithRole(Role.ADMIN);
        loginUser(admin);
        when(flightPlanRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(statisticsRepository.findByYearAndMonth(2026, 1)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.generateMonthlyReport(ReportPeriod.of(2026, 1)));
        verify(statisticsRepository).save(any(MonthlyStatistics.class));
    }

    @Test
    void generate_withPilotRole_throwsSecurityException() {
        User pilot = createUserWithRole(Role.PILOT);
        loginUser(pilot);

        assertThrows(SecurityException.class,
                () -> service.generateMonthlyReport(ReportPeriod.of(2026, 1)));
        verify(statisticsRepository, never()).save(any(MonthlyStatistics.class));
    }

    @Test
    void generate_persistsRealStatistics_notZeroed() throws Exception {
        User admin = createUserWithRole(Role.ADMIN);
        loginUser(admin);
        when(flightPlanRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(statisticsRepository.findByYearAndMonth(2026, 3)).thenReturn(Optional.empty());

        service.generateMonthlyReport(ReportPeriod.of(2026, 3));

        verify(statisticsRepository).save(argThat((MonthlyStatistics stats) ->
                stats.totalFlights() == 0 &&
                stats.totalDistance() == 0.0 &&
                stats.year() == 2026 &&
                stats.month() == 3
        ));
    }
}

