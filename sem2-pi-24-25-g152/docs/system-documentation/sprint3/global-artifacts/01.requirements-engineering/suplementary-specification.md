# Revised Supplementary Specification (FURPS+)

## Functionality

- Authentication system for Editors and Players with password requirements (7 alphanumeric characters, including 3 capital letters and 2 digits)
- Map and scenario editing capabilities
- Simulation capabilities with start/pause functionality
- Route planning and train management
- Railway network analysis for connectivity and maintenance
- Statistical analysis of station performance
- Cargo and passenger traffic management
- Persistence through object serialization

## Usability 

- Visualization of railway networks
- Station and train listing with proper sorting (trains grouped by locomotive type, then alphabetically)
- Intuitive interfaces for map creation, station building, and route planning
- Clear display of cargo demands/supplies at stations

## Reliability

- Validation of business rules when recording/updating data
- Prevention of invalid operations (e.g., overbuilding stations)
- System consistency for resources (industries, cities, stations, trains)

## Performance

- Efficient generation of cargoes at stations based on surrounding industries and cities
- Appropriate simulation speed for the railway network operation

## Supportability

- Code organization following best Object-Oriented practices for easy maintenance
- Documentation in English using Javadoc
- Unit testing using JUnit 5 framework with JaCoCo for coverage reporting
- Adoption of recognized coding standards (e.g., CamelCase)
- SVG format for all images/figures produced during development

## +

### Design Constraints

- Application must be developed in Java
- Statistical analysis component must be developed in Python using Jupyter Notebook

### Implementation Constraints

- Object serialization for data persistence
- Class structure must allow for easy maintenance and feature additions

### Interface Constraints
- Good and concise CLI interface
- Simple and intuitive
- Well structured, organized, reliable and readable menus for good UX (user experience)

### Physical Constraints

- No specific physical constraints mentioned with the client
