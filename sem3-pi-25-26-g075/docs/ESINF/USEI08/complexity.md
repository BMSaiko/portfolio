
## USEI08 - Range Search

### `getStationsByRange(double latMin, double latMax, double lonMin, double lonMax, StationFilter filter)`

**Time Complexity:**
- **O(√n + k log k)**
  - `repository.getKdTree().rangeSearch()`: **O(√n + k)** 
    - KD-tree visits approximately √n nodes for a balanced tree
    - k = number of stations found in range
    - Pruning eliminates ~50% of nodes per level on average
  - Optional filter application: **O(k)** - applies predicate to k candidates
  - `results.sort(Station::compareTo)`: **O(k log k)** - sort matching stations
  - **Total: O(√n + k log k)**

    
**Where:**
- **n**: Total number of stations in the KD-tree
- **k**: Number of stations within the specified range (after filtering)