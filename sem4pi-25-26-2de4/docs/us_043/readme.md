# US043 - Consult Weather Data

## 1. Context
This user story belongs to the weather management and operational consultation area.

The goal is to allow a Weather Person, Pilot, or Flight Control Operator to consult weather data stored in the system for a given day and a specific air control area.

The data may have been registered manually or imported in bulk through US042.

## 1.1 List of Issues
- **Analysis:** Understand which weather data should be shown, how date and air control area filters are applied, and which roles can access the information.
- **Design:** Define the consultation controller, repository query operation, UI flow, and result formatting.
- **Implement:** Implement weather data search by day and air control area, returning all matching records ordered by period start.
- **Test:** Validate successful consultation, no-results scenarios, invalid dates, unknown areas, and authorization.

## 2. Requirements
**US043** As a Weather Person, a Pilot, or a Flight Control Operator, I want to consult weather data in the system in a given day and in a specific air control area.

### Acceptance Criteria
- **US043.1:** A Weather Person can consult weather data.
- **US043.2:** A Pilot can consult weather data.
- **US043.3:** A Flight Control Operator can consult weather data.
- **US043.4:** Consultation must be filtered by day.
- **US043.5:** Consultation must be filtered by air control area.
- **US043.6:** The system should show all weather records for that area during the selected day.
- **US043.7:** The operation must be authenticated and authorized appropriately.

### Dependencies / References
- Depends on **US030** for authentication and authorization.
- Depends on **US041** and **US042** because weather data must exist before it can be consulted.
- Depends on air control area management because the query is scoped to a specific area.
- Supports **US082**, because pilots may consult weather data before inserting it into a flight plan.

## 3. Analysis

### 3.1 Understanding
Weather data consultation is a read-only operation. The user selects an air control area and a day. The system returns all weather records whose period starts during that day for the selected air control area.

The relevant data shown to the user includes:
- period start and end;
- air control area;
- temperature;
- wind speed;
- wind direction;
- humidity;
- pressure.

Because weather data may represent forecasts, future dates can be consulted if future weather records were imported.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Query by date range instead of exact date-time | A day contains multiple possible weather records. |
| Query by `TimePeriod.start` | The domain represents weather validity as a period, not as a single timestamp. |
| Use the existing `WeatherData` entity | Weather values and validation are centralized there. |
| Use air control area code as input | It is the business identifier the operator understands. |
| Return records ordered by period start | Makes the result easier to read and operationally useful. |
| Allow Weather Person, Pilot, Flight Control Operator and Admin | These are the actors listed in the requirement, with Admin retained for backoffice support. |

## 4. Design

### 4.1 Realization
The consultation is handled by `ConsultWeatherDataController`.

The UI collects:
- air control area;
- date.

The controller checks authorization, resolves the selected air control area, builds the date-time interval for the selected day, queries the weather repository and returns the matching data.

### 4.2 Main Flow
    User -> WeatherMenuUI: consult weather data
    WeatherMenuUI -> ConsultWeatherDataUI: show consultation form
    ConsultWeatherDataUI -> ConsultWeatherDataController: consult(areaCode, date)
    ConsultWeatherDataController -> AuthService: ensureHasAnyRole(WEATHER_PERSON, PILOT, FLIGHT_CONTROL_OPERATOR, ADMIN)
    ConsultWeatherDataController -> AirControlAreaRepository: findByCode(areaCode)
    ConsultWeatherDataController -> WeatherDataRepository: findByAreaAndPeriodStartBetween(area, startOfDay, startOfNextDay)
    WeatherDataRepository --> ConsultWeatherDataController: matching weather data
    ConsultWeatherDataController --> ConsultWeatherDataUI: weather data list
    ConsultWeatherDataUI --> User: display weather data table

### 4.3 Alternative Flows
**Air control area not found:**
    Repository returns empty Optional.
    Operation is rejected.
    User receives an error message.

**No weather data for selected day:**
    Repository returns an empty list.
    UI displays a no-data message.

**Invalid date:**
    UI/controller rejects the input.
    User receives an error message.

**Unauthorized user:**
    `AuthService` throws `SecurityException`.
    Operation is rejected.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core
- `eapli.aisafe.weather.domain.WeatherData`
- `eapli.aisafe.generic.domain.TimePeriod`
- `eapli.aisafe.weather.repositories.WeatherDataRepository`
- `eapli.aisafe.weather.application.ConsultWeatherDataController`
- `eapli.aisafe.aircontrolmanagement.domain.AirControlArea`
- `eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode`
- `eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository`

#### aisafe.persistence.impl
- `eapli.aisafe.persistence.impl.jpa.JpaWeatherDataRepository`
- `eapli.aisafe.persistence.impl.inmemory.InMemoryWeatherDataRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaAirControlAreaRepository`

#### aisafe.app.backoffice.console
- `eapli.aisafe.app.backoffice.console.presentation.weather.ConsultWeatherDataUI`
- `eapli.aisafe.app.backoffice.console.presentation.weather.WeatherMenuUI`
- `eapli.aisafe.app.backoffice.console.AISafeBackoffice`

### 5.2 Domain Rules
Weather data must have:
    valid air control area
    valid time period
    valid temperature
    valid wind speed
    valid wind direction
    valid humidity
    valid pressure

The consultation itself does not change domain state.

### 5.3 Repository Operations
The weather repository supports:
    findByAreaAndPeriodStartBetween(area, start, end)
    findAll()
    findById(id)

### 5.4 Persistence
Weather data is read from:
    T_WEATHER_DATA

The query filters by:
    AREA_CODE
    PERIOD_START between startOfDay and startOfNextDay

The results are ordered by:
    PERIOD_START

### 5.5 Proposed Package Structure
```
aisafe.core/
└── src/main/java/eapli/aisafe/weather/
    ├── application/
    │   └── ConsultWeatherDataController.java
    ├── domain/
    │   └── WeatherData.java
    └── repositories/
        └── WeatherDataRepository.java
```

## 6. Demonstration
Run the Backoffice application:
    mvn clean install
    cd aisafe.app.backoffice.console
    mvn exec:java

Login with one of the authorized roles:
    WEATHER_PERSON
    PILOT
    FLIGHT_CONTROL_OPERATOR

Menu path:
    Login
    Weather management
    Consult weather data

Example input after importing `CW_WeatherData_v0(Wind).csv`:
    Air Control Area: LPPC
    Date: 2026-06-22

Expected result:
    Weather data records for LPPC on 2026-06-22 are listed.

Example input after importing `aisafe_weather_complete.csv`:
    Air Control Area: LPPO
    Date: 2026-06-23

Expected result:
    All weather data records for LPPO on 2026-06-23 are listed.

## 7. Tests
The following aspects are tested:
- authorized Weather Person can consult weather data;
- authorized Pilot can consult weather data;
- authorized Flight Control Operator can consult weather data;
- unauthorized roles cannot consult;
- unknown air control area is rejected;
- invalid date is rejected;
- no data scenario returns an empty result message;
- returned data is filtered by air control area and day;
- future forecast records can be consulted when present.

## 8. Observations
This user story is read-only and should not modify weather data.

Future improvements:
- add filtering by hour range;
- export consultation results;
- support graph/table visualization for weather trends;
- show source/provider information if persisted in the domain model.
