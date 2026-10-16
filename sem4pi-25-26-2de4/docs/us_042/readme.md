# US042 - Import Bulk Weather Data

## 1. Context
This user story belongs to the weather management area.

The goal is to allow a Weather Person to import weather data in bulk into the system. Weather data may come from multiple external providers, so the import mechanism must not be tied to a single CSV structure.

The implementation supports:
- a complete AISafe CSV format, used by the application as the initial internal/simple format;
- a provider-specific CW wind CSV format, based on the supplied `CW_WeatherData_v0(Wind).csv` file.

## 1.1 List of Issues
- **Analysis:** Understand the required weather fields, the available provider data, validation rules, and how imported records relate to air control areas.
- **Design:** Define an import controller, importer abstraction, simple CSV importer, provider-specific importer, result summary, and UI flow.
- **Implement:** Implement bulk import with validation, support multiple records, and persist valid weather data entries.
- **Test:** Validate successful imports, invalid files, malformed lines, unknown air control areas, invalid weather values, forecast dates, and authorization.

## 2. Requirements
**US042** As a Weather Person, I want to import bulk weather data into the system.

Weather data might be from multiple external weather service providers, so the system can aggregate data from various sources for better accuracy. While a simple CSV file format will be available for initial system development, the system should be easy to expand to new weather data sources.

### Acceptance Criteria
- **US042.1:** A Weather Person can import weather data from a CSV file.
- **US042.2:** Imported data must be validated before being persisted.
- **US042.3:** The import must support multiple weather records in a single file.
- **US042.4:** Each record must reference an existing air control area.
- **US042.5:** Invalid records must be reported clearly.
- **US042.6:** The design must allow adding new data providers or formats later.
- **US042.7:** The operation must be authenticated and authorized appropriately.
- **US042.8:** Forecast weather data, including future periods, is accepted because flight planning may require future weather conditions.

### Dependencies / References
- Depends on **US030** for authentication and authorization.
- Depends on **US041** for the weather data domain model and single weather data registration.
- Depends on air control area management so weather data can be associated with a valid area.
- Supports **US043**, because imported data can later be consulted.
- Supports **US082**, because flight plans can use weather data already available in the system.

## 3. Analysis

### 3.1 Understanding
Bulk weather import is a batch operation. Instead of registering one weather entry manually, the Weather Person provides a file containing multiple weather records.

The complete AISafe CSV format contains:
- air control area code;
- start date/time;
- end date/time;
- temperature;
- wind speed;
- wind direction;
- humidity;
- pressure;
- provider/source identifier.

The supplied CW provider wind CSV contains wind-specific data:
- ACA identifier;
- geographic limits;
- altitude range;
- wind direction;
- wind speed in knots;
- day;
- start time;
- end time.

Because the CW wind file does not provide temperature, humidity, or pressure, the provider-specific importer uses operational default values for those fields:
- temperature: `15.0 C`;
- humidity: `50.0 %`;
- pressure: `1013.0 hPa`.

This keeps the domain model consistent while demonstrating support for provider-specific formats.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Use a `WeatherDataImporter` interface | Allows new weather providers/formats without changing the import controller. |
| Provide `CsvWeatherDataImporter` | Supports the complete internal AISafe CSV format. |
| Provide `CwWindWeatherDataImporter` | Supports the supplied CW provider wind CSV format. |
| Provide `AutoDetectWeatherDataImporter` | Selects the correct importer based on the CSV header. |
| Reuse `WeatherData` domain validation | Keeps weather rules centralized in the domain model. |
| Validate air control area existence before creating data | Prevents orphan weather data. |
| Allow forecast/future periods | Weather data may be used for future flight planning. |
| Return an import summary | The operator should know how many rows were imported and which failed. |
| Allow only `WEATHER_PERSON` and `ADMIN` | Only authorized users should import official weather data. |

## 4. Design

### 4.1 Realization
The import is handled by `ImportWeatherDataController`.

The UI collects:
- path to the CSV file.

The controller checks authorization, delegates parsing to a `WeatherDataImporter`, validates each parsed record, resolves the air control area through the repository, creates `WeatherData` objects and persists the valid entries.

Importer selection is performed by `AutoDetectWeatherDataImporter`:
- `AREA_CODE,...` header uses `CsvWeatherDataImporter`;
- `ACA,Lat.,...` header uses `CwWindWeatherDataImporter`.

### 4.2 Main Flow
    Weather Person -> WeatherMenuUI: import bulk weather data
    WeatherMenuUI -> ImportWeatherDataUI: show import form
    ImportWeatherDataUI -> ImportWeatherDataController: importFrom(filePath)
    ImportWeatherDataController -> AuthService: ensureHasAnyRole(WEATHER_PERSON, ADMIN)
    ImportWeatherDataController -> AutoDetectWeatherDataImporter: parse(filePath)
    AutoDetectWeatherDataImporter -> CsvWeatherDataImporter/CwWindWeatherDataImporter: parse(filePath)
    CsvWeatherDataImporter/CwWindWeatherDataImporter --> ImportWeatherDataController: parsed records
    ImportWeatherDataController -> AirControlAreaRepository: findByCode(areaCode)
    ImportWeatherDataController -> WeatherData: new WeatherData(area, period, temperature, windSpeed, windDirection, humidity, pressure)
    WeatherData -> WeatherData: validate weather values
    ImportWeatherDataController -> WeatherDataRepository: save(weatherData)
    ImportWeatherDataController --> ImportWeatherDataUI: import summary
    ImportWeatherDataUI --> Weather Person: success and errors summary

### 4.3 Alternative Flows
**File not found:**
    Importer cannot open the file.
    Operation is rejected.
    User receives an error message.

**Unsupported CSV format:**
    Auto-detection cannot match the file header.
    Operation is rejected.
    User receives an unsupported format message.

**Malformed CSV line:**
    Parser marks the line as invalid.
    Controller reports the line in the import summary.
    Valid lines may still be imported.

**Unknown air control area:**
    Repository returns empty Optional.
    Record is rejected.
    Error is added to the summary.

**Invalid weather values:**
    `WeatherData` constructor or value objects throw `IllegalArgumentException`.
    Record is rejected.
    Error is added to the summary.

**Unauthorized user:**
    `AuthService` throws `SecurityException`.
    Operation is rejected.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core
- `eapli.aisafe.weather.domain.WeatherData`
- `eapli.aisafe.generic.domain.TimePeriod`
- `eapli.aisafe.weather.repositories.WeatherDataRepository`
- `eapli.aisafe.weather.application.ImportWeatherDataController`
- `eapli.aisafe.weather.application.WeatherDataImporter`
- `eapli.aisafe.weather.application.CsvWeatherDataImporter`
- `eapli.aisafe.weather.application.CwWindWeatherDataImporter`
- `eapli.aisafe.weather.application.AutoDetectWeatherDataImporter`
- `eapli.aisafe.weather.application.WeatherImportRecord`
- `eapli.aisafe.weather.application.WeatherImportResult`

#### aisafe.persistence.impl
- `eapli.aisafe.persistence.impl.jpa.JpaWeatherDataRepository`
- `eapli.aisafe.persistence.impl.inmemory.InMemoryWeatherDataRepository`

#### aisafe.app.backoffice.console
- `eapli.aisafe.app.backoffice.console.presentation.weather.ImportWeatherDataUI`
- `eapli.aisafe.app.backoffice.console.presentation.weather.WeatherMenuUI`
- `eapli.aisafe.app.backoffice.console.AISafeBackoffice`

### 5.2 Domain Rules
The `WeatherData` entity validates:
    area cannot be null
    period cannot be null
    period start cannot be null
    period end cannot be null
    period end must be after period start
    temperature must be within the accepted operational range
    wind speed cannot be negative
    wind direction must be between 0 and 360 degrees
    humidity must be between 0 and 100
    pressure must be within a realistic range

Forecast periods are allowed, so future weather data can be imported.

### 5.3 Repository Operations
The repository supports:
    save(weatherData)
    findById(id)
    findAll()
    delete(weatherData)
    findByAreaAndPeriodStartBetween(area, start, end)

### 5.4 Persistence
The entity is persisted in table:
    T_WEATHER_DATA

Important fields:
    AREA_CODE not null
    PERIOD_START not null
    PERIOD_END not null
    TEMPERATURE not null
    WIND_SPEED not null
    WIND_DIRECTION not null
    HUMIDITY not null
    PRESSURE not null

The embedded `TimePeriod` uses explicit column names (`period_start`, `period_end`) to avoid reserved SQL words.

### 5.5 Proposed Package Structure
```
aisafe.core/
└── src/main/java/eapli/aisafe/weather/
    ├── application/
    │   ├── ImportWeatherDataController.java
    │   ├── WeatherDataImporter.java
    │   ├── CsvWeatherDataImporter.java
    │   ├── CwWindWeatherDataImporter.java
    │   ├── AutoDetectWeatherDataImporter.java
    │   ├── WeatherImportRecord.java
    │   └── WeatherImportResult.java
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

Login with:
    weather@aisafe.com
    Weather123

Menu path:
    Login
    Weather management
    Import bulk weather data

### Complete AISafe CSV example
    AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE,PROVIDER
    LPPC,2026-06-22 05:00,2026-06-22 08:15,17.9,14.8,90,65,1013,AISAFE

Expected result:
    Import finished.
    Imported records: 1
    Rejected records: 0

### CW provider wind CSV example
    ACA,Lat.,Long.,Lat.,Long.,Alt_inf (ft),Alt_Sup (ft),Direction (degree),Value (knot),Day,Star,End
    121,43.840454,-9.795711,40.225,-7.9501,0,1000,90,28.75,6/22/2026,5:00,8:15

Expected result:
    Import finished.
    Imported records: 1
    Rejected records: 0

The CW importer maps ACA `121` to air control area `LPPC` and converts wind speed from knots to meters per second.

## 7. Tests
The following aspects are tested:
- valid complete CSV import with multiple records;
- provider-specific CW wind CSV import;
- file not found is rejected;
- unsupported CSV format is rejected;
- malformed lines are reported;
- unknown air control area is rejected;
- invalid weather values are rejected;
- forecast/future weather periods are accepted;
- authenticated Weather Person can import;
- unauthorized users cannot import;
- parser abstraction allows adding future providers.

## 8. Observations
The implementation supports the initial complete CSV format and one provider-specific format. Future improvements:
- support JSON import;
- support direct provider API integration;
- support additional ACA-to-area mappings;
- avoid duplicate imports by area, period, provider, and source row;
- provide a detailed import audit report.
