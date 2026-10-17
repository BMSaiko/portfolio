# Setup — Sheets and Picks (LP3)

> Documentação adicionada em 2026 (portfolio). Derivada do código-fonte e dos ficheiros de build. **Nenhum comando abaixo foi corrido**: esta máquina não tem JDK nem Gradle e a base de dados da cadeira não está disponível (marcado `não testado`). English version: [SETUP.md](SETUP.md).

## Pré-requisitos

| Item | Versão | Origem |
|---|---|---|
| JDK | 17 ou superior (`JavaVersion.VERSION_17`) | `gp2-lp3/SheetsAndPicks/build.gradle` |
| Gradle | 8.4 via wrapper (`./gradlew`) | `gradle/wrapper/gradle-wrapper.properties` |
| JavaFX | descarregado pelo `org.openjfx.javafxplugin` | `build.gradle` |
| SQL Server | acessível a partir da tua máquina | `Controllers/database/DBConnect.java` |

## Variáveis de ambiente

| Variável | Usada por | Significado |
|---|---|---|
| `SHEETS_DB_URL` | `DBConnect` | URL JDBC completo **com credenciais**: `jdbc:sqlserver://<host>;databaseName=<db>;user=<user>;password=<password>;` |

Se faltar ou estiver vazia, o `DBConnect` lança `IllegalStateException("Defina a variável de ambiente SHEETS_DB_URL ...")`.

## Passos

1. **Base de dados.** `Docs/drawSQL-sqlsrv-export-2023-12-02.sql` cria apenas as 14 tabelas base. A aplicação precisa também de vistas (`Listar_Stock`, `Listar_Stock_Confirmar`, `Ver_pais`), procedimentos (`Criar_Funcionario`, `Criar_Encomenda`, `ConfirmacaodeStock`, ...) e dados iniciais (pelo menos uma linha em `permissao`, `acesso` e `Funcionarios`, ver [ARCHITECTURE.pt](ARCHITECTURE.pt.md#modelo-de-dados)). Nada disto está no repositório. TODO: confirmar com o BMS (exportar a base de dados final).
   - No export, as colunas `VARCHAR` não têm comprimento (seriam `VARCHAR(1)` no SQL Server); define comprimentos adequados antes de o correr.
2. **Criar um utilizador.** A coluna da password guarda um SHA-256 em hexadecimal, sem salt. Para o calcular (PowerShell, não testado):
   ```
   [BitConverter]::ToString([Security.Cryptography.SHA256]::Create().ComputeHash([Text.Encoding]::UTF8.GetBytes("<password>"))).Replace("-","").ToLower()
   ```
   Insere-o em `acesso.password` e liga a linha a `Funcionarios` e a um `permissao.Tipo` igual a `Admin`, `Operador` ou `Fornecedor` (grafia exata; o `MainController` compara estas strings).
3. **Definir o URL** (não testado):
   - PowerShell: `$env:SHEETS_DB_URL = "jdbc:sqlserver://localhost;databaseName=SheetsAndPicks;user=sa;password=<password>;encrypt=false;"`
   - bash: `export SHEETS_DB_URL='jdbc:sqlserver://localhost;databaseName=SheetsAndPicks;user=sa;password=<password>;encrypt=false;'`
4. **Executar** (não testado):
   ```
   cd sheets-and-picks/gp2-lp3/SheetsAndPicks
   ./gradlew run
   ```
   Classe principal: `com.example.sheetsandpicks.MainApplication` (abre o ecrã de login).
5. **Empacotar** (não testado): `./gradlew jlink` / `./gradlew jlinkZip` (zip em `build/distributions/`). O lançador chama-se `app`.

## Importação de XML

Os ficheiros de exemplo estão em `XMLs/` (`XML-Sample-4.2.xml` … `4.6.xml`). A validação lê o XSD de um local fixo no código (ver [Problemas conhecidos](ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos) #1):

- o ecrã de upload procura `C:\Trabalho\Fichalp3\gp2-lp3\XSDverificator.xsd`;
- a vista de stock procura `SheetsAndPicks/src/main/XSDverificator.xsd` relativo à pasta de trabalho do processo.

Alternativas que não alteram o código original (não testadas): copiar `gp2-lp3/XSDverificator.xsd` para esse caminho do Windows e arrancar a aplicação com `gp2-lp3` como pasta de trabalho (por exemplo uma imagem jlink executada a partir daí) para o caminho relativo resolver. O `./gradlew run` usa `SheetsAndPicks` como pasta de trabalho, onde o caminho relativo não resolve.

## Testes

`src/test/java/Verifylogintest.java` é uma classe vazia. `./gradlew test` não corre nada de útil (JUnit 4 declarado, JUnit Platform comentado).

## Resolução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| `IllegalStateException: Defina a variável de ambiente SHEETS_DB_URL` | Variável não definida no processo que lança o Gradle/IDE | Defini-la nessa shell ou na configuração de execução do IDE |
| `Connection to the SQL Server database failed: ...` | Servidor/porta errados, 1433 fechada, credenciais erradas, TLS de SQL Server recente | Verificar o URL; para uma instância local experimentar `encrypt=false;trustServerCertificate=true;` (o driver é o 9.4) |
| O login falha sempre | Sem linha em `acesso`, hash diferente ou `username` desconhecido | Recriar o utilizador com um SHA-256 em hexadecimal (passo 2) |
| O login funciona mas os menus estão vazios | `permissao.Tipo` não é exatamente `Admin` / `Operador` / `Fornecedor` | Corrigir a linha |
| Diálogo "Erro na validação do XML" com ficheiro não encontrado | Caminho do XSD (ver acima) | Usar as alternativas |
| `Module javafx.controls not found` | Execução com `java` simples | Usar Gradle/IDE ou a imagem jlink |
| Ecrã em branco / `LoadException` ao abrir a vista alternativa de movimentos de utilizador | `app_utilizador_movimentos.fxml` não existe | Problema conhecido #4 |

## Notas de segurança

- O URL JDBC leva uma password; mantém-no na shell/IDE, nunca num ficheiro versionado.
- Tratar a aplicação como projeto pedagógico: o input é concatenado em SQL e as passwords usam SHA-256 sem salt.
