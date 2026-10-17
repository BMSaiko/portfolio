# US005 - Automated Deployment

## 1. Context

This user story focuses on preparing the project to be easily built, tested, executed, and deployed in a Unix-compatible environment.

The goal is to provide reusable Bash scripts that allow any developer, evaluator, or system operator to run the main project tasks without manually typing long Maven commands.

This improves reliability, reduces manual errors, and makes the project easier to demonstrate and evaluate.

---

## 1.1 List of Issues

- [x] Create environment bootstrap script
- [x] Create clean script
- [x] Create build script
- [x] Create test script
- [x] Create Backoffice execution script
- [x] Create run-all script
- [x] Create deployment script
- [x] Document script usage

---

## 2. Requirements

US005 - Automated Deployment

As Project Manager, I want the team to add to the project the necessary scripts, so that build, executions, and deployments can be executed effortlessly in a Unix-compatible machine.

Include scripts for all the major tasks and execution of applications.

### Acceptance Criteria

- US005.1: The repository includes Unix-compatible scripts for all major tasks.
- US005.2: The project can be built, tested, executed and deployed through scripts.
- US005.3: Scripts are simple to run from the project root.
- US005.4: The scripts validate the required environment where appropriate.
- US005.5: Script usage is documented.

---

## 3. Analysis

This user story focuses on automation and operational usability.

The project is a multi-module Maven application. The current executable application is the Backoffice console application, located in the module:

    aisafe.app.backoffice.console

The scripts therefore automate the following major tasks:

- Validate the Unix-compatible environment
- Clean Maven build artifacts
- Build the full project
- Run automated tests
- Start the Backoffice application
- Run the available application set
- Execute a full deployment pipeline

The scripts are written in Bash and are intended to be executed from a Unix-compatible shell, such as Linux, macOS, Git Bash, or a similar environment.

---

## 4. Design

### 4.1 Solution Overview

A `scripts/` directory was created in the project root.

Each script has a clear responsibility and can be executed independently.

### 4.2 Script Structure

    scripts/
    - bootstrap.sh
    - clean.sh
    - build.sh
    - test.sh
    - run-backoffice.sh
    - run-all.sh
    - deploy.sh

### 4.3 Script Responsibilities

| Script | Responsibility |
|--------|----------------|
| bootstrap.sh | Checks Java and Maven availability and prepares script permissions |
| clean.sh | Cleans Maven build artifacts |
| build.sh | Builds the full project using Maven |
| test.sh | Runs the automated test suite |
| run-backoffice.sh | Builds required modules and starts the Backoffice console application |
| run-all.sh | Starts all currently available applications |
| deploy.sh | Runs the full deployment pipeline |

### 4.4 Design Decisions

| Decision | Rationale |
|----------|-----------|
| Use Bash scripts | Required for Unix-compatible execution |
| Use Maven commands | The project is a multi-module Maven project |
| Run Backoffice through Maven exec | The Backoffice module already defines the executable main class |
| Keep scripts modular | Each task can be executed independently |
| Keep run-all simple | Currently only the Backoffice application is available |
| Use bootstrap script | Makes environment validation explicit |

---

## 5. Implementation

### 5.1 Scripts

The following scripts were added under `scripts/`.

#### bootstrap.sh

Validates the execution environment.

It checks:

- Java availability
- Maven availability
- Script execution permissions

Run:

    ./scripts/bootstrap.sh

#### clean.sh

Cleans generated Maven artifacts.

Run:

    ./scripts/clean.sh

#### build.sh

Builds the full project without running tests.

Run:

    ./scripts/build.sh

Internally, it executes:

    mvn clean install -DskipTests

#### test.sh

Runs the automated test suite.

Run:

    ./scripts/test.sh

Internally, it executes:

    mvn test

#### run-backoffice.sh

Starts the Backoffice console application.

Run:

    ./scripts/run-backoffice.sh

This script first installs the required modules and then starts the Backoffice module using Maven exec.

#### run-all.sh

Starts all currently available applications.

Run:

    ./scripts/run-all.sh

At the current stage of the project, this starts the Backoffice application.

#### deploy.sh

Executes the deployment pipeline.

Run:

    ./scripts/deploy.sh

The deployment pipeline performs:

1. Environment bootstrap
2. Clean
3. Tests
4. Build

After deployment, the Backoffice can be started with:

    ./scripts/run-backoffice.sh

---

## 6. How to Use

From the project root, first ensure the scripts are executable:

    chmod +x scripts/*.sh

Then run the desired script.

Recommended full setup:

    ./scripts/bootstrap.sh
    ./scripts/deploy.sh
    ./scripts/run-backoffice.sh

For development:

    ./scripts/clean.sh
    ./scripts/build.sh
    ./scripts/test.sh

For execution:

    ./scripts/run-backoffice.sh

---

## 7. Acceptance Tests

### Test 1 - Bootstrap Script

Command:

    ./scripts/bootstrap.sh

Expected result:

- Java is detected
- Maven is detected
- Scripts are made executable
- Script ends successfully

### Test 2 - Clean Script

Command:

    ./scripts/clean.sh

Expected result:

- Maven clean phase runs successfully
- Build artifacts are removed

### Test 3 - Build Script

Command:

    ./scripts/build.sh

Expected result:

- Project compiles successfully
- Maven install completes successfully

### Test 4 - Test Script

Command:

    ./scripts/test.sh

Expected result:

- Automated tests are executed
- Test phase completes successfully

### Test 5 - Backoffice Execution

Command:

    ./scripts/run-backoffice.sh

Expected result:

- Required modules are built
- Backoffice console starts
- The main menu is displayed

Expected menu:

    AISafe Backoffice
    1. Login
    0. Exit

### Test 6 - Run All Script

Command:

    ./scripts/run-all.sh

Expected result:

- All currently available applications are started
- At this stage, the Backoffice application starts successfully

### Test 7 - Deploy Script

Command:

    ./scripts/deploy.sh

Expected result:

- Bootstrap runs successfully
- Clean runs successfully
- Tests run successfully
- Build runs successfully
- Deployment finishes without errors

---

## 8. Current Limitations

At this stage, the project only has one executable application integrated into the scripts:

- Backoffice console application

The scripts do not currently start client applications, TCP servers, logging servers, or C simulations because those applications are not yet available as executable project modules in the current project structure.

When those components are added, new scripts can be introduced, for example:

- run-client.sh
- run-tcp-server.sh
- run-logging-server.sh
- build-simulation.sh
- run-simulation.sh

The current solution is prepared to evolve in that direction.

---

## 9. Conclusion

US005 is fulfilled by providing a Unix-compatible automation layer for the current project.

The scripts allow the project to be bootstrapped, cleaned, built, tested, deployed and executed through simple commands.

This improves usability, supports demonstrations, and reduces the risk of manual command errors during development and evaluation.
