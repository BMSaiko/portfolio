# US01 - As an Editor, I want to create a map with a size and a name.

## 1. Requirements Engineering

### 1.1. User Story Description

As an Editor, I want to create a map with a specified size and name so that I can use it as a base for further editing and industry placement.

### 1.2. Customer Specifications and Clarifications 

The enunciado specifies that:

- The map should have a customizable size, defined by width and height.
- The map should have a unique name for identification.
- The map serves as a base for adding industries and other elements.

### 1.3. Acceptance Criteria

- It must be possible to specify the map's width and height.
- The map must have a unique name.
- The map should be saved and retrievable for future editing.

### 1.4. Found out Dependencies

- **US02** - Industries can only be added to a map that has been created.

### 1.5 Input and Output Data

Input Data:
- Map name (typed value)
- Map width (typed value)
- Map height (typed value)

Output Data:
- Success/failure message 
- Created map with specified size and name

### 1.6. System Sequence Diagram (SSD)

1. Editor requests to create a map
2. System requests map name
3. Editor inputs map name
4. System requests map size (width and height)
5. Editor inputs map size
6. System validates and creates the map
7. System confirms successful creation

### 1.7 Other Relevant Remarks

The map creation functionality is fundamental for starting any new project within the system. It should be intuitive and allow for quick setup to facilitate the addition of industries and other elements.
