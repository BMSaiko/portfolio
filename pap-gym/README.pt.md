# PAP Gym (Spump)

[🇬🇧 English](README.md) · 🇵🇹 Português

Projeto final de curso profissional (PAP, 12.º ano, 2021/22): sistema de gestão de ginásio composto por uma aplicação desktop e um website de apresentação.

## O que faz
- **Aplicação desktop (`spump/`)** — C# / WPF, padrão MVVM, base de dados MySQL. Gestão de clientes, professores, salas, modalidades, exercícios e aulas de grupo; edição de planos de treino; aulas do dia e horários das aulas.
- **Website (`site/`)** — página HTML/CSS estática com formulário de registo.
- **`img/`** — capturas de todos os ecrãs, diagramas da base de dados e anexos do projeto.

## Stack
C# · .NET Framework 4.7.2 · WPF/XAML · MySQL · HTML · CSS

## Como executar
1. Criar uma base de dados MySQL chamada `gym` em `localhost` (esquema nos anexos do projeto).
2. Abrir `spump/spump.sln` no Visual Studio, restaurar os pacotes NuGet e executar.
3. A connection string está em `spump/spump/BD.cs`; a password é lida da variável de ambiente `GYM_DB_PASSWORD`.

## Capturas de ecrã
Ver [`img/screenshoots`](img/screenshoots) e [`img/App`](img/App).
