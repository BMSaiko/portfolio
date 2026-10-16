package eapli.aisafe.reportmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.flightplanmanagement.domain.FlightLeg;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus;
import eapli.aisafe.flightplanmanagement.domain.FlightSegment;
import eapli.aisafe.flightplanmanagement.domain.Waypoint;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.Altitude;
import eapli.aisafe.generic.domain.Area;
import eapli.aisafe.generic.domain.Coefficient;
import eapli.aisafe.generic.domain.Speed;
import eapli.aisafe.generic.domain.TSFC;
import eapli.aisafe.generic.domain.Thrust;
import eapli.aisafe.generic.domain.Weight;
import eapli.aisafe.reportmanagement.domain.ReportPeriod;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MonthlyReportGeneratorTest {

    private FlightPlanRepository flightPlanRepository;
    private MonthlyReportGenerator generator;

    @Before
    public void setUp() {
        flightPlanRepository = mock(FlightPlanRepository.class);
        generator = new MonthlyReportGenerator(flightPlanRepository);
    }

    private AircraftModel dummyAircraftModel() {
        Set<EngineModel> engines = new HashSet<>();
        engines.add(new EngineModel(
                EngineModelId.of("CFM56-7B", "CFM"), EngineType.TURBOFAN,
                Thrust.of(121_000), Thrust.of(26_000), Speed.of(0.78),
                TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1));
        return new AircraftModel(
                AircraftModelId.of("BOEING-737", "Boeing"), AircraftType.PASSENGER,
                Weight.of(41_413), Weight.of(79_016), Weight.of(62_732), Weight.of(26_020),
                Altitude.of(41_000), Speed.of(0.785), Area.of(338),
                35.79, 9.45, Coefficient.of(0.85), Coefficient.of(0.021), Coefficient.of(0.5),
                2, 189, 5_600, engines);
    }

    private FlightPlan createFP(String id, String route, String from, String to,
                                 LocalDateTime dep, FlightPlanStatus status) {
        AircraftModel ac = dummyAircraftModel();
        List<FlightSegment> segments = new ArrayList<>();
        segments.add(FlightSegment.of("CRUISE", Waypoint.of(41.0, -8.0, 0), Waypoint.of(38.0, -9.0, 0)));
        List<FlightLeg> legs = new ArrayList<>();
        legs.add(new FlightLeg(from, to, 500.0, segments));
        FlightPlan fp = new FlightPlan(
                FlightPlanId.of(id),
                route, from, to, ac, legs, 500.0, 10000.0, dep);
        if (status == FlightPlanStatus.VALIDATED) {
            fp.validate();
        } else if (status == FlightPlanStatus.APPROVED) {
            fp.validate();
            fp.approve();
        } else if (status == FlightPlanStatus.COMPLETED || status == FlightPlanStatus.CANCELLED) {
            fp.validate();
            fp.approve();
        }
        return fp;
    }

    @Test
    public void generate_emptyRepo_reportWithZeroValues() throws IOException {
        when(flightPlanRepository.findAll()).thenReturn(Collections.emptyList());
        ReportPeriod period = ReportPeriod.of(2026, 1);
        Path reportPath = generator.generate(period);
        assertNotNull(reportPath);
        assertTrue(Files.exists(reportPath));
        String content = Files.readString(reportPath);
        assertTrue(content.contains("AISAFE - MONTHLY REPORT"));
        assertTrue(content.contains("January 2026"));
        assertTrue(content.contains("0 flights"));
        Files.deleteIfExists(reportPath);
    }

    @Test
    public void generate_withFlights_containsData() throws IOException {
        FlightPlan fp = createFP("FP001", "TP123", "OPO", "LIS",
                LocalDateTime.of(2026, 3, 15, 10, 0), FlightPlanStatus.VALIDATED);
        when(flightPlanRepository.findAll()).thenReturn(List.of(fp));
        ReportPeriod period = ReportPeriod.of(2026, 3);
        Path reportPath = generator.generate(period);
        String content = Files.readString(reportPath);
        assertTrue(content.contains("1 flights"));
        assertTrue(content.contains("OPO-LIS"));
        Files.deleteIfExists(reportPath);
    }

    @Test
    public void generate_flightOutsidePeriod_notIncluded() throws IOException {
        FlightPlan fp = createFP("FP002", "TP456", "LIS", "MAD",
                LocalDateTime.of(2026, 1, 10, 8, 0), FlightPlanStatus.COMPLETED);
        when(flightPlanRepository.findAll()).thenReturn(List.of(fp));
        ReportPeriod period = ReportPeriod.of(2026, 2);
        Path reportPath = generator.generate(period);
        String content = Files.readString(reportPath);
        assertTrue(content.contains("0 flights"));
        Files.deleteIfExists(reportPath);
    }

    @Test
    public void generate_reportHasCorrectBranding() throws IOException {
        when(flightPlanRepository.findAll()).thenReturn(Collections.emptyList());
        ReportPeriod period = ReportPeriod.of(2026, 6);
        Path reportPath = generator.generate(period);
        String content = Files.readString(reportPath);
        assertTrue(content.contains("AISAFE - MONTHLY REPORT"));
        assertTrue(content.contains("June 2026"));
        assertTrue(content.contains("END OF REPORT - AISAFE Flight Control"));
        Files.deleteIfExists(reportPath);
    }

    @Test
    public void generate_withSimulationReport_containsViolationCount() throws Exception {
        Path simDir = Path.of("reports/simulation");
        Files.createDirectories(simDir);
        Path fixture = Path.of("src/test/resources/simulation_report_test.txt");
        Path target = simDir.resolve("simulation_report_20260115.txt");
        Files.copy(fixture, target, StandardCopyOption.REPLACE_EXISTING);
        when(flightPlanRepository.findAll()).thenReturn(Collections.emptyList());
        ReportPeriod period = ReportPeriod.of(2026, 1);
        Path reportPath = generator.generate(period);
        String content = Files.readString(reportPath);
        assertTrue(content.contains("5 violations"));
        Files.deleteIfExists(target);
        Files.deleteIfExists(reportPath);
    }

    @Test
    public void generate_withoutSimulationFiles_noViolationsInReport() throws Exception {
        Path simDir = Path.of("reports/simulation");
        if (Files.isDirectory(simDir)) {
            String prefix = "simulation_report_202602";
            Files.list(simDir).filter(f -> f.getFileName().toString().startsWith(prefix))
                    .forEach(f -> { try { Files.deleteIfExists(f); } catch (Exception e) {} });
        }
        when(flightPlanRepository.findAll()).thenReturn(Collections.emptyList());
        ReportPeriod period = ReportPeriod.of(2026, 2);
        Path reportPath = generator.generate(period);
        String content = Files.readString(reportPath);
        assertTrue(content.contains("0 violations"));
        Files.deleteIfExists(reportPath);
    }
}
