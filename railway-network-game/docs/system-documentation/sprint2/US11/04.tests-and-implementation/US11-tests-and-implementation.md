# US11 - Tests and Implementation

## 1. Tests

### 1.1. Unit Tests

#### TrainService Tests
* Should return empty list when no trains exist
* Should group trains by locomotive type
* Should sort trains alphabetically within groups
* Should include cargo information for each train
* Should handle trains with no cargo
* Should include route information when available

#### TrainController Tests
* Should return success status when listing trains
* Should handle empty train list gracefully
* Should return properly formatted response

#### TrainRepository Tests
* Should retrieve all trains from database
* Should handle database connection errors
* Should return trains with all required attributes

### 1.2. Integration Tests

* Should retrieve and display trains from database
* Should properly group and sort trains
* Should include all required train information
* Should handle large numbers of trains efficiently

### 1.3. Acceptance Tests

**Scenario 1: List trains when none exist**
```gherkin
Given there are no trains in the system
When the player requests to list all trains
Then an empty list should be displayed
And a message indicating no trains exist should be shown
```

**Scenario 2: List multiple trains of different types**
```gherkin
Given there are multiple trains of different locomotive types
When the player requests to list all trains
Then trains should be grouped by locomotive type
And within each group, trains should be sorted alphabetically
And each train should show its cargo and route information
```

**Scenario 3: List trains with and without cargo**
```gherkin
Given there are trains with and without cargo
When the player requests to list all trains
Then trains with cargo should show their cargo details
And trains without cargo should indicate they are empty
```

## 2. Implementation

### 2.1. Class Implementation

```java
public class TrainController {
    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    public Response listAllTrains() {
        List<TrainDTO> trains = trainService.getAllTrainsGrouped();
        return Response.ok(trains);
    }
}

public class TrainService {
    private final TrainRepository trainRepository;

    public List<TrainDTO> getAllTrainsGrouped() {
        List<Train> trains = trainRepository.findAll();
        return groupAndSortTrains(trains);
    }

    private List<TrainDTO> groupAndSortTrains(List<Train> trains) {
        // Implementation of grouping and sorting logic
    }
}
```

### 2.2. Code Organization

The implementation follows a layered architecture:
* **Controller Layer**: Handles HTTP requests
* **Service Layer**: Contains business logic
* **Repository Layer**: Manages data access
* **Domain Layer**: Contains domain entities
* **DTO Layer**: Handles data transfer objects

### 2.3. Test Implementation

```java
@Test
public void testEmptyTrainList() {
    when(trainRepository.findAll()).thenReturn(Collections.emptyList());
    List<TrainDTO> result = trainService.getAllTrainsGrouped();
    assertTrue(result.isEmpty());
}

@Test
public void testTrainGroupingAndSorting() {
    List<Train> mockTrains = createMockTrains();
    when(trainRepository.findAll()).thenReturn(mockTrains);
    List<TrainDTO> result = trainService.getAllTrainsGrouped();
    assertCorrectGroupingAndSorting(result);
}
``` 