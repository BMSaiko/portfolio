# Setup — PAP Gym (Spump)

> Documentação adicionada em 2026 (portfolio). Derivada do código-fonte e dos ficheiros do projeto. **Nenhum comando abaixo foi corrido**: esta máquina não tem Visual Studio, ferramentas de build do .NET Framework nem MySQL preparados para o projeto (marcado `não testado`). O repositório não tem script SQL, por isso o SQL do passo 2 é **reconstruído** a partir do diagrama da equipa (`img/BD/BD.PNG`) e dos `INSERT`/`SELECT` do código. English version: [SETUP.md](SETUP.md).

## Pré-requisitos

| Item | Versão | Origem |
|---|---|---|
| Windows | 10 ou superior (WPF) | `spump.csproj` (`OutputType` `WinExe`) |
| Visual Studio | 2019 ou 2022 com a carga de trabalho ".NET desktop development" | `spump.sln` |
| .NET Framework | pacote de desenvolvimento 4.8 | `spump.csproj`, `App.config` (o README diz 4.7.2) |
| MySQL Server | 8.x (ou 5.7) em `localhost`, porta 3306 por omissão | `BD.cs` |
| MySQL Connector/NET | 8.0.26 | `spump.csproj` (`MySql.Data, Version=8.0.26.0`) |

## Variáveis de ambiente

| Variável | Usada por | Significado |
|---|---|---|
| `GYM_DB_PASSWORD` | `spump/spump/BD.cs` | Password do utilizador MySQL `root`. Se não estiver definida, a password é vazia. |

Servidor (`localhost`), base de dados (`gym`) e utilizador (`root`) estão fixos em `BD.cs`. A variável é lida quando o processo arranca, por isso define-a **antes** de abrir o Visual Studio.

## Passos

Todos os comandos são `não testado`.

1. **Instalar o MySQL e o conector.** Instala o MySQL Server e o MySQL Connector/NET 8.0.26 (regista o `MySql.Data` na GAC) ou acrescenta o pacote NuGet `MySql.Data` 8.0.26 ao projeto `spump`. O `.csproj` versionado não tem `HintPath` nem `packages.config`, por isso "restaurar pacotes NuGet" sozinho não faz nada.
2. **Criar a base de dados** (reconstruída; tipos lidos do diagrama):
   ```sql
   CREATE DATABASE gym CHARACTER SET utf8mb4;
   USE gym;
   CREATE TABLE salas (NumeroSala INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(30), limite INT);
   CREATE TABLE professores (CodP INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(30),
     dataNascimento DATE, contacto VARCHAR(30), salario DECIMAL(10,2));
   CREATE TABLE modalidades (CodM INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(30), valor DECIMAL(10,2));
   CREATE TABLE clientes (CodC INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(30),
     dataNascimento DATE, email VARCHAR(50), contacto VARCHAR(30));
   CREATE TABLE exercicios (CodEx INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(50),
     series INT, repeticoes INT, descanso TIME);
   CREATE TABLE planos (CodPl INT AUTO_INCREMENT PRIMARY KEY, CodEx INT, quantidade INT, nome VARCHAR(30),
     FOREIGN KEY (CodEx) REFERENCES exercicios(CodEx));
   CREATE TABLE aulagrupo (CodAula INT AUTO_INCREMENT PRIMARY KEY, datas DATE, hora TIME,
     NumeroSala INT, CodP INT, CodM INT,
     FOREIGN KEY (NumeroSala) REFERENCES salas(NumeroSala),
     FOREIGN KEY (CodP) REFERENCES professores(CodP),
     FOREIGN KEY (CodM) REFERENCES modalidades(CodM));
   CREATE TABLE clientesporaula (CodCA INT AUTO_INCREMENT PRIMARY KEY, CodC INT, CodAula INT,
     FOREIGN KEY (CodC) REFERENCES clientes(CodC),
     FOREIGN KEY (CodAula) REFERENCES aulagrupo(CodAula));
   ```
   O diagrama tem também `musculos`, `musculosporexercicios` e `exerciciosporplano`; a aplicação nunca as usa, por isso ficam de fora. O código insere valores posicionais (`insert into ... values (null, ...)`), por isso mantém esta ordem de colunas. Não são fornecidos dados: acrescenta salas, professores e modalidades antes de marcar aulas.
3. **Definir a variável da password** (não testado):
   - PowerShell: `setx GYM_DB_PASSWORD "<password>"` (depois reinicia o Visual Studio)
   - cmd: `setx GYM_DB_PASSWORD "<password>"`
4. **Compilar e executar** (não testado): abre `spump/spump.sln` no Visual Studio e carrega em F5. Numa developer prompt: `msbuild spump\spump.sln /p:Configuration=Debug` e depois corre `spump\spump\bin\Debug\spump.exe`.
5. **Website**: abre `site/index.html` num browser (estático; precisa de internet para os assets de CDN). O formulário de registo não submete para lado nenhum.

## Testes

Nenhum (sem projeto de testes, sem CI).

## Resolução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| Erro de build `The type or namespace name 'MySql' could not be found` | Conector não instalado (problema conhecido #5) | Instalar o Connector/NET 8.0.26 ou acrescentar o pacote NuGet `MySql.Data` |
| Caixa de mensagem `Erro: MySql.Data.MySqlClient.MySqlException ... Access denied for user 'root'` | `GYM_DB_PASSWORD` errada ou em falta, ou o Visual Studio arrancou antes de ela ser definida | Definir a variável e reiniciar o Visual Studio |
| `Unknown database 'gym'` | Base de dados não criada | Passo 2 |
| `Table 'gym.salas' doesn't exist` e semelhantes | Esquema incompleto | Correr o script completo do passo 2 |
| `Column count doesn't match value count` num insert | Tabela criada com outra ordem de colunas ou colunas a mais | Igualar a ordem do passo 2 |
| Erros de autenticação com MySQL 8 e um conector antigo | `caching_sha2_password` | Usar o Connector/NET 8.0.26 ou superior |
| Listas vazias | Ainda não há linhas | Inserir dados pela aplicação ou por SQL |

## Notas de segurança

- Liga-se como `root` com TLS desativado e constrói SQL a partir de input do utilizador (ver [Problemas conhecidos](ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos) #1 e #3): usar só contra uma base local descartável.
- Nunca fazer commit de uma password real.
