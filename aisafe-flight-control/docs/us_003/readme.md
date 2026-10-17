# US003 - Project Structure

## 1. Context

Esta user story foca-se na configuração inicial da estrutura do projeto, de forma a suportar a arquitetura prevista e acelerar o desenvolvimento das user stories futuras. Uma estrutura bem definida desde o início garante consistência entre os membros da equipa e facilita a integração dos vários componentes do sistema (Java, ANTLR, C, base de dados, etc.).

### 1.1 List of Issues

- **Analysis:** Review the project requirements to define the necessary modules, packages, and technologies to be supported.
- **Design:** Define the project's directory structure, module layout, and technology stack configuration (e.g., Maven multi-module, ANTLR plugin setup).
- **Implement:** Create the project skeleton with all necessary configuration files, module structure, and technology scaffolding.
- **Test:** Verify that the project compiles successfully and that all modules are correctly configured (e.g., ANTLR generates code, Maven builds without errors).

---

## 2. Requirements

**US003 - Project Structure**

*As Project Manager, I want the team to configure the project structure to facilitate/accelerate the development of upcoming user stories.*

**Acceptance Criteria:**

- **US003.1** The project must be structured as a Maven multi-module project, with clearly separated modules for each major component (e.g., domain, application, infrastructure, UI, simulation).
- **US003.2** The project structure must include support for ANTLR (e.g., Maven ANTLR plugin configured and functional).
- **US003.3** The project must compile successfully from the root, without errors.
- **US003.4** The directory structure must include a `docs/` folder for documentation and a root `readme.md` explaining the project structure.
- **US003.5** The structure must support both in-memory and relational database configurations (NFR08).

**Dependencies/References:**

- NFR02 – Technical Documentation
- NFR05 – Continuous Integration (Maven as build tool)
- NFR07 – Deployment and Scripts
- NFR08 – Database by configuration
- NFR10 – Programming language (Java + C)
- US004 – Continuous Integration Server
- US083 – Flight DSL (requires ANTLR support)

---

## 3. Analysis

Esta US é de setup técnico e não envolve lógica de negócio. O principal objetivo é garantir que todos os membros da equipa têm uma base comum e que as tecnologias necessárias estão corretamente integradas no projeto desde o início.

A estrutura segue uma arquitetura em camadas (Layered Architecture / DDD), organizada em dois grandes grupos de módulos Maven:

| Módulo | Responsabilidade |
|---|---|
| `eapli.base` | Módulo pai do framework base EAPLI, agrega os sub-módulos abaixo |
| `eapli.base/exemplo.core` | Domínio: entidades, agregados, value objects e regras de negócio |
| `eapli.base/exemplo.infrastructure.application` | Infraestrutura e configuração da aplicação |
| `eapli.base/exemplo.persistence.impl` | Persistência: repositórios JPA e in-memory (H2) |
| `eapli.base/exemplo.app.backoffice.console` | UI de backoffice (menu-driven CLI) |
| `eapli.base/exemplo.app.user.console` | UI do utilizador (menu-driven CLI) |
| `eapli.base/exemplo.app.other.console` | UI de outros atores |
| `eapli.base/exemplo.app.common.console` | UI comum partilhada |
| `eapli.base/exemplo.bootstrappers` | Bootstrappers de dados iniciais |
| `eapli.base/exemplo.app.bootstrap` | Arranque da aplicação com dados de demonstração |
| `lprog` | Gramática ANTLR4 e processamento do Flight DSL (US083) |

O módulo `scomp` (simulação em C) será adicionado numa sprint futura, quando as user stories de simulação forem implementadas.

A configuração do plugin ANTLR4 no Maven permite que os ficheiros `.g4` em `lprog/src/main/antlr4/` sejam automaticamente compilados para classes Java durante o build.

---

## 4. Design

### 4.1 Realization

A estrutura de diretórios implementada é a seguinte:
```
sem4pi2526/                          (raiz do repositório)
├── pom.xml                          (parent POM — agrega todos os módulos)
├── readme.md                        (instruções de build e execução)
├── .gitignore                       (inclui target/, .idea/, etc.)
├── docs/
│   └── us_003/
│       └── readme.md                (este ficheiro)
├── eapli.base/                      (framework base EAPLI)
│   ├── pom.xml                      (pai dos sub-módulos EAPLI)
│   ├── exemplo.core/
│   ├── exemplo.infrastructure.application/
│   ├── exemplo.persistence.impl/
│   ├── exemplo.app.backoffice.console/
│   ├── exemplo.app.user.console/
│   ├── exemplo.app.other.console/
│   ├── exemplo.app.common.console/
│   ├── exemplo.bootstrappers/
│   └── exemplo.app.bootstrap/
└── lprog/                           (módulo ANTLR/DSL)
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── antlr4/              (ficheiros .g4 — a implementar na US083)
        │   └── java/
        │       └── pt/isep/sem4pi/lprog/
        └── test/
            └── java/
                └── pt/isep/sem4pi/lprog/
```

### 4.2 Acceptance Tests

**Test 1 (Automated):** Verificar que o projeto compila sem erros a partir da raiz.

- Refere-se a: US003.3
- Descrição: Executar o goal `install` a partir do painel Maven do IntelliJ (sem4pi2526 → Lifecycle → install), verificando que todos os módulos compilam com sucesso.
- Resultado: **PASSED** — BUILD SUCCESS com 12 módulos compilados (ver secção 6).

**Test 2 (Manual/Inspection):** Verificar a existência dos diretórios e ficheiros de configuração esperados.

- Refere-se a: US003.1, US003.4
- Descrição: Inspecionar o repositório e confirmar que todos os módulos Maven estão declarados no parent POM e que a pasta `docs/` existe.
- Resultado: **PASSED** — estrutura confirmada conforme secção 4.1.

**Test 3 (Automated):** Verificar que o plugin ANTLR é reconhecido e executado pelo Maven.

- Refere-se a: US003.2
- Descrição: Após o build, confirmar que o plugin ANTLR4 é executado no módulo `lprog`.
- Resultado: **PASSED** — output do build confirma `ANTLR 4: Processing source directory (...)\lprog\src\main\antlr4`.

---

## 5. Implementation

A implementação desta US consistiu em:

1. Criar o **parent `pom.xml`** na raiz com declaração dos módulos `eapli.base` e `lprog`, propriedades de Java 21 e versão do ANTLR.
2. Adicionar o bloco `<parent>` ao **`eapli.base/pom.xml`** para o ligar ao pai da raiz.
3. Criar o módulo **`lprog/`** com o respetivo `pom.xml` configurado com o plugin ANTLR4 (visitor e listener ativados).
4. Criar a estrutura de pastas `lprog/src/main/antlr4/` e `lprog/src/main/java/pt/isep/sem4pi/lprog/`.
5. Atualizar o **`.gitignore`** para excluir `target/`, `*.class`, `*.jar` e ficheiros gerados.

---

## 6. Integration/Demonstration

O build foi executado com sucesso a partir do painel Maven do IntelliJ (Lifecycle → install na raiz `sem4pi2526`), com o seguinte resultado:
```
[INFO] Reactor Summary:
[INFO] sem4pi2526 1.0.0-SNAPSHOT ......... SUCCESS [  0.591 s]
[INFO] exemplo 1.4.0-SNAPSHOT ............ SUCCESS [ 16.266 s]
[INFO] exemplo.infrastructure.application  SUCCESS [ 21.773 s]
[INFO] exemplo.core 1.4.0-SNAPSHOT ....... SUCCESS [ 34.136 s]
[INFO] exemplo.app.common.console ........ SUCCESS [  9.045 s]
[INFO] exemplo.bootstrapper .............. SUCCESS [ 10.272 s]
[INFO] exemplo.persistence.impl .......... SUCCESS [ 10.791 s]
[INFO] exemplo.app.backoffice.console .... SUCCESS [ 10.376 s]
[INFO] exemplo.app.user.console .......... SUCCESS [  8.076 s]
[INFO] exemplo.app.other.console ......... SUCCESS [ 21.888 s]
[INFO] exemplo.app.bootstrap ............. SUCCESS [  6.183 s]
[INFO] lprog 1.0.0-SNAPSHOT .............. SUCCESS [  9.988 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  02:39 min
[INFO] Finished at: 2026-04-02T12:01:08+01:00
```

---

## 7. Observations

- A estrutura definida aqui é a base de todo o projeto. Alterações futuras devem ser discutidas em equipa.
- O módulo `scomp` será adicionado quando as user stories de simulação (US100+) forem iniciadas, com um `Makefile` próprio invocado via `exec-maven-plugin`.
- O package base `pt.isep.sem4pi.lprog` foi criado no módulo `lprog` para garantir consistência nos nomes dos packages do projeto.
- Os warnings do JaCoCo no output do build (`Unsupported class file major version 67`) são inofensivos — resultam de uma incompatibilidade entre o JaCoCo 0.8.11 e o Java 23, e não afetam a compilação nem os testes.