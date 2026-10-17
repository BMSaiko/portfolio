# Auditoria da documentação

> Documentação adicionada em 2026 (portfolio). English version: [AUDIT.md](AUDIT.md).

Estado: **passagem 3 (re-pontuada após os docs adicionados)**, 2026-10-17. A passagem 1 (2026-10-16) pontuou os ficheiros que existiam no repositório; a passagem 3 re-pontua os critérios cobertos pelos ficheiros `ARCHITECTURE`, `SETUP`, `INDEX` e ADR adicionados, com as regras abaixo. Os critérios que os docs adicionados não tocam mantêm a nota da passagem 1. Nada do conteúdo original das equipas foi alterado e nenhum achado foi corrigido.

## Escala

| Nota | Significado |
|---|---|
| 0 | Nada encontrado |
| 1 | Só referido no README, ou um artefacto isolado |
| 2 | Documento(s) próprio(s), incompleto(s) |
| 3 | Próprio, estruturado, completo e verificado contra o código |
| – | Não se aplica ao tipo de projeto |

Regras da passagem 3 (o que foi aplicado a cada critério):

- **Arquitetura (2)**: 3 quando se escreveu um `ARCHITECTURE.md` a partir do código e a lista de problemas foi verificada; os projetos P3 mantêm a nota da passagem 1.
- **Setup (3)**: 3 só se os comandos foram corridos. Cada `SETUP` diz quais não foram testados (nesta máquina não há JDK, Gradle, dotnet, PHP, MySQL, Docker nem Visual Studio), por isso valem 2; só o `lp1-2022-g2-tema1` (servidor local corrido) vale 3. O `sem4pi-25-26-2de4` passa de 3 para 2 por esta regra.
- **Modelo de dados (4)**: 3 quando o ER vem do SQL/migrações reais; 2 quando vem de duas fontes ou de um diagrama sem script de esquema (`rentacar`, `pap-gym`) ou é inferido (`porto-on-wheels`).
- **API / contratos (5)**: `biblioteca-ermesinde` 2 (tabela de endpoints); `dsos-grupo1-bombastico` 1 (rotas documentadas, sem contratos).
- **Decisões (6)**: 2 por um ADR em cada projeto P1/P2 (um só ADR, logo não 3). Os projetos P3 não têm ADR porque os docs das equipas já guardam as decisões.
- **Segurança (9)**: 2 quando as limitações conhecidas estão documentadas (nenhuma corrigida).
- **Classe**: SEM = só README; PARCIAL = há docs próprias mas falta arquitetura, setup ou ADR; COMPLETO = arquitetura, setup e (ADR ou índice com rastreabilidade) presentes. Mostrada como passagem 1 → passagem 3.

## Critérios

1. README · 2. Arquitetura · 3. Setup / execução reprodutível · 4. Modelo de dados · 5. API / contratos · 6. Decisões (ADRs) · 7. Testes · 8. Qualidade / CI · 9. Segurança · 10. Requisitos / user stories · 11. Equipa / processo · 12. Higiene do repo · 13. Bilingue (EN/PT)

## Matriz

| Projeto | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | Classe |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| [pap-gym](#pap-gym) | 3 | 3 | 2 | 2 | – | 2 | 0 | 0 | 2 | 0 | 0 | 0 | 3 | PARCIAL → COMPLETO |
| [lp1-2022-g2-tema1](#lp1-2022-g2-tema1) | 3 | 3 | 3 | – | – | 2 | 0 | 0 | – | 0 | 0 | 0 | 3 | SEM → COMPLETO |
| [companhia-aerea](#companhia-aerea) | 3 | 3 | 2 | – | – | 2 | 0 | 0 | – | 0 | 0 | 0 | 3 | SEM → COMPLETO |
| [apoo-lp1-companhia-aerea](#apoo-lp1-companhia-aerea) | 3 | 3 | 2 | – | – | 2 | 0 | 0 | – | 0 | 0 | 1 | 3 | SEM → COMPLETO |
| [dsos-grupo1-bombastico](#dsos-grupo1-bombastico) | 3 | 3 | 2 | 3 | 1 | 2 | 1 | 0 | 2 | 0 | 0 | 2 | 3 | SEM → COMPLETO |
| [lp3-g2](#lp3-g2) | 3 | 3 | 2 | 3 | – | 2 | 2 | 0 | 2 | 2 | 3 | 1 | 3 | PARCIAL → COMPLETO |
| [rentacar](#rentacar) | 3 | 3 | 2 | 2 | – | 2 | 0 | 0 | 2 | 0 | 0 | 1 | 3 | SEM → COMPLETO |
| [porto-on-wheels](#porto-on-wheels) | 3 | 3 | 2 | 2 | – | 2 | 0 | 0 | 2 | 0 | 0 | 0 | 3 | SEM → COMPLETO |
| [biblioteca-ermesinde](#biblioteca-ermesinde) | 3 | 3 | 2 | 3 | 2 | 2 | 1 | 2 | 2 | 1 | 0 | 1 | 3 | PARCIAL → COMPLETO |
| [rcomp-25-26-de-g2](#rcomp-25-26-de-g2) | 3 | 3 | 2 | – | – | 0 | – | – | 2 | 2 | 3 | 1 | 3 | PARCIAL → COMPLETO |
| [sem2-pi-24-25-g152](#sem2-pi-24-25-g152) | 3 | 3 | 2 | 2 | 1 | 1 | 2 | 0 | 2 | 3 | 3 | 1 | 3 | COMPLETO |
| [sem3-pi-24-25-g152](#sem3-pi-24-25-g152) | 3 | 2 | 2 | 3 | 0 | 0 | 2 | 0 | 2 | 3 | 2 | 1 | 3 | COMPLETO |
| [sem3-pi-25-26-g075](#sem3-pi-25-26-g075) | 3 | 3 | 2 | 2 | 0 | 0 | 2 | 0 | 2 | 3 | 2 | 1 | 3 | COMPLETO |
| [sem4pi-25-26-2de4](#sem4pi-25-26-2de4) | 3 | 3 | 2 | 2 | 1 | 1 | 2 | 2 | 2 | 3 | 3 | 3 | 3 | COMPLETO |

Coluna Classe: passagem 1 → passagem 3 (regra na lista acima).

## Evidência por projeto

### pap-gym
- Existente: `pap-gym/img/` (capturas, ícones, logótipos). Passos de execução no README com `GYM_DB_PASSWORD`.
- Lacunas: sem arquitetura, diagrama ER, ADR, testes, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../pap-gym/docs/ARCHITECTURE.pt.md) (camadas, fluxos, ER lido do diagrama da equipa, 9 problemas verificados), [SETUP](../pap-gym/docs/SETUP.pt.md) (SQL reconstruído, comandos não testados), [ADR 0001](../pap-gym/docs/adr/0001-wpf-mvvm-navigation-direct-sql.pt.md).
- Achados: todo o SQL é construído a partir de input do utilizador; capacidade da sala verificada só na interface; `MySql.Data` não restaurável pelo NuGet tal como está versionado; o README diz .NET Framework 4.7.2 mas o projeto tem como alvo o 4.8; o formulário de registo do website não está ligado; três tabelas do diagrama nunca são usadas; sem script de esquema.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (M) parametrizar todo o SQL; P1 (S) impor a capacidade da sala fora da interface; P1 (S) acrescentar `.gitignore` (`bin/`, `obj/`); P2 (S) restaurar `MySql.Data` pelo NuGet e alinhar o README com o .NET Framework 4.8 do projeto; P2 (S) incluir o script de esquema (o SETUP tem um reconstruído); P3 (S) ligar ou remover o formulário de registo do website, acrescentar `LICENSE`; P3 (L) primeiros testes.

### lp1-2022-g2-tema1
- Existente: só README (site estático: `index.html`, `pages/`, `styles/`, `assets/`).
- Lacunas: mapa do site, guia de edição/deploy, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../lp1-2022-g2-tema1/docs/ARCHITECTURE.pt.md) (mapa do site Mermaid, tabela de páginas/estilos, 9 problemas verificados), [SETUP](../lp1-2022-g2-tema1/docs/SETUP.pt.md) (servidor local executado e verificado; guia de edição e deploy), [ADR 0001](../lp1-2022-g2-tema1/docs/adr/0001-static-html-css-no-build.pt.md) (estado Inferido).
- Achados: URLs absolutos à raiz falham em `file://` e em sub-caminho (o "abrir `index.html`" do README dá ligações/estilos partidos); a ligação `/pages/amdvsintel` sem `.html` devolve 404 num servidor simples; 3 referências a imagens diferem em maiúsculas dos ficheiros versionados; ligação `socket_strx4` sem o host; navegação duplicada em 5 ficheiros; 3 recursos não referenciados; 1 `<img>` sem `alt`. Por resolver: `.gitignore`, licença.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (S) tornar os URLs relativos, ou o README mandar usar um servidor local; P1 (S) corrigir a ligação `/pages/amdvsintel` (404); P2 (S) corrigir as 3 referências a imagens com maiúsculas diferentes, a ligação `socket_strx4` e o `<img>` sem `alt`; P2 (M) eliminar a navegação duplicada em 5 ficheiros; P3 (S) `.gitignore`, `LICENSE`, remover 3 recursos não referenciados.

### companhia-aerea
- Existente: só README (8 ficheiros versionados).
- Lacunas: diagrama de classes, setup, nota de evolução v1→v2, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../companhia-aerea/docs/ARCHITECTURE.pt.md) (diagrama de classes Mermaid com as classes em falta, 7 problemas verificados), [SETUP](../companhia-aerea/docs/SETUP.pt.md) (falha de compilação prevista, não executada), [ADR 0001](../companhia-aerea/docs/adr/0001-keep-early-version-as-unrepaired-snapshot.pt.md) (decisão de curadoria, estado Proposto). A evolução v1→v2 está no ADR do projeto irmão.
- Achados: faltam `Pessoa` e `PilotoTripulante`, logo não compila; `Main.voos` nunca inicializado; tripulação nunca adicionada; o leitor de data-hora não consegue ler um valor válido; o menu corre uma vez e só a opção 1 está implementada. Por resolver: `.gitignore`.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): Mantida de propósito como snapshot não reparado (ADR 0001), por isso sem correções de código. P2 (S) dizer no README que não compila e ligar a `apoo-lp1-companhia-aerea`; P3 (S) acrescentar `.gitignore`.

### apoo-lp1-companhia-aerea
- Existente: só README (14 ficheiros versionados), `.gitignore`.
- Lacunas: diagrama de classes UML (herança), setup, ADR v1→v2.
- Adicionado (passagem 2): [ARCHITECTURE](../apoo-lp1-companhia-aerea/docs/ARCHITECTURE.pt.md) (diagramas Mermaid de classes e sequência, menu, formato de ficheiro, 10 problemas verificados), [SETUP](../apoo-lp1-companhia-aerea/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../apoo-lp1-companhia-aerea/docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.pt.md) (v1→v2).
- Achados: o formato de gravação com `;` quebra com `;` no texto e falha com ficheiros malformados; média `NaN` sem correspondências; sem testes; o domínio depende do estático `Main.voos`. A versão inicial `companhia-aerea` referencia `Pessoa` e `PilotoTripulante`, que não estão no repositório, logo não compila tal como está commitada; o menu lista 11 opções mas só a 1 está implementada.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (S) proteger a média `NaN` e tratar ficheiros de gravação malformados; P2 (M) substituir o formato com `;` por um com escape (CSV/JSON); P2 (M) testes unitários; P2 (M) tirar a dependência do domínio do estático `Main.voos`; P3 (S) `LICENSE`, `.editorconfig`.

### dsos-grupo1-bombastico
- Existente: `dsos-grupo1-bombastico/DB/2023_ERM_DSOS_G1.sql`; 12 ficheiros de testes; `.editorconfig`, `.gitattributes`, 13 `.gitignore`.
- Lacunas: arquitetura, diagrama ER a partir do SQL, setup, ADR, como correr os testes.
- Adicionado (passagem 2): [ARCHITECTURE](../dsos-grupo1-bombastico/docs/ARCHITECTURE.pt.md) (papéis, rotas, fluxos, ER a partir do dump, 9 problemas verificados), [SETUP](../dsos-grupo1-bombastico/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../dsos-grupo1-bombastico/docs/adr/0001-laravel-mvc-roles-by-table.pt.md).
- Achados: sem imposição de papéis nas rotas (qualquer utilizador autenticado aprova empresas/propostas), importação CSV de alunos sem autenticação e com passwords sem hash, código de entrega de projeto quebrado, dump e migrações dessincronizados, dados pessoais e tokens de reposição no dump, `.env.example` com `DB_PASSWORD`/`APP_KEY` não vazios (rodar se forem reais).
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (S) esvaziar ou rodar `DB_PASSWORD`/`APP_KEY` no `.env.example`; P1 (M) limpar dados pessoais e tokens de reposição do dump SQL; P1 (M) verificação de papéis nas rotas, e autenticação e hash na importação CSV de alunos; P2 (S) corrigir a entrega de projeto e ressincronizar dump e migrações; P3 (M) GitHub Actions a correr os ficheiros de teste existentes; P3 (S) `LICENSE`. `.editorconfig` e `.gitattributes` já existem.

### lp3-g2
- Existente: `lp3-g2/Docs/` com PNG de casos de uso, sequência e modelo de domínio, `Modelo de base de dados.png`, `drawSQL-sqlsrv-export-2023-12-02.sql`, `UAT Test Plan.pdf`, `Sprints/*.pdf` (plannings, retrospetivas).
- Lacunas: setup, ADR, visão de arquitetura escrita, API/contratos.
- Adicionado (passagem 2, na pasta `Docs/` existente porque o Windows trata `docs` e `Docs` como o mesmo caminho): [ARCHITECTURE](../lp3-g2/Docs/ARCHITECTURE.pt.md) (pacotes, papéis, fluxos de login e importação XML, ER a partir do export SQL, 7 problemas verificados), [SETUP](../lp3-g2/Docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../lp3-g2/Docs/adr/0001-xsd-validated-xml-import.pt.md).
- Achados: caminhos do XSD fixos (`C:\Trabalho\...`), `ConfirmarStock` processa só o último id, SQL por concatenação, falta `app_utilizador_movimentos.fxml`, classe de teste vazia, sem FK em `Produto.id_Encomenda`, `VARCHAR` sem comprimento no export.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (S) tornar configuráveis os caminhos do XSD; P1 (S) corrigir `ConfirmarStock` (processa só o último id); P2 (S) acrescentar o `app_utilizador_movimentos.fxml` em falta ou retirar a referência; acrescentar a FK em `Produto.id_Encomenda` e os comprimentos de `VARCHAR` no export; P2 (M) queries parametrizadas e uma classe de teste a sério mais CI; P3 (S) `.editorconfig`, `.gitattributes`, `LICENSE`.

### rentacar
- Existente: README; SQL em `rentacar/RentACar/src/main/java/olabomdia.sql`.
- Lacunas: arquitetura, diagrama ER, setup (`RENTACAR_DB_PASSWORD`), ADR, testes.
- Adicionado (passagem 2): [ARCHITECTURE](../rentacar/docs/ARCHITECTURE.pt.md) (pacotes, fluxo de login/acessos, duas fontes de ER, 7 problemas verificados), [SETUP](../rentacar/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../rentacar/docs/adr/0001-javafx-jdbc-stored-procedures.pt.md).
- Achados: o `mainClass` do `build.gradle` aponta para uma `HelloApplication` inexistente; `olabomdia.sql` só tem esquema e não inclui as tabelas/vistas/procedimentos que o código chama; SQL construído por concatenação; sem testes.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (S) corrigir o `mainClass` para `./gradlew run` funcionar; P1 (M) completar o script SQL com as tabelas, vistas e procedimentos que o código chama; P2 (M) queries parametrizadas; P3 (M) primeiros testes mais CI; P3 (S) `.editorconfig`, `.gitattributes`, `LICENSE`; o README diz "script de esquema/dados" mas o ficheiro só tem esquema.

### porto-on-wheels
- Existente: só README. Sem esquema SQL no repositório (o único `LICENSE` é o do Font Awesome em vendor).
- Lacunas: arquitetura, diagrama ER (marcado **inferido** a partir das queries), setup (`PORTOONWHEELS_DB_*`), ADR, aviso de passwords em texto simples, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../porto-on-wheels/docs/ARCHITECTURE.pt.md) (ficheiros, fluxos, ER **inferido**, 9 problemas verificados), [SETUP](../porto-on-wheels/docs/SETUP.pt.md) (SQL reconstruído, comandos não testados), [ADR 0001](../porto-on-wheels/docs/adr/0001-custom-mysqli-wrapper.pt.md).
- Achados: o registo guarda o e-mail como password; `admin/db/*.php` sem autenticação; redirecionamentos de admin sem `exit`; a atualização de perfil confia num id enviado pelo cliente; passwords em texto simples; sem esquema.
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (M) deixar de guardar o e-mail como password e fazer hash das passwords; P1 (M) autenticação em `admin/db/*.php` e `exit` após os redirecionamentos de admin; P1 (S) acrescentar `.gitignore`; P2 (S) validar o id de perfil no servidor; P2 (S) incluir um script de esquema (o SETUP tem um reconstruído); P3 (S) `LICENSE` do projeto (a única é a do Font Awesome), `.editorconfig`.

### biblioteca-ermesinde
- Existente: `Relatorio_de_DOS.docx`, `docker-compose.yml`, `dockerfile`, `jenkinsfile`, migrações EF em `Biblioteca_Ermesinde/Migrations`, 4 ficheiros de testes.
- Lacunas encontradas: arquitetura (camadas API/EF/BD), tabela de endpoints, diagrama do pipeline, setup (`SA_PASSWORD`, `SONAR_TOKEN`), ADR Jenkins + SonarQube + Docker Compose.
- Adicionado (passagem 2): [ARCHITECTURE](../biblioteca-ermesinde/docs/ARCHITECTURE.pt.md) (camadas, ER, tabela da API, pipeline, 10 problemas verificados), [SETUP](../biblioteca-ermesinde/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../biblioteca-ermesinde/docs/adr/0001-jenkins-sonarqube-docker-compose.pt.md).
- Alerta de segurança: `docker-compose.yml` contém uma password de admin do SonarQube em texto simples; passar para variável de ambiente e rodar (ficheiro original intocado).
- Melhorias (P1/P2/P3, esforço S/M/L; não aplicadas): P1 (S) mover a password de admin do SonarQube para uma variável de ambiente e rodá-la; P1 (S) corrigir `PostReserva` (`.First()`) e correr na pipeline os 4 ficheiros relacionados com testes; P2 (M) GitHub Actions de build e testes espelhando o `jenkinsfile`; P2 (S) `.editorconfig`, `.gitattributes`, `LICENSE`; P3 (S) exemplos de pedido/resposta dos endpoints, badges.

### rcomp-25-26-de-g2
- Existente: `rcomp-25-26-de-g2/doc/sprint{1,2,3}/` com READMEs por membro, esquemas, `planning.md`, `review.md`, `global-inventory.md`, capturas de DNS.
- Lacunas: índice entre sprints, ADR, pasta `sprint2/4444444` com aspeto de placeholder, verificar `.gitignore`.
- Adicionado (passagem 2, melhorar sem reescrever): [INDEX](../rcomp-25-26-de-g2/docs/INDEX.pt.md) (sprints, rastreabilidade tarefa → entregável → revisão, desenho num relance, lista de melhorias), [SETUP](../rcomp-25-26-de-g2/docs/SETUP.pt.md) (Packet Tracer 9.0.0, ficheiros a abrir; não testado), [GLOSSARY](../rcomp-25-26-de-g2/docs/GLOSSARY.pt.md). `doc/` original intocado; sem ADR (as decisões da equipa já estão nos plannings).
- Achados: o README diz "grupo de 3" mas o Sprint 1 tem 4 membros e um Terminal 5 por concluir; inventário global parcial (falta T5); o planning do Sprint 3 dá ao DNS do T2 o `.129` (o gateway) e o README implementa `.130`; `sprint2/4444444` é o placeholder da cadeira; mojibake no README do T2 do Sprint 3; 32 running-configs com hashes de passwords (`password 7` no router ISP).
- Lista de melhorias (P1/P2/P3, não aplicadas): [docs/INDEX.pt.md §5](../rcomp-25-26-de-g2/docs/INDEX.pt.md).

### sem2-pi-24-25-g152
- Existente: `docs/README.md`, `docs/TeamMembersAndTasks.md`, `docs/auth/`, `docs/datasets/`, `docs/system-documentation/` (523 ficheiros, 200 PlantUML), 77 ficheiros de testes.
- Lacunas: índice por US, rastreabilidade US → código → testes, CI.
- Adicionado (passagem 2, melhorar sem reescrever): [INDEX](../sem2-pi-24-25-g152/docs/INDEX.pt.md) (tabela US → docs → código, testes, achados, lista de melhorias), [SETUP](../sem2-pi-24-25-g152/docs/SETUP.pt.md) (não testado). Docs originais intocados; o glossário do domínio já existe e é ligado, não duplicado.
- Achados: o `Main` expõe só o menu de conectividade (US13/US14/US27), nenhuma das dez UIs de editor/jogador de US01–US12 é instanciada; sem testes unitários (só harness de desempenho e `US29PerformanceTestRunner`); `TeamMembersAndTasks.md` não tem linhas do Sprint 3 e liga a pastas inexistentes do Sprint 2 (US02/US03); `AuthLib` obtido de um repositório Maven externo; artefacto do POM ainda `project-template`.
- Lista de melhorias (P1/P2/P3, não aplicadas): [docs/INDEX.pt.md §5](../sem2-pi-24-25-g152/docs/INDEX.pt.md).

### sem3-pi-24-25-g152
- Existente: `documentation/{ARQCP,BDDAD,ESINF,FSIAP,LPRA3}` com SQL por user story (`Create Tables.sql`, `USBD*.sql`), 136 ficheiros de testes.
- Lacunas: índice, glossário, setup, rastreabilidade, ADR, CI. Mantêm-se passwords locais de dev (Oracle `sys`, `123456`).
- Adicionado (passagem 2, melhorar sem reescrever): [INDEX](../sem3-pi-24-25-g152/docs/INDEX.pt.md) (4 subsistemas, família de US → docs → código, testes, achados, lista de melhorias), [SETUP](../sem3-pi-24-25-g152/docs/SETUP.pt.md) (por subsistema; não testado). Os glossários existentes (LAPR3, BDDAD) são ligados, não duplicados; sem ADR (as decisões estão nas entregas das UCs).
- Achados: o simulador resolve os caminhos dos CSV a partir da raiz `.git` mais próxima, por isso dentro do portfolio apontam para um `portfolio/documentation/ESINF` inexistente (previsto, não executado); o Sprint 2 de BDDAD existe em duas cópias divergentes (20 diferenças); 6 `.o`, 15 `.bak_*`, 2 `.lck` e `.DS_Store` versionados; ficheiros de histórias sem extensão; máquina Arduino física substituída por um emulador; password Oracle local não vazia em `config.properties`.
- Lista de melhorias (P1/P2/P3, não aplicadas): [docs/INDEX.pt.md §5](../sem3-pi-24-25-g152/docs/INDEX.pt.md).

### sem3-pi-25-26-g075
- Existente: `docs/{ARQCP,BDDAD,ESINF,LAPR3}`, `docs/global-artifacts`, `docs/README.md`, 54 PlantUML, 40 ficheiros de testes.
- Lacunas: índice, glossário, setup, rastreabilidade, ADR, CI.
- Adicionado (passagem 2, melhorar sem reescrever): [INDEX](../sem3-pi-25-26-g075/docs/INDEX.pt.md) (estrutura, US → docs → código → testes de ESINF/LAPR3/BDDAD/ARQCP, achados, lista de melhorias), [SETUP](../sem3-pi-25-26-g075/docs/SETUP.pt.md) (Java, Oracle, RISC-V; não testado). O glossário, as regras de negócio e o modelo de domínio existentes são ligados, não duplicados; sem ADR.
- Achados: o `docs/README.md` só detalha USEI01–03 embora existam nove histórias ESINF; USLP07 e USLP10 partilham o título e a USLP10 não tem documento de testes; USAC11–16 têm código mas não docs; URL JDBC fixo em 5 ficheiros Java e passwords literais em 5 (valores não reproduzidos); os Makefiles de Assembly precisam de um toolchain cruzado RISC-V e do QEMU, não referidos no README; sem comando de execução do ponto de entrada Java; os ids das histórias são citados sobretudo nos testes, não no código de produção.
- Lista de melhorias (P1/P2/P3, não aplicadas): [docs/INDEX.pt.md §5](../sem3-pi-25-26-g075/docs/INDEX.pt.md).

### sem4pi-25-26-2de4
- Existente: `docs/global-artifacts/` (glossário, composição do grupo, `CLOUD_TESTING.md`, `CLOUD_VPN_ACCESS.md`, reuniões, casos de uso PlantUML, PDFs de requisitos), `docs/us_*`, `docs/Peer-Assessement/`, workflow de CI, `LICENSE`, 129 ficheiros de testes.
- Lacunas: índice por US, rastreabilidade, ADR, um ficheiro de teste omitido (caminho longo), avisos LF→CRLF.
- Adicionado (passagem 2, melhorar sem reescrever): [INDEX](../sem4pi-25-26-2de4/docs/INDEX.pt.md) (estrutura, família de US → docs → código → testes, CI, achados, lista de melhorias), [SETUP](../sem4pi-25-26-2de4/docs/SETUP.pt.md) (por componente; não testado). O glossário e as notas de cloud existentes são ligados, não duplicados; sem ADR.
- Achados: o README liga a um `docs/readme.md` inexistente; a documentação de US100–US112 está em duas árvores (`docs/` e `scomp/docs/`) com conteúdo diferente em US107/111/112; a CI só compila o `eapli.base` (o framework de exemplo), não os módulos AISafe, o `lprog` nem o `scomp`; esquema H2 recriado a cada arranque (`hbm2ddl.auto=create`); US044 e US086 são citadas no código sem docs; os scripts de cloud dependem da VPN do ISEP.
- Lista de melhorias (P1/P2/P3, não aplicadas): [docs/INDEX.pt.md §5](../sem4pi-25-26-2de4/docs/INDEX.pt.md).

## Próximas passagens

1. Feito: passagem 1 (inventário), passagem 2 (docs dos 14 projetos, um commit cada), passagem 3 (re-pontuação e listas de melhorias, este ficheiro).
2. Pendente, verificação global antes do fecho: `grep` de segredos, verificação de links relativos em todos os `.md` adicionados, diff só com adições, verificação da sintaxe Mermaid.
3. Decisões que ficam com o BMS: rodar e mover as credenciais indicadas nos achados, limpar o dump SQL do `dsos-grupo1-bombastico`, e aplicar (ou não) qualquer melhoria acima. Nenhuma foi aplicada.
