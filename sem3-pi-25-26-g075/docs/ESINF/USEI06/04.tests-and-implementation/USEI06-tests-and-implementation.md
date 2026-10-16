# USEI06 - Station Search Operations

## 4. Tests and Implementation

### 4.1. Test Strategy

Testing focuses on:
- Valid input scenarios for each search operation
- Invalid input handling (null, empty, out-of-range values)
- Edge cases (no matches, multiple matches)

### 4.2. Unit Tests

**StationSearchServiceTest:**

**Search by Timezone Group Tests:**
- `testGetStationsByTimeZoneGroup_ValidGroup()`: Returns all stations in "CET" timezone
- `testGetStationsByTimeZoneGroup_CaseInsensitive()`: Handles "cet", "CET", "Cet"
- `testGetStationsByTimeZoneGroup_NoMatches()`: Returns empty list for non-existent group
- `testGetStationsByTimeZoneGroup_NullGroup()`: Throws IllegalArgumentException
- `testGetStationsByTimeZoneGroup_EmptyGroup()`: Throws IllegalArgumentException
- `testGetStationsByTimeZoneGroup_SortedByCountry()`: Verifies country ordering

**Search by Coordinates Tests:**
- `testGetStationsByCoordinates_ValidCoordinates()`: Finds stations at exact location
- `testGetStationsByCoordinates_MultipleStations()`: Handles multiple stations at same coordinates
- `testGetStationsByCoordinates_NoMatches()`: Returns empty list for unused coordinates
- `testGetStationsByCoordinates_InvalidLatitude()`: Throws IllegalArgumentException for lat > 90
- `testGetStationsByCoordinates_InvalidLongitude()`: Throws IllegalArgumentException for lon < -180
- `testGetStationsByCoordinates_SortedByName()`: Verifies alphabetical ordering

**Search by Country Tests:**
- `testGetStationsByCountry_ValidCountry()`: Returns all stations in "PT"
- `testGetStationsByCountry_CaseInsensitive()`: Handles "pt", "PT", "Pt"
- `testGetStationsByCountry_NoMatches()`: Returns empty list for non-existent country
- `testGetStationsByCountry_NullCountry()`: Throws IllegalArgumentException
- `testGetStationsByCountry_EmptyCountry()`: Throws IllegalArgumentException
- `testGetStationsByCountry_SortedByName()`: Verifies alphabetical ordering

### 4.3. Implementation Details

**Key Classes:**

**StationSearchService (service package):**
```java
public List<Station> getStationsByTimeZoneGroup(String timeZoneGroup)
public List<Station> getStationsByCoordinates(double latitude, double longitude)
public List<Station> getStationsByCountry(String country)
```

**StationsRepository (repository package):**
```java
public BST<TimeZone> getTimeZones()
public Map<TimeZone, List<Station>> getTimeZoneStationMap()
public List<Station> getAllStations()
```

**Station (domain/stations package):**
```java
public String getName()
public String getCountry()
public Coordinates getCoordinates()
public TimeZone getTimeZone()
public boolean isCity()
public boolean isMainStation()
public boolean isAirport()
```

**TimeZone (domain/stations package):**
```java
public String getCountry()
public String getZone()
public String getGroup()
```

**Coordinates (domain/stations package):**
```java
public double getLatitude()
public double getLongitude()
public boolean equals(Object o)
```

