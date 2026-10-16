US01 - As an Editor, I want to create a map with a size and a name.
2. Analysis
2.1. Relevant Domain Model Excerpt

The domain model excerpt shows the following classes and their relationships:

    Map: Represents the rectangular area that can be created with a specified size and name.

The relationships show that:

    A Map is a standalone entity that can be used as a base for further editing and industry placement.

2.2. Other Remarks

The map creation process involves validation of:

    Map name uniqueness within the system.
    Validity of size parameters (width and height) to ensure they are within acceptable limits.

The map serves as a foundational element for adding industries and other elements in subsequent user stories.
