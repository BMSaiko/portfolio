# US112 - Monthly Report Generation

## 1. Context

Sprint 3 requirement for EAPLI (Section 6.4, Project Requirements V3b).
The Flight Control Operator needs monthly statistics reports.
Foundational for future report types (compliance, incident, etc.)
with consistent branding across all report types.

### 1.1 List of Issues

**Analysis:** Report structure, data sources, aggregation, output format.
**Design:** Template Method pattern, monthly aggregation.
**Implement:** MonthlyReportGenerator, MonthlyReportService, console UI.
**Test:** Report accuracy, aggregation, edge cases.

---

## 2. Requirements

**US112** As a Flight Control Operator, I want to generate a monthly
statistics report, foundational for all future report types.

### Acceptance Criteria

- **US112.1:** Generates monthly report from aggregated data.
- **US112.2:** Consistent structure extensible by other report types.
- **US112.3:** Includes aggregated monthly data (flights, violations, aircraft).
- **US112.4:** Works for any past month with data.

### Dependencies

- **US030** (Auth), **NFR02** (Documentation standards)
- **US070-US073** (Aircraft & Routes bootstrap)
- **US075** (Pilot), **US080** (Flight plan CRUD), **US085** (Validate)
- **US100-US103** (SCOMP simulation infrastructure)
- **US109** (Simulation report generation -- violation data source)
- **FlightPlanRepository** (flight data by month)
- **AircraftModelRepository** (fleet data)

---

## 3. Analysis

### 3.1 Understanding

US112 establishes a report generation framework aggregating flight plan
data by month. The abstract ReportGenerator base enables extensibility
for future types through the Template Method pattern.

FlightPlanBootstrap (reused from US080/US085) creates 9 sample flight plans
(FP001-FP009) across January/February/March 2026 for testing.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Abstract ReportGenerator base | Shared header/footer, file output, branding |
| Template Method pattern | Subclasses implement collectData() + buildSections() |
| Text output with consistent branding | Readable anywhere, no dependencies |
| Auth-restricted | ADMIN / FLIGHT_CONTROL_OPERATOR only |
| MonthlyStatistics entity (JPA) | Caches aggregated metrics (except violationCount) |
| ReportPeriod Value Object | Immutable, validates year >= 2000, month 1-12 |
| Column naming: year_value, month_value | Avoids H2 reserved words YEAR/MONTH |
| Manual JPA (not EAPLI) | MonthlyStatistics lacks DomainEntity; custom EntityManager |
| Simulation file scanning | scanSimulationViolations() reads reports/simulation/ each time |
| Stale cache | persistStatistics() skips if already present; never refreshes |

### 3.3 Report Structure

```
========================================
  AISAFE - MONTHLY REPORT
  Period: January 2026 | Generated: 2026-06-07 23:03
========================================
1. SUMMARY: 3 flights | 3 validated | 0 completed | 0 cancelled | 100,0% pass rate
----------------------------------------
2. FLEET: 1 active aircraft types | Most used: A320-200 (Airbus) (3) | Total distance: 1500,0 km
----------------------------------------
3. SAFETY: 500,0 km avg distance | 10000,0 kg avg fuel | 0 cancellations | 0 violations
----------------------------------------
4. TOP ROUTES: OPO-LIS(2) LIS-MAD(1)
----------------------------------------

========================================
  END OF REPORT - AISAFE Flight Control
========================================
```

### 3.4 Data Flow

```
User (ADMIN/FCO)
    |
    v
GenerateMonthlyReportUI
    |  reads year, month from console
    v
MonthlyReportService.generateMonthlyReport(period)
    |  auth.ensureHasAnyRole(ADMIN, FLIGHT_CONTROL_OPERATOR)
    |  reportGenerator.generate(period)
    v
MonthlyReportGenerator (Template Method)
    |-- collectData(period)
    |     |-- flightPlanRepository.findAll()
    |     |-- filter by departureDateTime in [start, end]
    |     |-- aggregate: totalFlights, validated, completed, cancelled
    |     |-- aggregate: totalDistance, totalFuel
    |     |-- aggregate: routeCounts, aircraftCounts
    |     |-- scanSimulationViolations(period)
    |           |-- scan reports/simulation/ for YYYYMM prefix
    |           |-- parse violation counts from FLIGHT DETAILS
    |-- buildSections(period)
    |     |-- buildSummarySection()
    |     |-- buildFleetSection()
    |     |-- buildSafetySection()
    |     |-- buildRoutesSection()
    |-- writeToFile(period, sections)
    |     |-- buildHeader(period)
    |     |-- sections numbered 1..N
    |     |-- buildFooter()
    |     |-- Files.write(reports/monthly/AISAFE_MonthlyReport_YYYY_MM.txt)
    v
MonthlyReportService.persistStatistics(period)
    |-- if findByYearAndMonth() == empty
    |     create MonthlyStatistics from generator getters
    |     statisticsRepository.save(stats)
    v
Path to generated report file
```

### 3.5 Consistent Branding

All report types share a unified branding scheme defined in the abstract
ReportGenerator base class:

**Header convention:**
- Top border: 40 = characters
- Title line: AISAFE - <REPORT_TYPE>
- Subtitle line: Period: <Month> <Year> | Generated: <timestamp>
- Bottom border: 40 = characters

**Footer convention:**
- END OF REPORT - AISAFE Flight Control followed by = border

**Section separator:** 40 - characters between sections

**Report naming:** AISAFE_<ReportType>_<YYYY>_<MM>.txt
**Output directory:** reports/monthly/ (hardcoded)

### 3.6 Data Persistence

Dual-layer: MonthlyStatistics (H2 cache) + simulation files (raw audit trail).
Generator aggregates flight plans from DB AND scans reports/simulation/ for violations.
persistStatistics() caches flight metrics after generation.

Limitation: violationCount is NOT cached; re-scanned each time.
Limitation: cached stats never refreshed (stale if plans modified).

---

## 4. Design

### 4.1 Class Hierarchy

```
ReportGenerator (abstract)
+-- MonthlyReportGenerator
|   +-- collectData(ReportPeriod)
|   +-- buildSections(ReportPeriod) -> List<String>
+-- ComplianceReportGenerator (future)
+-- IncidentReportGenerator (future)
```

### 4.2 Key Classes

**aisafe.core (domain + application):**
- reportmanagement/domain/ReportPeriod.java -- VO (year, month)
- reportmanagement/domain/ReportType.java -- Enum
- reportmanagement/domain/MonthlyStatistics.java -- JPA entity
- reportmanagement/repositories/MonthlyStatisticsRepository.java -- Interface
- reportmanagement/application/ReportGenerator.java -- Abstract base
- reportmanagement/application/MonthlyReportGenerator.java -- 4 sections
- reportmanagement/application/MonthlyReportService.java -- Auth + orchestration

**aisafe.persistence.impl:**
- jpa/JpaMonthlyStatisticsRepository.java -- JPA implementation
- inmemory/InMemoryMonthlyStatisticsRepository.java -- For testing

**aisafe.app.backoffice.console:**
- presentation/reportmanagement/ReportsMenuUI.java -- Reports submenu
- presentation/reportmanagement/GenerateMonthlyReportUI.java -- Input + display

**aisafe.bootstrap:**
- FlightPlanBootstrap.java -- 9 sample flight plans (FP001-FP009)

### 4.3 Authorization

```java
authService.ensureHasAnyRole(Role.ADMIN, Role.FLIGHT_CONTROL_OPERATOR);
```

---

## 5. Implementation

### 5.1 Files Created (13)

| File | Layer | Purpose |
|------|-------|---------|
| ReportPeriod.java | Domain | VO: year(), month(), startDate(), endDate() |
| ReportType.java | Domain | Enum: MONTHLY, COMPLIANCE, INCIDENT |
| MonthlyStatistics.java | Domain | JPA entity for cached aggregates |
| MonthlyStatisticsRepository.java | Domain | Repository interface |
| ReportGenerator.java | Application | Abstract Template Method base |
| MonthlyReportGenerator.java | Application | 4 sections: Summary, Fleet, Safety, Routes |
| MonthlyReportService.java | Application | Auth + orchestration + persist |
| JpaMonthlyStatisticsRepository.java | Persistence | JPQL implementation |
| InMemoryMonthlyStatisticsRepository.java | Persistence | HashMap-based for tests |
| ReportsMenuUI.java | UI | Reports submenu |
| GenerateMonthlyReportUI.java | UI | Input year/month, show output |
| ReportPeriodTest.java | Test | 8 tests (JUnit 5): validation, dates, equals/hashCode |
| MonthlyReportGeneratorTest.java | Test | 6 tests (JUnit 4): aggregation, branding, violations |
| MonthlyReportServiceTest.java | Test | 3 tests (JUnit 5): auth, persist |

### 5.2 Files Modified (3)

| File | Change |
|------|--------|
| persistence.xml | Added MonthlyStatistics class |
| MainMenu.java | Added Reports option for ADMIN + FCO |
| AISafeBackoffice.java | Wired MonthlyReportService + FlightPlanBootstrap |

### 5.3 Files Reused (not created by US112)

| File | Original US | Role in US112 |
|------|-------------|---------------|
| FlightPlanBootstrap.java | US080, US085 | 9 sample flight plans for report testing |

---

## 6. Demonstration

```bash
mvn clean install
mvn -pl aisafe.app.backoffice.console exec:java
# Login: fco@control.com / Fco123
# Main Menu > Reports > Generate Monthly Report
# Month: 1 | Year: 2026
# -> reports/monthly/AISAFE_MonthlyReport_2026_01.txt
```

### Verified Output (2026-06-07)

| Month | Flights | Routes | Aircraft | Distance |
|-------|---------|--------|----------|----------|
| January 2026 | 3 | OPO-LIS(2), LIS-MAD(1) | A320-200 (3) | 1500 km |
| February 2026 | 2 | OPO-LIS(1), LIS-MAD(1) | A320-200 (2) | 1000 km |
| March 2026 | 4 | OPO-LIS(2), LIS-OPO(1), LIS-MAD(1) | A320-200(4) | 2000 km |
| **Total** | **9** | | | **4500 km** |

---

## 7. Tests

| Test Class | Tests | JUnit | Coverage |
|---|---|---|---|
| ReportPeriodTest | 8 | JUnit 5 | of(), startDate/endDate, equals/hashCode/toString |
| MonthlyReportGeneratorTest | 6 | JUnit 4 | aggregation, branding, routes, violations, empty |
| MonthlyReportServiceTest | 3 | JUnit 5 | auth (admin OK, pilot denied), persist |

**Total: 17 tests, 0 failures, BUILD SUCCESS**

> **Note:** MonthlyReportGeneratorTest uses JUnit 4 while others use JUnit 5.

---

## 8. Observations

### 8.1 Acceptance Criteria Status

| ID | Criteria | Status |
|---|---|---|
| US112.1 | Generates monthly report from aggregated data | OK |
| US112.2 | Consistent structure extensible by other report types | OK |
| US112.3 | Includes aggregated monthly data (flights, violations, aircraft) | OK |
| US112.4 | Works for any past month with data | OK |

### 8.2 Current Limitations
- Text-only output, no graphics
- Monthly granularity only
- Output directory hardcoded to reports/monthly/ (not configurable)
- violationCount NOT persisted in MonthlyStatistics; re-scanned from sim files each time
- Cached statistics never refreshed (stale if flight plans modified after initial generation)
- JpaMonthlyStatisticsRepository uses manual EntityManager (not EAPLI JpaAutoTxRepository)
- ReportPeriod does not implement EAPLI ValueObject interface

### 8.3 Future Extensions
- HTML/PDF output with charts
- Automated scheduled generation
- Email distribution
- Add violationCount to MonthlyStatistics entity (currently re-scanned)
- Configurable output directory (via application property)
- Refactor JpaMonthlyStatisticsRepository to extend EAPLI JpaAutoTxRepository
- Implement ReportPeriod as EAPLI ValueObject
- Cache refresh strategy (update MonthlyStatistics on re-generation)
- Daily/weekly granularity
- Migrate MonthlyReportGeneratorTest from JUnit 4 to JUnit 5

---
*Documento US112 - Sprint 3 EAPLI - Junho 2026*
