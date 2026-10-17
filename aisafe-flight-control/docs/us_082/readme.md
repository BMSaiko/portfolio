# US082 - Insert Weather Data in a Flight

## 1. Context

This user story belongs to flight plan management and weather integration.

The goal is to allow a Pilot to add existing weather data to one of their own flight plans. Since weather data can affect the operational validity of a flight plan, adding a new weather record invalidates a previous validation/test result.

## 1.1 List of Issues

- **Analysis:** Understand the relation between flight plans, pilots and weather data, and how changing weather data affects previous validation.
- **Design:** Define ownership checks, repository operations, domain status changes and console flow.
- **Implement:** Associate weather data with the pilot's flight plan and invalidate previous validation when needed.
- **Test:** Validate successful insertion, authorization, ownership restrictions, missing data and status invalidation.

## 2. Requirements

**US082** As a Pilot, I want to add to flight plan of mine the weather data. If the flight plan has been previously tested, the test is deemed void because of the new weather data.

### Acceptance Criteria

- **US082.1:** A Pilot can add weather data to one of their own flight plans.
- **US082.2:** The selected flight plan must exist.
- **US082.3:** The selected flight plan must belong to the authenticated pilot.
- **US082.4:** The selected weather data must exist.
- **US082.5:** Weather data becomes associated with the flight plan.
- **US082.6:** If the flight plan had previous validation/test results, those results are invalidated.
- **US082.7:** The operation must be authenticated and authorized with the `PILOT` role.

### Dependencies / References

- Depends on **US030** for authentication and authorization.
- Depends on **US080** because a flight plan must exist and be assigned to a pilot.
- Depends on **US041/US042** because weather data must exist before being associated with a flight plan.
- Related to future/parallel flight plan testing or validation user stories.

## 3. Analysis

### 3.1 Understanding

Weather data is managed independently from flight plans. For US082, the pilot selects an existing weather data record and associates it with a flight plan they own.

The operation must guarantee:

- only authenticated pilots can execute it;
- only the owner pilot can update the selected flight plan;
- only existing weather data can be selected;
- duplicate weather associations are rejected;
- previous validation/test state is invalidated.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Associate existing `WeatherData` records with `FlightPlan` | Weather data is imported/registered separately and can be reused. |
| Store multiple weather records per flight plan | A flight may cross multiple areas or time intervals. |
| Restrict operation to the owner pilot | The user story states "flight plan of mine". |
| Invalidate in the domain object | Keeps the business invariant close to the `FlightPlan` aggregate. |
| Use `VALIDATED`/`APPROVED` as previous test states | No separate test-result aggregate exists yet, so these states represent prior validation. |

## 4. Design

### 4.1 Realization

The operation is handled by `InsertWeatherDataInFlightPlanController`.

The console UI collects:

- flight plan identifier;
- weather data identifier.

The controller:

1. Ensures the current user has the `PILOT` role.
2. Fetches the selected flight plan.
3. Verifies that the flight plan is assigned to the authenticated pilot.
4. Fetches the selected weather data.
5. Calls `FlightPlan.addWeatherData(weatherData)`.
6. Persists the updated flight plan.

The `FlightPlan` aggregate:

- rejects null weather data;
- rejects duplicate weather data;
- rejects changes to `ACTIVE`, `COMPLETED` or `CANCELLED` plans;
- changes `VALIDATED` or `APPROVED` plans back to `DRAFT`.

### 4.2 Main Flow

```text
Pilot -> MainMenu: Flight Plan Management
Pilot -> FlightPlanMenuUI: Insert Weather Data In Flight Plan
InsertWeatherDataInFlightPlanUI -> InsertWeatherDataInFlightPlanController: myFlightPlans()
InsertWeatherDataInFlightPlanUI -> InsertWeatherDataInFlightPlanController: availableWeatherData()
Pilot -> InsertWeatherDataInFlightPlanUI: flightPlanId, weatherDataId
InsertWeatherDataInFlightPlanController -> AuthService: ensureHasAnyRole(PILOT)
InsertWeatherDataInFlightPlanController -> FlightPlanRepository: findById(flightPlanId)
InsertWeatherDataInFlightPlanController -> WeatherDataRepository: findById(weatherDataId)
InsertWeatherDataInFlightPlanController -> FlightPlan: addWeatherData(weatherData)
FlightPlan -> FlightPlan: invalidate previous validation if needed
InsertWeatherDataInFlightPlanController -> FlightPlanRepository: save(flightPlan)
InsertWeatherDataInFlightPlanUI -> Pilot: success message
```

### 4.3 Alternative Flows

**Flight plan not found**

The repository returns an empty `Optional`, the operation is rejected and the user receives an error message.

**Flight plan belongs to another pilot**

The controller detects an ownership mismatch and throws a `SecurityException`.

**Weather data not found**

The weather repository returns an empty `Optional`, the operation is rejected and the user receives an error message.

**Duplicate weather data**

The domain object rejects the duplicate association.

**Flight plan was previously validated/approved**

The weather data is associated and the flight plan status returns to `DRAFT`.

**Unauthenticated or unauthorized user**

`AuthService` rejects the operation.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.flightplanmanagement.domain.FlightPlan`
- `eapli.aisafe.flightplanmanagement.application.InsertWeatherDataInFlightPlanController`
- `eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository`
- `eapli.aisafe.weather.domain.WeatherData`
- `eapli.aisafe.weather.repositories.WeatherDataRepository`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement.InsertWeatherDataInFlightPlanUI`
- `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement.FlightPlanMenuUI`
- `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement.ShowFlightPlanDetailsUI`
- `eapli.aisafe.app.backoffice.console.presentation.MainMenu`
- `eapli.aisafe.app.backoffice.console.AISafeBackoffice`

### 5.2 Domain Rules

The `FlightPlan` entity validates:

- weather data cannot be null;
- duplicate weather records cannot be associated with the same flight plan;
- weather data can only be added before flight execution;
- adding weather data to a `VALIDATED` or `APPROVED` flight plan returns it to `DRAFT`.

### 5.3 Repository Operations

The flight plan repository supports:

- `save(flightPlan)`
- `findById(flightPlanId)`
- `findAll()`
- `findByPilot(pilot)`

The weather data repository supports:

- `findById(weatherDataId)`
- `findAll()`
- `findByAreaAndPeriodStartBetween(area, start, end)`

### 5.4 Persistence

Weather data is associated with flight plans through a join table:

```text
FLIGHT_PLAN_WEATHER_DATA
```

This supports multiple weather records per flight plan.

When weather data is added, the flight plan status is updated in the `FLIGHT_PLAN` table through the normal repository `save`.

## 6. Demonstration

Run:

```text
mvn clean install
```

Start:

```text
eapli.aisafe.app.backoffice.console.AISafeBackoffice
```

Import weather data with a Weather Person:

```text
Login: weather@aisafe.com
Password: Weather123
Weather management
Import bulk weather data
CSV file path: weather-imports/us82_weather.csv
```

Insert weather data with a Pilot:

```text
Login: pilot@aisafe.com
Password: Pilot123
Flight Plan Management
Insert Weather Data In Flight Plan
Flight Plan ID: <one of the listed flight plan IDs>
Weather Data ID: <one of the listed weather data IDs>
```

Expected result:

```text
Weather data added successfully.
Flight plan status: DRAFT
Associated weather records: 1
```

## 7. Tests

The following aspects are tested:

- pilot can insert weather data into their own flight plan;
- the updated flight plan is persisted;
- only the authenticated pilot's plans are listed;
- available weather data is listed;
- unauthenticated users cannot execute the operation;
- users without `PILOT` role cannot execute the operation;
- a pilot cannot change another pilot's flight plan;
- null/blank flight plan identifiers are rejected;
- null weather data identifiers are rejected;
- unknown flight plans are rejected;
- unknown weather data is rejected;
- duplicate weather data is rejected;
- flight plans without pilot ownership are rejected;
- `VALIDATED` and `APPROVED` plans return to `DRAFT` after adding weather data.

### Test Classes

- `eapli.aisafe.flightplanmanagement.domain.FlightPlanWeatherDataTest`
- `eapli.aisafe.flightplanmanagement.application.InsertWeatherDataInFlightPlanControllerTest`

The tests use in-memory repositories and real domain objects for fixtures.

## 8. Observations

This user story connects flight plan management with weather management.

Future improvements:

- suggest weather data based on route areas and departure time;
- allow filtering weather data by area/date before insertion;
- replace the current status-based invalidation with a dedicated test-result aggregate when that feature exists.
