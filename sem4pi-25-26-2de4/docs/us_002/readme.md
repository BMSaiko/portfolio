# US002 - Project repository

## 1. Context

This is the second task of the project and follows directly from US001. While US001 focused on understanding the technical constraints, US002 is about setting up the actual collaboration and project management tools that will enable the team to work effectively and comply with those constraints. This task must be completed at the very beginning of the project to establish the foundation for all future work.

### 1.1 List of issues

- [x] #26
- [x] #27
- [x] #28
- [x] #29

## 2. Requirements

**US002 - Project repository**
As Project Manager, I want the team to use the defined project repository in GitHub and setup a GitHub tool for project management.

**Acceptance Criteria:**

- US002.1 The team must accept the GitHub Classroom assignment and have access to the team repository.
- US002.2 All team members must be collaborators on the repository.
- US002.3 A GitHub project management tool (e.g., Projects, Issues, Milestones) must be configured to track the team's work throughout the sprints.
- US002.4 The repository must be cloned locally by all team members.

**Dependencies/References:**

- **US001 - Technical constraints:** Specifically NFR04 (Source Control) and NFR05 (Continuous Integration).
- **Project Requirements:** Section 2.1 (Teams) and Section 2.2 (Work methodology).

## 3. Analysis

This user story is about establishing the team's digital workspace. The project specifies that repositories will be created in the context of GitHub Classroom, and teams will receive information regarding their creation.

Key aspects to analyze:

### 3.1. GitHub Repository Structure

The repository will be the single source of truth for all project artifacts:
- Source code
- Documentation (as per NFR02)
- Scripts (as per NFR07)
- Project management artifacts

### 3.2. GitHub Project Management Tools

Several GitHub tools can be used for project management:

| Tool | Purpose | Relevance to Project |
| :--- | :--- | :--- |
| **Issues** | Track tasks, bugs, feature requests | Each user story and task can be tracked as an issue. |
| **Milestones** | Group issues into sprints/releases | Sprint 1, Sprint 2, Sprint 3 can be configured as milestones. |
| **Projects** | Kanban-style board for workflow visualization | Can be used to track progress of issues (To Do, In Progress, Done). |
| **Labels** | Categorize issues (e.g., US, bug, enhancement, UC) | Help filter and organize work by course unit or type. |

### 3.3. Access and Permissions

All team members must have write access to the repository to push code and manage issues.

## 4. Design

As this is an infrastructure setup task, the "design" consists of decisions about how to structure the repository and configure the project management tools.

### 4.1. Repository Structure Design

The following directory structure is proposed to organize the project:
TODO: change to real structure
```
project-root/
│
├── docs/                           # Documentation (as per NFR02)
│   ├── us_001/                     # Documentation for each user story
│   ├── us_002/
│   ├── us_003/
│   ├── us_004/
│   ├── us_005/
│   ├── us_006/
│   ├── us_007/
│   ├── us_008/
│   ├── us_009/
│   ├── us_010/
│   ├── us_011/
│   └── us_666/                     # Example/template documentation
│
├── libs/                           # External libraries and dependencies
│
├── eapli.base/                     # Root directory for the EAPLI project
│   │
│   ├── exemplo.app.backoffice.console/    # Backoffice console application
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── eapli/
│   │           │       └── exemplo/
│   │           │           └── app/
│   │           │               └── backoffice/
│   │           │                   └── console/
│   │           │                       └── presentation/
│   │           │                           ├── authz/
│   │           │                           └── utente/
│   │           └── resources/
│   │
│   ├── exemplo.app.bootstrap/             # Bootstrap application
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── eapli/
│   │           │       └── exemplo/
│   │           │           └── app/
│   │           │               └── bootstrap/
│   │           └── resources/
│   │
│   ├── exemplo.app.common.console/        # Common console functionality
│   │   └── src/
│   │       └── main/
│   │           └── java/
│   │               └── eapli/
│   │                   └── exemplo/
│   │                       └── app/
│   │                           └── common/
│   │                               └── console/
│   │                                   └── presentation/
│   │                                       └── authz/
│   │
│   ├── exemplo.app.other.console/         # Other console application
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/
│   │       │   │   └── eapli/
│   │       │   │       └── exemplo/
│   │       │   │           └── app/
│   │       │   │               └── other/
│   │       │   │                   └── console/
│   │       │   │                       └── presentation/
│   │       │   └── resources/
│   │       └── test/
│   │           └── java/
│   │               └── eapli/
│   │                   └── exemplo/
│   │                       └── app/
│   │                           └── other/
│   │                               └── console/
│   │
│   ├── exemplo.app.user.console/          # User console application
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── eapli/
│   │           │       └── exemplo/
│   │           │           └── app/
│   │           │               └── utente/
│   │           │                   └── console/
│   │           │                       └── presentation/
│   │           │                           └── myuser/
│   │           └── resources/
│   │
│   ├── exemplo.bootstrappers/             # Bootstrappers for initial data
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── eapli/
│   │           │       └── exemplo/
│   │           │           └── infrastructure/
│   │           │               ├── bootstrapers/
│   │           │               │   └── demo/
│   │           │               └── smoketests/
│   │           └── resources/
│   │
│   ├── exemplo.core/                      # Core domain and business logic
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── eapli/
│   │   │   │   │       └── exemplo/
│   │   │   │   │           ├── infrastructure/
│   │   │   │   │           │   └── persistence/
│   │   │   │   │           ├── myutente/
│   │   │   │   │           │   └── application/
│   │   │   │   │           ├── usermanagement/
│   │   │   │   │           │   ├── application/
│   │   │   │   │           │   │   └── eventhandlers/
│   │   │   │   │           │   └── domain/
│   │   │   │   │           └── utentemanagement/
│   │   │   │   │               ├── application/
│   │   │   │   │               │   └── eventhandlers/
│   │   │   │   │               ├── domain/
│   │   │   │   │               │   └── events/
│   │   │   │   │               └── repositories/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   │       └── java/
│   │   │           └── eapli/
│   │   │               └── exemplo/
│   │   │                   └── utentemanagement/
│   │   │                       └── domain/
│   │   └── target/                        # Build output (generated)
│   │
│   ├── exemplo.infrastructure.application/ # Infrastructure application layer
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── eapli/
│   │           │       └── exemplo/
│   │           │           └── infrastructure/
│   │           │               └── authz/
│   │           └── resources/
│   │
│   ├── exemplo.persistence.impl/          # Persistence implementations
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── eapli/
│   │           │       └── exemplo/
│   │           │           └── persistence/
│   │           │               └── impl/
│   │           │                   ├── inmemory/
│   │           │                   └── jpa/
│   │           └── resources/
│   │               └── META-INF/
│   │
│   └── target/                            # Global build output
│
├── .gitattributes
├── .gitignore
├── gen                                   # Generated files reference
└── readme.md
```

### 4.2. Project Management Configuration Design

The following setup is proposed for GitHub project management:

1. **Milestones:**
   - `Sprint 1` (Due: 2025-04-05)
   - `Sprint 2` (Due: 2025-05-17)
   - `Sprint 3` (Due: 2025-06-14)

2. **Labels:**
   - `US` (User Story)
   - `EAPLI`, `LPROG`, `RCOMP`, `SCOMP`, `LAPR4` (for course-specific tasks)
   - `bug`, `enhancement`, `documentation`
   - `high`, `medium`, `low` (priority)

3. **Project Board:**
   - Create a classic project board named "Sprint Planning".
   - Columns: `Backlog`, `To Do`, `In Progress`, `Done`.

4. **Issues:**
   - Each user story should be created as an issue, assigned to the appropriate milestone, and added to the project board.

### 4.3. Realization Diagram

```
[Team receives GitHub Classroom invitation]
                |
                v
[Team accepts invitation and repository is created]
                |
                v
      [Team members clone repository locally]
                |
                v
 [Team creates directory structure as per design]
                |
                v
[Team configures GitHub Milestones, Labels, and Project Board]
                |
                v
[Team creates issues for initial user stories (US001, US002, US003...)]
```

### 4.4. Acceptance Tests

**Test 1 (Manual):** *Verify repository access.*
- **Refers to Acceptance Criteria:** US002.1, US002.2
- **Description:** Each team member attempts to clone the repository and push a trivial change (e.g., update README). All members must succeed.

**Test 2 (Manual):** *Verify project management setup.*
- **Refers to Acceptance Criteria:** US002.3
- **Description:**
  1. Navigate to the GitHub repository.
  2. Verify that Milestones (Sprint 1, 2, 3) are created with correct due dates.
  3. Verify that relevant labels are created.
  4. Verify that a project board exists with the defined columns.
  5. Verify that an issue for US002 exists and is correctly labeled and placed on the board.

## 5. Implementation

This section describes the steps taken to implement the repository setup.

### 5.1. Repository Initialization

1. The team lead (or any member) accepted the GitHub Classroom invitation, creating the team repository (e.g., `sem4pi2526-sem4pi2526_2de4`).
2. All team members were added as collaborators (automatically by GitHub Classroom based on team membership).
3. Each team member cloned the repository:

```bash
git clone https://github.com/Departamento-de-Engenharia-Informatica/sem4pi2526-sem4pi2526_2de4.git
cd sem4pi2526-sem4pi2526_2de4
```

### 5.2. GitHub Project Management Configuration

- **Milestones:** Created via the GitHub web interface under "Issues" -> "Milestones".
- **Labels:** Created via "Issues" -> "Labels".
- **Project Board:** Created via "Projects" tab, selecting "Kanban" template.
- **Issues:** Created issues for US001 and US002, assigning them to Sprint 1 milestone and adding them to the project board.

### 5.3. Major Commits

| Commit Hash (Example) | Description |
| :--- | :--- |
| `a1b2c3d` | (issue id) - Initial commit: Added README and .gitignore. |
| `e4f5g6h` | (issue id) - Docs: Added US001 documentation. |
| `i7j8k9l` | (issue id) - Infra: Created project directory structure and initial pom.xml. |
| `m0n1o2p` | (issue id) - Infra: Added initial GitHub Actions CI workflow. |
| `q3r4s5t` | (issue id) - Docs: Added US002 documentation. |

## 6. Integration/Demonstration

This user story establishes the infrastructure. Its success is demonstrated by:

- All team members having a functioning local clone of the repository.
- The repository containing the agreed-upon directory structure.
- The GitHub project management tools being fully configured and populated with the initial user stories.
- The team being able to use the project board to track progress during the first sprint planning meeting.

To demonstrate:

1. Open the GitHub repository in a browser and show the directory structure.
2. Navigate to the "Projects" tab and show the board with US001 and US002 in the appropriate columns.
3. Navigate to "Issues" -> "Milestones" and show the Sprint 1 milestone with its due date and associated issues.

## 7. Observations

This setup is critical for adhering to NFR04 (Source Control) and NFR05 (Continuous Integration). The CI workflow file (`.github/workflows/ci.yml`) was also created at this stage to comply with US004 later.

The initial `pom.xml` was created with basic dependencies (e.g., JUnit 5) to support future development and testing.

Using GitHub's integrated project management tools (Issues, Milestones, Projects) instead of external tools ensures tight coupling with the codebase, making traceability easier (e.g., commits can reference issues).

It is important that all team members regularly update the project board as they work on tasks to maintain an accurate representation of progress. This will be part of the LAPR4 assessment.