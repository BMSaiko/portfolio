# Setup — RentACar

> Documentação adicionada em 2026 (portfolio). Derivada do código-fonte e dos ficheiros de build. **Nenhum comando abaixo foi corrido**: esta máquina não tem JDK, Gradle nem acesso à base de dados da cadeira (marcado `não testado`). English version: [SETUP.md](SETUP.md).

## Pré-requisitos

| Item | Versão | Origem |
|---|---|---|
| JDK | 19 (`sourceCompatibility = '19'`) | `RentACar/build.gradle` |
| Gradle | 8.0.2 via wrapper (`./gradlew`) | `gradle/wrapper/gradle-wrapper.properties` |
| JavaFX | 19.0.2, descarregado pelo `org.openjfx.javafxplugin` | `build.gradle` |
| SQL Server | acessível a partir da tua máquina | `Database/DatabaseConnection.java` |

## Variáveis de ambiente

| Variável | Usada por | Significado |
|---|---|---|
| `RENTACAR_DB_PASSWORD` | `DatabaseConnection` | Password do utilizador SQL Server. Sem ela o URL fica com `password=null` e o login falha. |

Servidor (`ctespbd.dei.isep.ipp.pt`), base de dados (`2023_DIAS_G4_ERMESINDE`) e utilizador (`2023_DIAS_G4_ERMESINDE`) estão fixos em `DatabaseConnection.java`. Esse servidor pertencia à cadeira e provavelmente só é acessível a partir da rede ISEP ou VPN. TODO: confirmar com o BMS (se ainda está online).

## Passos

1. **Base de dados.** Usa uma base que já tenha os objetos da aplicação, ou constrói a tua:
   - `RentACar/src/main/java/olabomdia.sql` cria só as tabelas base (sem dados, procedimentos, funções ou vistas); ver [ARCHITECTURE.pt](ARCHITECTURE.pt.md#modelo-de-dados) secção B para o que falta. A aplicação não faz login sem `dbo.verify_login` e a tabela `Funcionario` (`nome`, `nivel_acesso`).
   - Aponta a ligação ao teu servidor editando o URL em `Database/DatabaseConnection.java`.
2. **Definir a password** (não testado):
   - PowerShell: `$env:RENTACAR_DB_PASSWORD = "<password>"`
   - bash: `export RENTACAR_DB_PASSWORD='<password>'`
3. **Executar** (não testado):
   ```
   cd RentACar
   ./gradlew run
   ```
   Como está versionado, deve falhar por falta da classe principal: o `build.gradle` define `mainClass = 'com.example.rentacar.HelloApplication'`, que não existe. Usa uma das alternativas:
   - Correr `com.example.rentacar.Rent_a_carApplication` a partir do IDE (o projeto já aplica o plugin JavaFX do Gradle).
   - Ou alterar localmente essa linha para `com.example.rentacar.Rent_a_carApplication` (o ficheiro original ficou intocado neste repositório).
4. **Empacotar** (não testado): `./gradlew jlink` cria uma imagem de runtime; `./gradlew jlinkZip` comprime-a para `build/distributions/` (grupo `distribution`). O lançador jlink chama-se `app`.

## Login

As credenciais vivem na base de dados (`dbo.verify_login`, tabela `Funcionario`). Nenhuma está guardada no repositório. `nivel_acesso` 1, 2 e 3+ mostram progressivamente mais do menu (ver [ARCHITECTURE.pt](ARCHITECTURE.pt.md#login-e-níveis-de-acesso)).

## Resolução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| `Could not find or load main class ...HelloApplication` / `ClassNotFoundException` | `mainClass` errada no `build.gradle` | Ver passo 3 |
| `Connection to the SQL Server database failed: ...` na consola | Servidor inacessível (fora da rede ISEP/VPN) ou password errada | Verificar rede, `RENTACAR_DB_PASSWORD`, firewall; ou apontar ao teu servidor |
| O login falha sempre mas a ligação funciona | Falta `dbo.verify_login` ou `Funcionario`, ou não há staff inserido | Criá-los (não estão no repositório) |
| `NullPointerException` depois de uma ligação falhada | `DatabaseConnection` mantém uma ligação `null` | Resolver primeiro a ligação |
| `Unsupported class file major version` / `invalid source release: 19` | JDK anterior à 19 | Instalar JDK 19+ e definir `JAVA_HOME` |
| Erros de módulo como `Module javafx.controls not found` | Execução com `java` simples em vez de Gradle/IDE | Usar `./gradlew run` ou a imagem jlink |
| Texto acentuado corrompido | Codificação do código-fonte | O build define `UTF-8`; garantir o mesmo no IDE |

## Testes

Não existem no repositório (JUnit 5 está declarado no `build.gradle`, `src/test` não existe). `./gradlew test` não correria nada.

## Notas de segurança

- Nunca fazer commit de uma password real; a original foi removida do repositório.
- Tratar a aplicação como projeto pedagógico: o input é concatenado em SQL (ver [Problemas conhecidos](ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos)).
