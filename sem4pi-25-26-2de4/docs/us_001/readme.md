# US001 - Technical constraints

## 1. Context

This is the first task of the project. Its purpose is to make the team aware of, and to document, the various technical constraints and concerns (non-functional requirements) that will govern the development of the system throughout all sprints. Understanding these constraints is crucial for making correct architectural and design decisions from the very beginning.

### 1.1 List of issues

- **Analysis:** Review Section 5 of the project requirements to identify all stated Non-Functional Requirements (NFRs).
- **Design:** Not applicable for this user story, as it is an informational and awareness task.
- **Implement:** Not applicable.
- **Test:** Not applicable.

## 2. Requirements

**US001 - Technical constraints**
As Project Manager, I want the team to follow the technical constraints and concerns of the project. These constraints and concerns are described in section 5.

**Acceptance Criteria:**

- US001.1 The team must be able to identify and describe all the technical constraints listed in Section 5 of the project requirements document.
- US001.2 The team must acknowledge that these constraints will influence the development process, architectural choices, and implementation of all future user stories.

**Dependencies/References:**

- **Main Reference:** Section 5 - "Non-Functional Requirements (NFR)" of the `Project_Requirements_V1.pdf`.

## 3. Analysis

This user story is about knowledge acquisition and project setup awareness. The main artifact for this analysis is the list of NFRs themselves. The team must read and understand each one to ensure future work complies.

The following is a summary and analysis of the key technical constraints from Section 5:

| NFR ID | Constraint Name | Analysis / Implication for the Team |
| :--- | :--- | :--- |
| **NFR01** | Project management using scrum | The team must follow Scrum practices. The LAPR4 PL teacher is the Scrum Master. Weekly scrum meetings will be held. The "Development Process document" on Moodle is a key reference. |
| **NFR02** | Technical Documentation | All documentation (analysis, design, testing) for every US must be in the `docs` folder in Markdown format. UML diagrams (preferably with PlantUML) must be included with both source and PNG files. |
| **NFR03** | Unit testing requirements | A Test-Driven Development (TDD) approach is recommended. Test coverage for Java controller and domain packages must be **at least 90%** at all times. Tests should follow the AAA (Arrange, Act, Assert) pattern. |
| **NFR04** | Source Control | A GitHub repository will be provided. Only the `main` (or `master`) branch will be used. |
| **NFR05** | Continuous Integration | GitHub Actions (Workflows) must be used for CI. Nightly builds with results and metrics are required. Maven is the build tool. |
| **NFR06** | Commits in a CI environment | This is a critical rule: **every commit must leave the system in a valid state (compiling and passing tests).** Failure to do so results in a grade of zero for LAPR4 in that sprint. This enforces disciplined development. |
| **NFR07** | Deployment and Scripts | The repository must contain scripts to build, deploy, and execute the solution on a Linux system. A root `readme.md` must explain how to use these scripts. |
| **NFR08** | Database by configuration | The persistence layer must be configurable to run either "in-memory" (for dev/testing) or with a persistent relational database (for final deployment). The system must be able to bootstrap/initialize default data. |
| **NFR09** | Authentication and Authorization | All users must authenticate, and access to functionalities must be authorized based on roles (e.g., Administrator, Backoffice Operator, Pilot). |
| **NFR10** | Programming language | Java is the primary language. Other languages may be used only as specified by other requirements (e.g., C for simulation components in US100, US105, etc.). |
| **NFR11** | LPROG Assessment | This outlines specific quality criteria for the DSL part of the project: conceptual design, grammar clarity, robustness of analysis, quality of error reporting, and innovation. |

## 4. Design

As this US does not involve implementing a functional feature, there is no technical design in terms of classes or algorithms. The "design" is the team's plan to comply with these constraints.

### 4.1. Realization

The realization of this US is the team's commitment to abide by these rules. The main evidence will be the team's work methodology and the resulting project artifacts (repository structure, commit history, documentation, test reports, etc.) that will be produced in subsequent sprints.

### 4.2. Acceptance Tests

This US cannot be validated by automated tests. It is validated by inspection of the team's work throughout the project.

**Test 1 (Manual/Inspection):** *Verify team awareness.*
- **Refers to Acceptance Criteria:** US001.1
- **Description:** During the first sprint review or a team meeting, each member should be able to list and explain the main technical constraints (e.g., test coverage requirement, CI commit policy, documentation standards).

## 5. Implementation

No implementation is required for this user story.

## 6. Integration/Demonstration

This user story does not produce a demonstrable feature. Its "demonstration" is implicit in how the team follows the constraints while developing and integrating all other features. For example:

- Following NFR06 will be visible in the GitHub commit history.
- Following NFR03 will be visible in the test coverage reports from the CI pipeline (NFR05).
- Following NFR02 will be visible in the `docs` folder.

## 7. Observations

- This is the foundational user story for the entire project. All subsequent work must be checked against these constraints.
- The constraint **NFR06 (Commits in a continuous integration environment)** is particularly strict and will require a disciplined workflow where features are developed in small, verifiable increments to avoid breaking the build.
- The requirement for **90% test coverage (NFR03)** on domain and controller packages is a high bar that will require thorough unit testing from the start. It encourages the design of testable, decoupled classes.