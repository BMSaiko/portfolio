# Auditoria da documentação

> Documentação adicionada em 2026 (portfolio). English version: [AUDIT.md](AUDIT.md).

Estado: **passagem 1 (inventário de ficheiros)**, 2026-10-16. As notas vêm dos ficheiros que existem no repositório (caminhos citados abaixo). A amostragem de qualidade e as listas de melhorias (P1/P2/P3) entram em passagens seguintes. Nada do conteúdo original das equipas foi alterado.

## Escala

| Nota | Significado |
|---|---|
| 0 | Nada encontrado |
| 1 | Só referido no README, ou um artefacto isolado |
| 2 | Documento(s) próprio(s), incompleto(s) |
| 3 | Próprio, estruturado e completo (por inventário; qualidade ainda não amostrada) |
| – | Não se aplica ao tipo de projeto |

## Critérios

1. README · 2. Arquitetura · 3. Setup / execução reprodutível · 4. Modelo de dados · 5. API / contratos · 6. Decisões (ADRs) · 7. Testes · 8. Qualidade / CI · 9. Segurança · 10. Requisitos / user stories · 11. Equipa / processo · 12. Higiene do repo · 13. Bilingue (EN/PT)

## Matriz

| Projeto | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | Classe |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| [pap-gym](#pap-gym) | 3 | 1 | 2 | 1 | – | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 3 | PARCIAL |
| [lp1-2022-g2-tema1](#lp1-2022-g2-tema1) | 3 | 0 | 1 | – | – | 0 | 0 | 0 | – | 0 | 0 | 0 | 3 | SEM |
| [companhia-aerea](#companhia-aerea) | 3 | 0 | 1 | – | – | 0 | 0 | 0 | – | 0 | 0 | 0 | 3 | SEM |
| [apoo-lp1-companhia-aerea](#apoo-lp1-companhia-aerea) | 3 | 0 | 1 | – | – | 0 | 0 | 0 | – | 0 | 0 | 1 | 3 | SEM |
| [dsos-grupo1-bombastico](#dsos-grupo1-bombastico) | 3 | 0 | 1 | 2 | 0 | 0 | 1 | 0 | 1 | 0 | 0 | 2 | 3 | SEM |
| [lp3-g2](#lp3-g2) | 3 | 2 | 1 | 3 | – | 0 | 2 | 0 | 1 | 2 | 3 | 1 | 3 | PARCIAL |
| [rentacar](#rentacar) | 3 | 0 | 1 | 1 | – | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 3 | SEM |
| [porto-on-wheels](#porto-on-wheels) | 3 | 0 | 1 | 0 | – | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 3 | SEM |
| [biblioteca-ermesinde](#biblioteca-ermesinde) | 3 | 0 | 2 | 2 | 1 | 0 | 1 | 2 | 1 | 1 | 0 | 1 | 3 | PARCIAL |
| [rcomp-25-26-de-g2](#rcomp-25-26-de-g2) | 3 | 3 | 2 | – | – | 0 | – | – | 1 | 2 | 3 | 1 | 3 | PARCIAL |
| [sem2-pi-24-25-g152](#sem2-pi-24-25-g152) | 3 | 3 | 2 | 2 | 1 | 1 | 2 | 0 | 2 | 3 | 3 | 1 | 3 | COMPLETO |
| [sem3-pi-24-25-g152](#sem3-pi-24-25-g152) | 3 | 2 | 1 | 3 | 0 | 0 | 2 | 0 | 1 | 3 | 2 | 1 | 3 | COMPLETO |
| [sem3-pi-25-26-g075](#sem3-pi-25-26-g075) | 3 | 3 | 1 | 2 | 0 | 0 | 2 | 0 | 1 | 3 | 2 | 1 | 3 | COMPLETO |
| [sem4pi-25-26-2de4](#sem4pi-25-26-2de4) | 3 | 3 | 3 | 2 | 1 | 1 | 2 | 2 | 2 | 3 | 3 | 3 | 3 | COMPLETO |

Regra: **SEM** = sem docs técnicas além do README; **PARCIAL** = há docs próprias mas falta pelo menos arquitetura / setup / ADR; **COMPLETO** = existem docs por sprint ou por história (continua a receber lista de melhorias).

## Evidência por projeto

### pap-gym
- Existente: `pap-gym/img/` (capturas, ícones, logótipos). Passos de execução no README com `GYM_DB_PASSWORD`.
- Lacunas: sem arquitetura, diagrama ER, ADR, testes, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../pap-gym/docs/ARCHITECTURE.pt.md) (camadas, fluxos, ER lido do diagrama da equipa, 9 problemas verificados), [SETUP](../pap-gym/docs/SETUP.pt.md) (SQL reconstruído, comandos não testados), [ADR 0001](../pap-gym/docs/adr/0001-wpf-mvvm-navigation-direct-sql.pt.md).
- Achados: todo o SQL é construído a partir de input do utilizador; capacidade da sala verificada só na interface; `MySql.Data` não restaurável pelo NuGet tal como está versionado; o README diz .NET Framework 4.7.2 mas o projeto tem como alvo o 4.8; o formulário de registo do website não está ligado; três tabelas do diagrama nunca são usadas; sem script de esquema.

### lp1-2022-g2-tema1
- Existente: só README (site estático: `index.html`, `pages/`, `styles/`, `assets/`).
- Lacunas: mapa do site, guia de edição/deploy, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../lp1-2022-g2-tema1/docs/ARCHITECTURE.pt.md) (mapa do site Mermaid, tabela de páginas/estilos, 9 problemas verificados), [SETUP](../lp1-2022-g2-tema1/docs/SETUP.pt.md) (servidor local executado e verificado; guia de edição e deploy), [ADR 0001](../lp1-2022-g2-tema1/docs/adr/0001-static-html-css-no-build.pt.md) (estado Inferido).
- Achados: URLs absolutos à raiz falham em `file://` e em sub-caminho (o "abrir `index.html`" do README dá ligações/estilos partidos); a ligação `/pages/amdvsintel` sem `.html` devolve 404 num servidor simples; 3 referências a imagens diferem em maiúsculas dos ficheiros versionados; ligação `socket_strx4` sem o host; navegação duplicada em 5 ficheiros; 3 recursos não referenciados; 1 `<img>` sem `alt`. Por resolver: `.gitignore`, licença.

### companhia-aerea
- Existente: só README (8 ficheiros versionados).
- Lacunas: diagrama de classes, setup, nota de evolução v1→v2, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../companhia-aerea/docs/ARCHITECTURE.pt.md) (diagrama de classes Mermaid com as classes em falta, 7 problemas verificados), [SETUP](../companhia-aerea/docs/SETUP.pt.md) (falha de compilação prevista, não executada), [ADR 0001](../companhia-aerea/docs/adr/0001-keep-early-version-as-unrepaired-snapshot.pt.md) (decisão de curadoria, estado Proposto). A evolução v1→v2 está no ADR do projeto irmão.
- Achados: faltam `Pessoa` e `PilotoTripulante`, logo não compila; `Main.voos` nunca inicializado; tripulação nunca adicionada; o leitor de data-hora não consegue ler um valor válido; o menu corre uma vez e só a opção 1 está implementada. Por resolver: `.gitignore`.

### apoo-lp1-companhia-aerea
- Existente: só README (14 ficheiros versionados), `.gitignore`.
- Lacunas: diagrama de classes UML (herança), setup, ADR v1→v2.
- Adicionado (passagem 2): [ARCHITECTURE](../apoo-lp1-companhia-aerea/docs/ARCHITECTURE.pt.md) (diagramas Mermaid de classes e sequência, menu, formato de ficheiro, 10 problemas verificados), [SETUP](../apoo-lp1-companhia-aerea/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../apoo-lp1-companhia-aerea/docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.pt.md) (v1→v2).
- Achados: o formato de gravação com `;` quebra com `;` no texto e falha com ficheiros malformados; média `NaN` sem correspondências; sem testes; o domínio depende do estático `Main.voos`. A versão inicial `companhia-aerea` referencia `Pessoa` e `PilotoTripulante`, que não estão no repositório, logo não compila tal como está commitada; o menu lista 11 opções mas só a 1 está implementada.

### dsos-grupo1-bombastico
- Existente: `dsos-grupo1-bombastico/DB/2023_ERM_DSOS_G1.sql`; 12 ficheiros de testes; `.editorconfig`, `.gitattributes`, 13 `.gitignore`.
- Lacunas: arquitetura, diagrama ER a partir do SQL, setup, ADR, como correr os testes.
- Adicionado (passagem 2): [ARCHITECTURE](../dsos-grupo1-bombastico/docs/ARCHITECTURE.pt.md) (papéis, rotas, fluxos, ER a partir do dump, 9 problemas verificados), [SETUP](../dsos-grupo1-bombastico/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../dsos-grupo1-bombastico/docs/adr/0001-laravel-mvc-roles-by-table.pt.md).
- Achados: sem imposição de papéis nas rotas (qualquer utilizador autenticado aprova empresas/propostas), importação CSV de alunos sem autenticação e com passwords sem hash, código de entrega de projeto quebrado, dump e migrações dessincronizados, dados pessoais e tokens de reposição no dump, `.env.example` com `DB_PASSWORD`/`APP_KEY` não vazios (rodar se forem reais).

### lp3-g2
- Existente: `lp3-g2/Docs/` com PNG de casos de uso, sequência e modelo de domínio, `Modelo de base de dados.png`, `drawSQL-sqlsrv-export-2023-12-02.sql`, `UAT Test Plan.pdf`, `Sprints/*.pdf` (plannings, retrospetivas).
- Lacunas: setup, ADR, visão de arquitetura escrita, API/contratos.
- Adicionado (passagem 2, na pasta `Docs/` existente porque o Windows trata `docs` e `Docs` como o mesmo caminho): [ARCHITECTURE](../lp3-g2/Docs/ARCHITECTURE.pt.md) (pacotes, papéis, fluxos de login e importação XML, ER a partir do export SQL, 7 problemas verificados), [SETUP](../lp3-g2/Docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../lp3-g2/Docs/adr/0001-xsd-validated-xml-import.pt.md).
- Achados: caminhos do XSD fixos (`C:\Trabalho\...`), `ConfirmarStock` processa só o último id, SQL por concatenação, falta `app_utilizador_movimentos.fxml`, classe de teste vazia, sem FK em `Produto.id_Encomenda`, `VARCHAR` sem comprimento no export.

### rentacar
- Existente: README; SQL em `rentacar/RentACar/src/main/java/olabomdia.sql`.
- Lacunas: arquitetura, diagrama ER, setup (`RENTACAR_DB_PASSWORD`), ADR, testes.
- Adicionado (passagem 2): [ARCHITECTURE](../rentacar/docs/ARCHITECTURE.pt.md) (pacotes, fluxo de login/acessos, duas fontes de ER, 7 problemas verificados), [SETUP](../rentacar/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../rentacar/docs/adr/0001-javafx-jdbc-stored-procedures.pt.md).
- Achados: o `mainClass` do `build.gradle` aponta para uma `HelloApplication` inexistente; `olabomdia.sql` só tem esquema e não inclui as tabelas/vistas/procedimentos que o código chama; SQL construído por concatenação; sem testes.

### porto-on-wheels
- Existente: só README. Sem esquema SQL no repositório (o único `LICENSE` é o do Font Awesome em vendor).
- Lacunas: arquitetura, diagrama ER (marcado **inferido** a partir das queries), setup (`PORTOONWHEELS_DB_*`), ADR, aviso de passwords em texto simples, `.gitignore`.
- Adicionado (passagem 2): [ARCHITECTURE](../porto-on-wheels/docs/ARCHITECTURE.pt.md) (ficheiros, fluxos, ER **inferido**, 9 problemas verificados), [SETUP](../porto-on-wheels/docs/SETUP.pt.md) (SQL reconstruído, comandos não testados), [ADR 0001](../porto-on-wheels/docs/adr/0001-custom-mysqli-wrapper.pt.md).
- Achados: o registo guarda o e-mail como password; `admin/db/*.php` sem autenticação; redirecionamentos de admin sem `exit`; a atualização de perfil confia num id enviado pelo cliente; passwords em texto simples; sem esquema.

### biblioteca-ermesinde
- Existente: `Relatorio_de_DOS.docx`, `docker-compose.yml`, `dockerfile`, `jenkinsfile`, migrações EF em `Biblioteca_Ermesinde/Migrations`, 4 ficheiros de testes.
- Lacunas encontradas: arquitetura (camadas API/EF/BD), tabela de endpoints, diagrama do pipeline, setup (`SA_PASSWORD`, `SONAR_TOKEN`), ADR Jenkins + SonarQube + Docker Compose.
- Adicionado (passagem 2): [ARCHITECTURE](../biblioteca-ermesinde/docs/ARCHITECTURE.pt.md) (camadas, ER, tabela da API, pipeline, 10 problemas verificados), [SETUP](../biblioteca-ermesinde/docs/SETUP.pt.md) (comandos não testados), [ADR 0001](../biblioteca-ermesinde/docs/adr/0001-jenkins-sonarqube-docker-compose.pt.md).
- Alerta de segurança: `docker-compose.yml` contém uma password de admin do SonarQube em texto simples; passar para variável de ambiente e rodar (ficheiro original intocado).

### rcomp-25-26-de-g2
- Existente: `rcomp-25-26-de-g2/doc/sprint{1,2,3}/` com READMEs por membro, esquemas, `planning.md`, `review.md`, `global-inventory.md`, capturas de DNS.
- Lacunas: índice entre sprints, ADR, pasta `sprint2/4444444` com aspeto de placeholder, verificar `.gitignore`.

### sem2-pi-24-25-g152
- Existente: `docs/README.md`, `docs/TeamMembersAndTasks.md`, `docs/auth/`, `docs/datasets/`, `docs/system-documentation/` (523 ficheiros, 200 PlantUML), 77 ficheiros de testes.
- Lacunas: índice por US, rastreabilidade US → código → testes, CI.

### sem3-pi-24-25-g152
- Existente: `documentation/{ARQCP,BDDAD,ESINF,FSIAP,LPRA3}` com SQL por user story (`Create Tables.sql`, `USBD*.sql`), 136 ficheiros de testes.
- Lacunas: índice, glossário, setup, rastreabilidade, ADR, CI. Mantêm-se passwords locais de dev (Oracle `sys`, `123456`).

### sem3-pi-25-26-g075
- Existente: `docs/{ARQCP,BDDAD,ESINF,LAPR3}`, `docs/global-artifacts`, `docs/README.md`, 54 PlantUML, 40 ficheiros de testes.
- Lacunas: índice, glossário, setup, rastreabilidade, ADR, CI.

### sem4pi-25-26-2de4
- Existente: `docs/global-artifacts/` (glossário, composição do grupo, `CLOUD_TESTING.md`, `CLOUD_VPN_ACCESS.md`, reuniões, casos de uso PlantUML, PDFs de requisitos), `docs/us_*`, `docs/Peer-Assessement/`, workflow de CI, `LICENSE`, 129 ficheiros de testes.
- Lacunas: índice por US, rastreabilidade, ADR, um ficheiro de teste omitido (caminho longo), avisos LF→CRLF.

## Próximas passagens

1. Docs P1 a criar: `biblioteca-ermesinde`, depois `rentacar`, `lp3-g2`, `dsos-grupo1-bombastico`, `porto-on-wheels`, `pap-gym` (`ARCHITECTURE`, `SETUP`, ADR, ER/endpoints, EN+PT).
2. P2: `apoo-lp1-companhia-aerea`, `companhia-aerea`, `lp1-2022-g2-tema1`.
3. P3: melhorar (sem reescrever) `rcomp`, `sem2`, `sem3` ×2, `sem4`.
4. Listas de melhorias (P1/P2/P3) por projeto neste ficheiro.
