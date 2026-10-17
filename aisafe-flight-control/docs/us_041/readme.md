# US041 - Register Weather Data

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit (team size ≥ 3, see planning table).

The goal is to allow a Weather Person to register weather data into the system for a specific air control area. This weather data will later be used by flight simulations (US100, US110) to incorporate environmental influences.

This US is part of the weather service module, which also includes bulk import (US042) and consultation (US043). The current implementation focuses on single registration through the backoffice console.

### 1.1 List of Issues

- **Analysis:** Identify the attributes that constitute weather data (temperature, wind, humidity, pressure, etc.) and the relationship with air control areas.
- **Design:** Define the WeatherData aggregate, its value objects, repository, and controller. Enforce authorization (only WEATHER_PERSON and ADMIN can register).
- **Implement:** Create the domain class, JPA mapping, repository, controller, and console UI.
- **Test:** Write unit tests for domain invariants, controller authorization, and repository integration.

## 2. Requirements

**US041** As a Weather Person, I want to register weather data in the system for a specific air control area.

### Acceptance Criteria

- **US041.1:** The system must allow a Weather Person to register weather data for an existing air control area.
- **US041.2:** The weather data must include at least: date/time, temperature (ºC), wind speed (m/s), wind direction (degrees), humidity (%), atmospheric pressure (hPa).
- **US041.3:** The air control area must be selected from a list of existing areas.
- **US041.4:** The system must validate that date/time is not in the future (or accept only present/past – business rule to be defined).
- **US041.5:** All numeric values must be within reasonable physical ranges (e.g., temperature between -50 and 60, wind speed ≥0).
- **US041.6:** The user must be authenticated and have the role WEATHER_PERSON or ADMIN.

### Dependencies / References

- **US050** (Register Air Control Area) – needed because weather data is associated with an existing air control area.
- **US030** (Authentication and Authorization) – ensures only authorised users can register.
- Future **US100** (Simulate flights) will consume this data.

## 3. Analysis

### 3.1 Understanding

Weather data is a time‑stamped set of meteorological measurements valid for a specific air control area. The system will store historical weather records that can be queried later for simulation or reporting.

The air control area is identified by its unique code (areaCode). Each weather record belongs to exactly one area and has a timestamp (date and time). For simplicity, we store the timestamp with minute precision.

### 3.2 Key Decisions

| Decision | Rationale |
|----------|-----------|
| Weather data as an aggregate root | Each weather record has independent identity and is not part of the AirControlArea aggregate. |
| Use of LocalDateTime for timestamp | Better than Date (Java Time API). JPA supports it with @Convert or using java.time.LocalDateTime directly. |
| Value objects for measurements | Temperature, Wind, etc. can be encapsulated as value objects with validation. |
| Repository per aggregate | WeatherDataRepository provides CRUD and query by area/date range. |
| Authorization in controller | ensureHasAnyRole(WEATHER_PERSON, ADMIN). |

### 3.3 Domain Model Extract

**AirControlArea** (already exists)
- areaCode (String, unique)
- name (String)

**WeatherData**
- id (Long, auto-generated)
- airControlArea (reference to AirControlArea)
- timestamp (LocalDateTime)
- temperature (double, ºC)
- windSpeed (double, m/s)
- windDirection (double, degrees from north)
- humidity (double, %)
- pressure (double, hPa)

All measurement values are validated (e.g., temperature >= -50 and <= 60, windSpeed >= 0, windDirection 0-360, humidity 0-100, pressure >= 800 and <= 1100).

## 4. Design

### 4.1 Realization

The registration flow follows the layered architecture:

1. RegisterWeatherDataUI collects area code, date/time, and measurements.
2. It calls RegisterWeatherDataController.register(...).
3. The controller checks authorization, validates the input, retrieves the AirControlArea, creates the WeatherData aggregate, and saves it.
4. The repository persists the entity; the controller returns void (success or exception).

### 4.2 Sequence Diagram

The full sequence diagram is in [`sequence_diagram.puml`](sequence_diagram.puml).

Key interactions:

- **Weather Person** navigates through `MainMenu` → "Weather management" → `RegisterWeatherDataUI`
- **RegisterWeatherDataUI** fetches areas via `AirControlAreaRepository.findAll()`, displays them, and collects: timestamp, temperature, wind speed, wind direction, humidity, pressure
- **RegisterWeatherDataController.register()** is called, which:
  1. Calls `AuthService.ensureHasAnyRole(WEATHER_PERSON, ADMIN)` — throws `SecurityException` if unauthorized
  2. Calls `AirControlAreaRepository.findByCode(areaCode)` — throws `IllegalArgumentException` if area not found
  3. Creates `new WeatherData(area, timestamp, ...)` — validates all measurements in setters (temperature -50..60, windSpeed ≥ 0, windDirection 0..360, humidity 0..100, pressure 800..1100, timestamp not in future)
  4. Calls `WeatherDataRepository.save(weatherData)` — returns `void`
- **RegisterWeatherDataUI** shows "Weather data registered successfully." or the error message

### 4.3 Validation

- Constructors of value objects and the aggregate itself throw IllegalArgumentException if any measurement is out of range.
- The controller also checks that the timestamp is not in the future (business rule).
- Repository methods throw IllegalStateException on persistence errors.

## 5. Implementation

### 5.1 Key Classes

**aisafe.core**
- eapli.aisafe.weather.domain.AirControlArea (already exists)
- eapli.aisafe.weather.domain.WeatherData (new aggregate)
- eapli.aisafe.weather.repositories.WeatherDataRepository

**eapli.aisafe.weather.application**
- RegisterWeatherDataController

**aisafe.persistence.impl**
- eapli.aisafe.persistence.impl.jpa.JpaWeatherDataRepository

**aisafe.app.backoffice.console**
- eapli.aisafe.app.backoffice.console.presentation.weather.RegisterWeatherDataUI

Updated MainMenu to include "Register weather data" under authenticated user options (if role WEATHER_PERSON or ADMIN).

### 5.2 Code Snippets

**WeatherData.java**

```java
@Entity
@Table(name = "T_WEATHER_DATA")
public class WeatherData {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "AREA_CODE", nullable = false)
    private AirControlArea area;
    private LocalDateTime timestamp;
    private double temperature;
    private double windSpeed;
    private double windDirection;
    private double humidity;
    private double pressure;

    protected WeatherData() {}

    public WeatherData(AirControlArea area, LocalDateTime timestamp,
                       double temperature, double windSpeed, double windDirection,
                       double humidity, double pressure) {
        setArea(area);
        setTimestamp(timestamp);
        setTemperature(temperature);
        setWindSpeed(windSpeed);
        setWindDirection(windDirection);
        setHumidity(humidity);
        setPressure(pressure);
    }

    // validation in setters
    private void setTimestamp(LocalDateTime ts) {
        if (ts == null) throw new IllegalArgumentException("Timestamp required");
        if (ts.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Cannot register future weather data");
        this.timestamp = ts;
    }

    private void setTemperature(double t) {
        if (t < -50 || t > 60) throw new IllegalArgumentException("Temperature out of range -50..60");
        this.temperature = t;
    }
    // similarly for other fields...
}
```

**RegisterWeatherDataController.java**

```java
public class RegisterWeatherDataController {
    private final AuthService auth;
    private final AirControlAreaRepository areaRepo;
    private final WeatherDataRepository weatherRepo;

    public void register(String areaCode, LocalDateTime timestamp, double temp,
                         double windSpeed, double windDirection, double humidity, double pressure) {
        auth.ensureHasAnyRole(Role.WEATHER_PERSON, Role.ADMIN);
        AirControlArea area = areaRepo.findByAreaCode(areaCode)
            .orElseThrow(() -> new IllegalArgumentException("Air control area not found"));
        WeatherData data = new WeatherData(area, timestamp, temp, windSpeed, windDirection, humidity, pressure);
        weatherRepo.save(data);
    }
}
```

### 5.3 Repository and UI

JpaWeatherDataRepository extends JpaAutoTxRepository (or implements WeatherDataRepository using EntityManager). The UI presents a list of air control areas (fetched from AirControlAreaRepository) and asks for each measurement.

## 6. Demonstration

### 6.1 Bootstrap Air Control Areas

The bootstrap already creates at least one air control area (e.g., "LIS_AREA" for Lisbon). If not, add one in MasterUsersBootstrapper.

### 6.2 Running the Application

```bash
mvn clean compile exec:java -pl aisafe.app.backoffice.console
```

### 6.3 Steps to Register Weather Data

1. Login as weather@aisafe.com (Password Weather123) or admin@aisafe.com.
2. From the main menu choose "Weather management" (new option) → "Register weather data".
3. Select an air control area from the list.
4. Enter date and time (format yyyy-MM-dd HH:mm).
5. Enter temperature, wind speed, wind direction, humidity, pressure.
6. Confirm – system displays "Weather data registered successfully."

### 6.4 Example Output

```
Weather management
1. Register weather data
2. Consult weather data
0. Back

Option: 1
Select air control area:
1. Lisbon Area (LIS_AREA)
2. Porto Area (OPO_AREA)
Option: 1
Timestamp (yyyy-MM-dd HH:mm): 2026-04-27 12:00
Temperature (ºC): 18.5
Wind speed (m/s): 12.3
Wind direction (deg): 270
Humidity (%): 65
Pressure (hPa): 1013.2
Weather data registered successfully.
```

## 7. Tests

### 7.1 Unit Tests (Domain)

- WeatherDataTest: ensures constructor rejects out‑of‑range values.
- WeatherDataTest: ensures future timestamps are rejected.

### 7.2 Controller Tests

RegisterWeatherDataControllerTest: mocks repository and auth service.
- Success when area exists and user has WEATHER_PERSON role.
- Failure when area not found.
- Failure when user not authenticated.
- Failure when user lacks required role.

### 7.3 Repository Integration Tests

JpaWeatherDataRepositoryTest: save and find by area and date range using an in‑memory H2 database.

## 8. Observations

### 8.1 Future Improvements

- Support bulk import (US042) via CSV or JSON.
- Add remote access for Weather Person (US044) through a TCP client.
- Enhance querying for simulations (e.g., nearest weather data to a given time and area).

### 8.2 Integration with Other US

- US043 (Consult weather data) will reuse the same repository to list records.
- US100 (Simulate flights) will call a service to retrieve weather conditions for the area and time of the flight.

### 8.3 Critical Perspective

The current implementation stores point‑based measurements. For realistic simulation, interpolation between recorded times may be needed. This can be added in later sprints without changing the core registration flow.