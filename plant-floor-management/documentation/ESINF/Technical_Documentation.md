# Technical Documentation for Production Planner Simulator

## Table of Contents
- [Overview](#overview)
- [System Architecture](#system-architecture)
  - [Main Components](#main-components)
  - [Component Interactions](#component-interactions)
- [Data Structures](#data-structures)
  - [Classes and Responsibilities](#classes-and-responsibilities)
  - [Data Models](#data-models)
- [Processing Logic](#processing-logic)
  - [Workflow](#workflow)
  - [Algorithms Used](#algorithms-used)
- [Installation and Configuration](#installation-and-configuration)
  - [System Requirements](#system-requirements)
  - [Installation Steps](#installation-steps)
- [Dependencies](#dependencies)
- [Appendix](#appendix)
  - [References](#references)

---

## Overview

The **Production Planner Simulator** is a Java-based application designed to simulate and optimize production planning within industrial environments. It enables users to model production processes, allocate resources efficiently, and predict outcomes based on various scenarios, thereby enhancing productivity and reducing operational costs.

## System Architecture

### Main Components

1. **User Interface (UI)**
   - **Responsibility**: Facilitates interaction with the user, capturing inputs, and displaying results.

2. **Processing Module**
   - **Responsibility**: Executes simulation and optimization logic.

3. **Data Management Module**
   - **Responsibility**: Handles creation, storage, and manipulation of data required for simulations.

4. **Configuration Module**
   - **Responsibility**: Manages simulation parameters and user preferences.

5. **Import/Export Module**
   - **Responsibility**: Enables data import from external sources and export of simulation results.

### Component Interactions

- **UI ↔ Configuration Module**: The UI allows users to adjust parameters that are passed to the Configuration Module.
- **Configuration Module ↔ Data Management Module**: Configurations influence how data is managed and structured.
- **Data Management Module ↔ Processing Module**: Data is supplied to the Processing Module to perform simulations.
- **Processing Module ↔ UI**: Simulation results are sent back to the UI for display.

## Data Structures

### Classes and Responsibilities

1. **Simulation**
   - **Responsibility**: Manages the lifecycle of the simulation, coordinating between data and processing modules.

2. **Article**
   - **Responsibility**: Represents a product in the production process, containing attributes such as ID, priority, and associated operations.

3. **Workstation**
   - **Responsibility**: Represents a workstation or machine, including details like ID, current operation, and capacity.

4. **Operation**
   - **Responsibility**: Defines a specific operation in the production process, including duration and required resources.

5. **Configuration**
   - **Responsibility**: Stores configuration parameters that influence simulation behavior.

6. **DataManager**
   - **Responsibility**: Handles creation, reading, updating, and deletion of data used in simulations.

7. **Result**
   - **Responsibility**: Contains the outcomes generated post-simulation, such as production times and resource allocations.

### Data Models

- **List of Articles**: A collection of `Article` objects representing the products to be manufactured.
- **List of Workstations**: A collection of `Workstation` objects representing available machines for production.
- **Operations**: Each `Article` has a sequence of `Operation` objects that need to be executed on workstations.
- **Configuration Parameters**: Values that define aspects like the number of simulation runs, optimization criteria, and resource allocation strategies.

## Processing Logic

### Workflow

1. **Initialization**
   - Load data for articles and workstations.
   - Apply user-defined configurations.

2. **Simulation Execution**
   - Allocate articles to workstations based on required operations.
   - Execute operations sequentially, respecting workstation capacities and availability.

3. **Result Collection**
   - Record execution times, resource allocations, and identify any production bottlenecks.

4. **Finalization**
   - Display results to the user.
   - Optionally export data for further analysis.

### Algorithms Used

- **Resource Allocation Algorithm**
  - **Purpose**: Efficiently allocates articles to workstations to minimize wait times and optimize resource usage.
  
- **Operation Sequencing Algorithm**
  - **Purpose**: Determines the optimal order of operations to maximize productivity and reduce total production time.
  
- **Optimization Algorithm**
  - **Purpose**: Utilizes heuristic methods to adjust parameters and find configurations that yield the best simulation performance.

## Installation and Configuration

### System Requirements

- **Java Development Kit (JDK) 23**
- **Apache Maven 3.6.0 or higher**
- **Operating System**: Windows, macOS, or Linux

### Installation Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/Departamento-de-Engenharia-Informatica/sem3-pi_2024_25_G152.git
    ```
2. **Navigate to the Project Directory**

    ```bash
    Copiar código
    cd prodPlanSimulator
    ```

3. **Compile the Project**

    ```bash
    mvn clean compile
    ```

4. **Run the Application**

    ```bash
    .\run.bat     
    ```

### Dependencies
- JUnit 5.10.0: Framework for unit testing.
- SLF4J 2.0.7: Logging facade.
- Log4j 2.20.0: Logging implementation.
- Jansi 2.4.0: Library for ANSI escape sequences.
- JLine 3.21.0: Library for handling command-line - interfaces.

### Appendix

#### References

- JUnit 5 Official Documentation
- SLF4J - Simple Logging Facade for Java
- Log4j 2 - Apache Logging Services
- Jansi - Java ANSI Escape
- JLine - Java Library for Handling Console Input
yaml

