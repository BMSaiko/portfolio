# USEI08 - 2D Range Query for Stations

## 4. Tests and Implementation

### 4.1. Test Strategy

Testing focuses on:
- KD-tree range query correctness with real dataset (64,000+ stations)
- Filter application (country, city, main station criteria)
- Coordinate validation (latitude/longitude bounds)
- Exception handling for invalid parameters inserted by the user

### 4.2. Unit Tests

**StationRangeSearchTest:**

Uses actual data from `train_stations_europe.csv` (64,039 European train stations) loaded via `DataInitializationService`.

**Range Search Tests with Real Dataset (9 tests):**

- `testRangeSearch_CentralEurope_MainStationsOnly()`: 
  - Range: lat 48-53, lon 5-10
  - Filter: main stations only
  - Expected: 101 stations
  - Verifies all results are main stations and within coordinate bounds

- `testRangeSearch_FranceRegion_NoFilter()`:
  - Range: lat 43-51, lon -5-8 (France region)
  - Filter: none
  - Verifies stations found and results are sorted by name

- `testRangeSearch_GermanyFranceBorder_CityStationsOnly()`:
  - Range: lat 48-50, lon 7-8 (Germany-France border)
  - Filter: city stations only
  - Verifies all results are city stations

- `testRangeSearch_NorthernFrance_CountryFilter()`:
  - Range: lat 48-52, lon 2-7
  - Filter: country = "FR"
  - Verifies all results are from France

- `testRangeSearch_BeneluxRegion_MainAndCityStations()`:
  - Range: lat 49-53, lon 3-7 (Benelux region)
  - Filter: main stations AND city stations
  - Verifies both filter criteria are met

- `testRangeSearch_SmallRegion_Strasbourg()`:
  - Range: lat 48.5-48.6, lon 7.7-7.8 (small region)
  - Verifies Strasbourg station found in tight coordinate bounds

- `testRangeSearch_EmptyRegion_AtlanticOcean()`:
  - Range: lat 30-35, lon -30--20 (Atlantic Ocean)
  - Expected: empty list
  - Verifies no false positives in empty regions

- `testRangeSearch_SinglePoint_BoundaryInclusive()`:
  - Range: Single point at Dijon coordinates (47.32337, 5.027208)
  - Verifies boundary inclusiveness (min/max are inclusive)

- `testRangeSearch_LargeRegion_AllEurope()`:
  - Range: lat 40-55, lon -10-20 (most of Europe)
  - Verifies large-scale search and alphabetical sorting

**Validation and Exception Tests (13 tests):**

- `testValidation_InvalidMinLatitudeBelowRange()`:
  - Input: latitude < -90
  - Expected: IllegalArgumentException with message mentioning "Min latitude"

- `testValidation_InvalidMaxLatitudeAboveRange()`:
  - Input: latitude > 90
  - Expected: IllegalArgumentException with message mentioning "Max latitude"

- `testValidation_InvalidMinLongitudeBelowRange()`:
  - Input: longitude < -180
  - Expected: IllegalArgumentException with message mentioning "Min longitude"

- `testValidation_InvalidMaxLongitudeAboveRange()`:
  - Input: longitude > 180
  - Expected: IllegalArgumentException with message mentioning "Max longitude"

- `testValidation_MinLatitudeGreaterThanMax()`:
  - Input: latMin > latMax
  - Expected: IllegalArgumentException about latitude range

- `testValidation_MinLongitudeGreaterThanMax()`:
  - Input: lonMin > lonMax
  - Expected: IllegalArgumentException about longitude range

- `testValidation_ValidEdgeCase_MinLatitude()`:
  - Input: latitude exactly -90
  - Expected: No exception (valid boundary)

- `testValidation_ValidEdgeCase_MaxLatitude()`:
  - Input: latitude exactly 90
  - Expected: No exception (valid boundary)

- `testValidation_ValidEdgeCase_MinLongitude()`:
  - Input: longitude exactly -180
  - Expected: No exception (valid boundary)

- `testValidation_ValidEdgeCase_MaxLongitude()`:
  - Input: longitude exactly 180
  - Expected: No exception (valid boundary)

- `testFilter_NullFilter_ReturnsAllStations()`:
  - Input: filter = null
  - Expected: Returns all stations in range without filtering

- `testFilter_EmptyFilter_ReturnsAllStations()`:
  - Input: empty StationFilter object
  - Expected: Returns all stations in range (no active filters)

- `testResults_AlwaysSortedByName()`:
  - Verifies all results are sorted alphabetically by station name
  - Checks large result set for proper ordering

### 4.3. Implementation Details

**Key Classes:**

**KDTree&lt;E extends KDTreeNodeInterface&gt; (utils package):**
```java
public class KDTree<E extends KDTreeNodeInterface> extends BST<E>
public void insert(E element)
public List<E> rangeSearch(double minX, double maxX, double minY, double maxY)
public List<E> rangeSearch(double minX, double maxX, double minY, double maxY, Predicate<E> filter)
```

**KDNode&lt;E&gt; (utils package):**
```java
public class KDNode<E extends KDTreeNodeInterface>
public E getElement()
public Point2D.Double getCoords()
public KDNode<E> getLeft()
public KDNode<E> getRight()
```

**KDTreeNodeInterface (interfaces package):**
```java
public interface KDTreeNodeInterface
Point2D.Double getCoords()
```

**Station (domain/stations package):**
```java
public class Station implements KDTreeNodeInterface, Comparable<Station>
public Point2D.Double getCoords()
public String getName()
public boolean isCity()
public boolean isMainStation()
public TimeZone getTimeZone()
```

**StationFilter (domain/stations package):**
```java
public class StationFilter
public StationFilter setIsCity(Boolean isCity)
public StationFilter setIsMainStation(Boolean isMainStation)
public StationFilter setCountry(String country)
public boolean hasActiveFilters()
public boolean matches(Station station)
```

**StationSearchService (service package):**
```java
public List<Station> getStationsByRange(
    double latMin, double latMax, 
    double lonMin, double lonMax, 
    StationFilter filter)
```
- Validates coordinate ranges
- Delegates to KDTree.rangeSearch()
- Applies optional filter using StationFilter.matches()
- Sorts results alphabetically by station name

**StationsRepository (repository package):**
```java
public KDTree<Station> getKdTree()
public void addStation(TimeZone tz, Station station)
```

