# Setup — PAP Gym (Spump)

> Documentation added in 2026 (portfolio). Derived from the source code and project files. **No command below was run**: this machine has no Visual Studio, .NET Framework build tools or MySQL set up for the project (marked `not tested`). The repository has no SQL script, so the SQL in step 2 is **reconstructed** from the team's diagram (`img/BD/BD.PNG`) and from the `INSERT`/`SELECT` statements in the code. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Item | Version | Source |
|---|---|---|
| Windows | 10 or newer (WPF) | `spump.csproj` (`OutputType` `WinExe`) |
| Visual Studio | 2019 or 2022 with the ".NET desktop development" workload | `spump.sln` |
| .NET Framework | 4.8 developer/targeting pack | `spump.csproj`, `App.config` (the README says 4.7.2) |
| MySQL Server | 8.x (or 5.7) on `localhost`, default port 3306 | `BD.cs` |
| MySQL Connector/NET | 8.0.26 | `spump.csproj` (`MySql.Data, Version=8.0.26.0`) |

## Environment variables

| Variable | Used by | Meaning |
|---|---|---|
| `GYM_DB_PASSWORD` | `spump/spump/BD.cs` | Password of the MySQL user `root`. If unset the password is empty. |

Server (`localhost`), database (`gym`) and user (`root`) are fixed in `BD.cs`. The variable is read when the process starts, so set it **before** opening Visual Studio.

## Steps

All commands are `not tested`.

1. **Install MySQL and the connector.** Install MySQL Server and either MySQL Connector/NET 8.0.26 (it registers `MySql.Data` in the GAC) or add the NuGet package `MySql.Data` 8.0.26 to the `spump` project. The committed `.csproj` has no `HintPath` and no `packages.config`, so "restore NuGet packages" alone does nothing.
2. **Create the database** (reconstructed; types read from the diagram):
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
   The diagram also has `musculos`, `musculosporexercicios` and `exerciciosporplano`; the application never uses them, so they are left out. The code inserts positional values (`insert into ... values (null, ...)`), so keep this column order. No data is provided: add rooms, teachers and modalities before scheduling classes.
3. **Set the password variable** (not tested):
   - PowerShell: `setx GYM_DB_PASSWORD "<password>"` (then restart Visual Studio)
   - cmd: `setx GYM_DB_PASSWORD "<password>"`
4. **Build and run** (not tested): open `spump/spump.sln` in Visual Studio and press F5. From a developer prompt: `msbuild spump\spump.sln /p:Configuration=Debug`, then run `spump\spump\bin\Debug\spump.exe`.
5. **Website**: open `site/index.html` in a browser (static; needs internet for the CDN assets). The registration form does not submit anywhere.

## Tests

None (no test project, no CI).

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| Build error `The type or namespace name 'MySql' could not be found` | Connector not installed (known issue #5) | Install Connector/NET 8.0.26 or add the `MySql.Data` NuGet package |
| Message box `Erro: MySql.Data.MySqlClient.MySqlException ... Access denied for user 'root'` | Wrong or missing `GYM_DB_PASSWORD`, or Visual Studio started before it was set | Set the variable and restart Visual Studio |
| `Unknown database 'gym'` | Database not created | Step 2 |
| `Table 'gym.salas' doesn't exist` and similar | Schema incomplete | Run the whole step 2 script |
| `Column count doesn't match value count` on insert | Table created with a different column order or extra columns | Match the order in step 2 |
| Authentication errors with MySQL 8 and an old connector | `caching_sha2_password` | Use Connector/NET 8.0.26 or newer |
| Empty lists | No rows yet | Insert data through the application or SQL |

## Security notes

- Connects as `root` with TLS disabled and builds SQL from user input (see [Known issues](ARCHITECTURE.md#known-issues-verified-in-the-code-not-fixed) #1 and #3): use it only against a local throw-away database.
- Never commit a real password.
