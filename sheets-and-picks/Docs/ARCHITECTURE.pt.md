# Arquitetura — Sheets and Picks (LP3)

> Documentação adicionada em 2026 (portfolio). Derivada do código-fonte e do export SQL; nada aqui foi escrito pela equipa original. English version: [ARCHITECTURE.md](ARCHITECTURE.md). Os diagramas da própria equipa (casos de uso, sequência, domínio, classes, base de dados) estão nesta mesma pasta.

## Visão geral

Aplicação desktop JavaFX (Java 17, Gradle, módulo JPMS `com.example.sheetsandpicks`) para gestão de stock e encomendas. Os utilizadores entram com um papel (Admin, Operador, Fornecedor) e a janela principal mostra menus diferentes por papel. As confirmações de encomenda dos fornecedores chegam em XML, são validadas contra um XSD e guardadas através de um procedimento armazenado; depois os operadores confirmam ou rejeitam o stock recebido. A persistência é JDBC simples para SQL Server.

```mermaid
flowchart LR
    U[Utilizador] --> L[app_login.fxml<br/>loginController + LoginService]
    L -->|SELECT password FROM acesso| DB
    L -->|comparação SHA-256 em Java| L
    L --> M[main-view.fxml<br/>MainController]
    M -->|papel Admin / Operador| Users[Controladores Users<br/>Admins, Operators, Clients, Suppliers]
    M --> Stock[Controladores Stock<br/>AppDashVizualizarStock,<br/>StockConfirmationController]
    M --> XML[ConfirmaXMLController<br/>StockFunctions]
    subgraph BL["BL (lógica de negócio + acesso a dados)"]
        DBs[DBstatements<br/>984 linhas, todo o SQL]
        SF[StockFunctions<br/>leitura de XML]
    end
    Users --> DBs
    Stock --> DBs
    XML --> SF --> DBs
    DBs --> C[DBConnect<br/>ligação JDBC singleton]
    C --> DB[(SQL Server<br/>SHEETS_DB_URL)]
```

## Pacotes (`com.example.sheetsandpicks.*`)

| Pacote | Papel | Ficheiros principais |
|---|---|---|
| (raiz) | Ponto de entrada `MainApplication` (carrega `app_login.fxml`, 1280x720), ecrã de validação de XML | `MainApplication`, `ConfirmaXMLController` |
| `Controllers` | Janela principal e navegação por papel | `MainController` |
| `Controllers.app_login` | Ecrã de login e autenticação | `loginController`, `LoginService` |
| `Controllers.app_dashboard` | Ecrãs de administração de utilizadores | `app_dash_utilizadoresController`, `app_dash_updateutilizadoresController` |
| `Controllers.Users` | Um controlador por lista de papel | `AdminsController`, `OperatorsController`, `ClientsController`, `SuppliersController` |
| `Controllers.Stock` | Lista de stock e pop-up de confirmação | `AppDashVizualizarStock`, `StockConfirmationController`, `PopUp_ConfirmarStockController` |
| `Controllers.database` | Ligação JDBC singleton | `DBConnect` |
| `BL` | Todo o SQL (`BL/Database/DBstatements`), importação de XML (`BL/Stock/StockFunctions`), auxiliar de janelas (`BL/util`) | |
| `Models` | Modelos de linha para as tabelas JavaFX | `Stock`, `Encomenda`, `Admins`, `Operators`, `Clients`, `Suppliers`, `Pais` |

Recursos: 14 vistas FXML e `css/mainstyle.css` em `src/main/resources`.

## Papéis e menus

`MainController.atualizarComboUti` preenche o menu de utilizadores a partir de `permissao.Tipo`:

| Papel | Menu de utilizadores | Menu de stock |
|---|---|---|
| Admin | Clientes, Fornecedor, Operador, Admin | Ver Stock |
| Operador | Clientes, Fornecedor | Ver Stock |
| Fornecedor | Perfil | Ver Stock (vista de fornecedor) |

## Fluxos principais

### Login

1. `LoginService.verifyLogin` seleciona `acesso.password` pelo `username`, calcula o SHA-256 (hex, sem salt) da password escrita e compara as strings em Java.
2. `LoginService.getNivelAcesso` junta `acesso` → `Funcionarios` → `permissao` para obter o nome do papel.
3. `loginController` abre `main-view.fxml` e passa o papel ao `MainController`.

### Importação de encomendas em XML

```mermaid
sequenceDiagram
    participant Op as Operador
    participant UI as ConfirmaXMLController / StockFunctions
    participant XSD as XSDverificator.xsd
    participant BL as DBstatements
    participant DB as SQL Server
    Op->>UI: escolher *.xml (FileChooser)
    UI->>XSD: validar (javax.xml.validation)
    alt inválido
        UI-->>Op: diálogo de erro com a mensagem do parser
    else válido
        UI->>UI: parse DOM, uma passagem por OrderConfirmationLineItem
        UI->>BL: enviarDados(encomenda, data, fornecedor, total, país, produto, ...)
        BL->>DB: EXEC Criar_Encomenda ...
        UI-->>Op: diálogo de sucesso
    end
```

Há dois pontos de entrada para este fluxo: o ecrã `app(DEMO)XMLUpload.fxml` (`ConfirmaXMLController`) e a vista de stock (`AppDashVizualizarStock` → `StockFunctions.Verificarxml`).

O total de cada linha é `quantidade × preço unitário × 1.23` (IVA de 23% fixo em `StockFunctions`). O XSD modela uma `OrderConfirmation` (cabeçalho com referência, data de emissão e morada do fornecedor; linhas com part numbers por agência, preço, quantidade, peso líquido e contagens com unidades de medida). Exemplos de entrada: `XMLs/XML-Sample-4.2.xml` … `4.6.xml`.

### Confirmação de stock

A vista `Listar_Stock_Confirmar` alimenta o ecrã de confirmação; `DBstatements.ConfirmarStock` executa `EXEC ConfirmacaodeStock @id = ?` ou `EXEC RejeitarStock @id = ?`.

## Modelo de dados

Fonte: `Docs/drawSQL-sqlsrv-export-2023-12-02.sql` (export do drawSQL: 14 tabelas, 14 chaves estrangeiras, sem dados, sem procedimentos nem vistas). A imagem da própria equipa é `Modelo de base de dados.png`.

```mermaid
erDiagram
    Pais ||--o{ Dados_pessoais : "id_pais"
    Pais ||--o{ Encomenda : "id_pais"
    Dados_pessoais ||--o| Clientes : "id_dados"
    Dados_pessoais ||--o| Funcionarios : "id_dados"
    acesso ||--o| Funcionarios : "id_acesso"
    permissao ||--o{ Funcionarios : "id_permissao"
    Funcionarios ||--o{ Encomenda : "id_funcionario"
    Produto ||--o{ Stock : "id_produto"
    Produto ||--o{ Agencia : "id_Produto"
    Stock ||--o{ rel_UOM_stock : "id_stock"
    UOM ||--o{ rel_UOM_stock : "id_UOM"
    Stock ||--o{ rel_Informacao_UOM_Stock : "id_stock"
    UOM ||--o{ rel_Informacao_UOM_Stock : "id_UOM"
    Informacao_adicional ||--o{ rel_Informacao_UOM_Stock : "id_informacao"
    Produto { int id PK
        string Nome
        float Preco_uni
        int id_Encomenda "sem FK" }
    Stock { int id PK
        int quantidade
        string rejeitado
        int Total }
    acesso { int id PK
        string username
        string password }
    Encomenda { int id PK
        date data }
```

Observações:

- Fornecedores, operadores e admins são todos linhas de `Funcionarios` distinguidas por `permissao`; `Clientes` é uma tabela à parte. Ambas apontam para `Dados_pessoais` (nome, morada, país, e-mail, data de nascimento).
- `Produto.id_Encomenda` **não tem chave estrangeira**, logo a ligação entre encomenda e produtos é só por convenção.
- Todos os `VARCHAR NOT NULL` do export não têm comprimento; no SQL Server uma coluna `VARCHAR` sem tamanho é `VARCHAR(1)`. A base real deve ter sido criada de outra forma. TODO: confirmar com o BMS.
- O código usa também objetos que não estão no export: vistas `Listar_Stock`, `Listar_Stock_Confirmar`, `Ver_pais`; procedimentos `Criar_Funcionario`, `Alterar_Funcionario`, `Eliminar_Funcionario`, `Criar_Cliente`, `Alterar_Cliente`, `Eliminar_Cliente`, `Criar_Encomenda`, `ConfirmacaodeStock`, `RejeitarStock`.

## Dependências

De `gp2-lp3/SheetsAndPicks/build.gradle`: plugin JavaFX 0.0.13 com JavaFX `19` (`controls`, `fxml`) mais um `javafx-controls:15` explícito, ControlsFX 11.1.2, FormsFX 11.6.0, `mssql-jdbc` 9.4.0.jre8, JUnit 4.13.1, plugins `moduleplugin` e `jlink`. Gradle wrapper 8.4.

## Problemas conhecidos (verificados no código, não corrigidos)

| # | Problema | Evidência |
|---|---|---|
| 1 | Os caminhos do XSD estão fixos: `C:\Trabalho\Fichalp3\gp2-lp3\XSDverificator.xsd` (um ecrã) e `SheetsAndPicks/src/main/XSDverificator.xsd` relativo à pasta de trabalho (o outro). A validação só funciona na máquina original ou a partir de uma pasta específica. | `ConfirmaXMLController.java`, `StockFunctions.java` |
| 2 | `ConfirmarStock` define o parâmetro num ciclo mas executa uma só vez no fim, por isso só o último id é processado; compara ainda strings com `!=`. | `DBstatements.java` |
| 3 | Injeção de SQL no login e na consulta do papel (`username` concatenado). Passwords com SHA-256 sem salt. | `LoginService.java` |
| 4 | O `MainController` recorre a `app_utilizador_movimentos.fxml`, que não existe (só há `app_fornecedor_movimentos.fxml`). | `MainController.java`, `src/main/resources` |
| 5 | O único teste é uma classe vazia (`Verifylogintest`); o JUnit 4 está declarado e `useJUnitPlatform()` está comentado. | `src/test`, `build.gradle` |
| 6 | A variável `SHEETS_DB_URL` leva as credenciais dentro do URL JDBC; se faltar, lança `IllegalStateException`, que o `DBConnect` não apanha. | `DBConnect.java` |
| 7 | Higiene do repositório: caches `.gradle/`, `bin/`, um atalho `.lnk`, uma cópia `drive-download-…/` de `XMLs/` e um `com/gluonhq/.../TextField.java` perdido em `resources` estão versionados. | árvore de `sheets-and-picks/` |
