# US004 - Continuous Integration Server

## 1. Context

Esta user story foca-se na configuração de um servidor de integração contínua (CI) para o projeto, utilizando GitHub Actions/Workflows. A integração contínua é essencial para garantir que o sistema se mantém sempre num estado válido ao longo do desenvolvimento, detetando erros de compilação ou falhas nos testes de forma automática e imediata após cada commit.

### 1.1 List of Issues

- **Analysis:** Review the project requirements and identify the necessary CI pipeline steps (build, test, coverage report, etc.).
- **Design:** Define the GitHub Actions workflow structure, including triggers, jobs, and steps to be executed.
- **Implement:** Create and configure the GitHub Actions workflow file in the repository.
- **Test:** Verify that the CI pipeline runs successfully on push/merge and that results and metrics are published correctly.

---

## 2. Requirements

**US004 - Continuous Integration Server**

*As Project Manager, I want the team to setup a continuous integration server.*

**Acceptance Criteria:**

- **US004.1** GitHub Actions/Workflows must be used as the CI tool.
- **US004.2** The CI pipeline must trigger automatically on every commit/push to the main branch.
- **US004.3** The pipeline must build the project using Maven (`mvn clean verify`).
- **US004.4** The pipeline must run all unit tests and fail if any test does not pass.
- **US004.5** The pipeline must publish build results and metrics (e.g., test coverage reports).
- **US004.6** Nightly builds must be configured and their results made available.

**Dependencies/References:**

- NFR05 – Continuous Integration (GitHub Actions + Maven)
- NFR06 – Commits in a CI environment (every commit must leave the system in a valid state)
- NFR03 – Unit testing requirements (90% coverage must be enforced)
- US003 – Project Structure (Maven multi-module project must exist before CI can be configured)

---

## 3. Analysis

Esta US é de infraestrutura e não envolve lógica de negócio. O objetivo é garantir que existe um pipeline automático que valida o estado do sistema após cada commit, em conformidade com o NFR06 — qualquer commit que quebre a compilação ou os testes resulta em penalização na avaliação.

Os principais passos do pipeline CI a configurar são:

| Passo | Descrição |
|---|---|
| **Checkout** | Fazer checkout do código do repositório |
| **Setup JDK** | Configurar a versão de Java necessária (e.g., Java 21) |
| **Build** | Executar `mvn clean verify` para compilar e testar |
| **Test Report** | Publicar os resultados dos testes (e.g., JUnit XML reports) |
| **Coverage Report** | Gerar e publicar o relatório de cobertura (e.g., JaCoCo) |
| **Nightly Build** | Trigger agendado (cron) para execução diária |

A cobertura de testes deve ser monitorizada pelo pipeline para garantir o cumprimento do NFR03 (mínimo de 90% nos packages `domain` e `controller`).

---

## 4. Design

### 4.1 Realization

A configuração do GitHub Actions é feita através de um único ficheiro YAML localizado em `.github/workflows/`, que agrupa tanto os triggers de commit como o agendamento noturno.

**Workflow — AlSafe CI Build (`maven-ci.yml`)**
- Triggers: `push` e `pull_request` (para os branches `main` e `master`), e `schedule` (cron, para o nightly build).
- Jobs: checkout → setup JDK → build & test → publish reports.

### 4.2 Acceptance Tests

**Test 1 (Automated):** Verificar que o pipeline CI é acionado automaticamente num push para o branch `main`.

- Refere-se a: US004.2
- Descrição: Fazer um commit e push para o branch `main` e confirmar que o workflow é iniciado automaticamente no separador "Actions" do GitHub.

**Test 2 (Automated):** Verificar que o pipeline falha quando existe um erro de compilação ou um teste falhado.

- Refere-se a: US004.3, US004.4
- Descrição: Introduzir intencionalmente um erro de compilação ou um teste falhado, fazer push, e confirmar que o pipeline reporta falha.

**Test 3 (Automated):** Verificar que os relatórios de testes e cobertura são publicados como artefactos.

- Refere-se a: US004.5
- Descrição: Após uma execução bem-sucedida do pipeline, confirmar a existência dos artefactos publicados (test results e coverage report) no separador "Actions" do GitHub.

**Test 4 (Manual/Inspection):** Verificar que o nightly build é executado de acordo com o schedule configurado.

- Refere-se a: US004.6
- Descrição: Inspecionar o histórico de execuções do workflow `maven-ci.yml` e confirmar que foi executado automaticamente de acordo com o cron definido.

---

## 5. Implementation

A implementação desta US consiste em:

1. Criar o ficheiro **`.github/workflows/maven-ci.yml`** configurado para ser acionado tanto por push/pull request como por schedule (cron) para o nightly build.
2. Configurar o plugin **JaCoCo** no `pom.xml` raiz (dentro de `eapli.base`) para geração de relatórios de cobertura de testes.
3. Garantir que os relatórios de testes (Surefire) e cobertura (JaCoCo) são publicados como artefactos no final de cada execução.
4. Validar que o pipeline falha corretamente quando a cobertura de testes cai abaixo dos 90% (NFR03), configurando o JaCoCo com um check de cobertura mínima.

---

## 6. Integration/Demonstration

A demonstração desta US é feita através do separador **"Actions"** do repositório GitHub, onde deve ser visível:

- A execução bem-sucedida do pipeline após um push para `main`.
- Os artefactos publicados (relatórios de testes e cobertura).
- O histórico de execuções do nightly build.

---

## 7. Observations

- O pipeline de CI é a principal salvaguarda contra violações do NFR06. Toda a equipa deve estar ciente de que um commit que quebre o build ou os testes resulta em penalização.
- Recomenda-se configurar notificações (e.g., por email ou GitHub notifications) para alertar a equipa em caso de falha no pipeline.
- O JaCoCo deve ser configurado com um threshold mínimo de 90% de cobertura nos packages `domain` e `controller`, fazendo o build falhar automaticamente caso esse valor não seja atingido.
- Em caso de falha no pipeline, o responsável pelo commit deve corrigir o problema com a maior brevidade possível para não bloquear o trabalho dos restantes elementos da equipa.