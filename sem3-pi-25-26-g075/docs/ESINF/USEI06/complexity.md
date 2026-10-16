# Time Complexity Analysis for USEI06 and USEI08

## USEI06 - Station Search Methods

### 1. `getStationsByTimeZoneGroup(String group)`

**Time Complexity: O(n + k log k)**
- `repository.getStationsBST().inOrder()`: **O(n)** - BST in-order traversal (n = number of TimeZone nodes)
- For each matching TimeZone:
  - `repository.getStations().get(timeZone)`: **O(1)** - HashMap lookup
  - `result.addAll(stations)`: **O(1)** - List addition
- `result.sort(Station::compareTo)`: **O(k log k)** - Sort all k matching stations once

**Total: O(n + k log k)**

### 2. `getStationsByTimeZoneGroups(String[] groups)`

**Time Complexity: O(m × (n + k log k))**
- Calls `getStationsByTimeZoneGroup()` m times (m = number of groups)
- Each call: **O(n + k log k)**

### 3. `getStationsByCoordinates(double latitude, double longitude)`

**Time Complexity: O(n + k log k)**
- `repository.getCoordinatesBST().inOrder()`: **O(n)** - BST in-order traversal (n = number of CoordinateStationKey nodes)
- For each node matching coordinates:
  - `result.add(key.getStation())`: **O(1)** - List addition
- `result.sort(Station::compareTo)`: **O(k log k)** - Sort all k matching stations once

**Total: O(n + k log k)**


### 4. `getStationsByCountry(String country)`

**Time Complexity: O(n + k log k)**
- `repository.getStationsBST().inOrder()`: **O(n)** - BST in-order traversal (n = number of TimeZone nodes)
- For each matching TimeZone:
  - `repository.getStations().get(timeZone)`: **O(1)** - HashMap lookup
  - `result.addAll(stations)`: **O(1)** - List addition
- `result.sort(Station::compareTo)`: **O(k log k)** - Sort all k matching stations once

**Total: O(n + k log k)**

### Where:
- **n**: Total number of nodes in the BST
- **m**: Number of time zone groups in query
- **k**: Total number of stations matching the query criteria
