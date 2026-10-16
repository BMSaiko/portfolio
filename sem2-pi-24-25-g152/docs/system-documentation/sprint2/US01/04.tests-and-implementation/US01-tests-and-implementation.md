# US01 - As an Editor, I want to create a map with a size and a name.

## 4. Tests

### 4.1 Map Creation Tests

**Test 1:** Check that it is not possible to create a map with a non-unique name.

```java
@Test(expected = IllegalArgumentException.class)
public void ensureCannotCreateMapWithNonUniqueName() {
    MapService mapService = new MapService();
    mapService.createMap("Test Map", 100, 100);
    mapService.createMap("Test Map", 150, 150); // Duplicate name
}
```

**Test 2:** Check that maps are correctly created with valid parameters.

```java
@Test
public void ensureMapIsCreatedWithValidParameters() {
    MapService mapService = new MapService();
    Map map = mapService.createMap("Unique Map", 100, 100);
    
    assertNotNull(map);
    assertEquals("Unique Map", map.getName());
    assertEquals(100, map.getWidth());
    assertEquals(100, map.getHeight());
}
```

## 5. Construction (Implementation)

### 5.1 Map Class

The implementation follows the design with a Map class:

```java
public class Map {
    private String name;
    private int width;
    private int height;
    
    public Map(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }
    
    // Getters and setters
}
```

### 5.2 Service Implementation

The MapService handles map creation and validation:

```java
public class MapService {
    private MapRepository mapRepository;
    
    public Map createMap(String name, int width, int height) {
        if (!isNameUnique(name)) {
            throw new IllegalArgumentException("Map name must be unique");
        }
        Map map = new Map(name, width, height);
        mapRepository.save(map);
        return map;
    }
    
    private boolean isNameUnique(String name) {
        // Check repository for existing map names
    }
}
```

### 5.3 Repository Implementation

The MapRepository implements the Repository pattern for map persistence:

```java
public class MapRepository {
    private List<Map> maps = new ArrayList<>();
    
    public void save(Map map) {
        maps.add(map);
    }
    
    public boolean existsByName(String name) {
        return maps.stream().anyMatch(map -> map.getName().equals(name));
    }
}
```

## 6. Integration and Demo

The map creation functionality was integrated with the existing system. A simple demo was created to showcase the functionality:

- Create a map with a unique name and specified size.
- Display the map details to confirm successful creation.

This demonstration verifies that maps can be correctly created and stored in the system.

## 7. Observations

The current implementation focuses on the structural aspects of maps but does not yet implement their behaviors in terms of resource generation, transformation, or import/export. These behaviors will be implemented as part of the simulation functionality in future user stories.
Future enhancements could include:

Visualization of maps on the map with appropriate icons
Implementation of map capacity and production rates
Support for more complex map interactions based on proximity
Implementation of historical events affecting map performance

