# PAP Gym (Spump)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Gestão de ginásio: aplicação desktop WPF e website promocional.

| | |
|---|---|
| **Contexto** | Curso profissional (PAP) — projeto final |
| **Ano** | 2021/22 |
| **Equipa** | Projeto final (PAP), 12.º ano |
| **Stack** | C# · .NET Framework 4.7.2 · WPF/XAML (MVVM) · MySQL · HTML · CSS |

## O que este projeto demonstra
- Arquitetura de aplicação desktop com **MVVM** em WPF.
- Modelação relacional em MySQL (clientes, professores, salas, modalidades, exercícios, aulas) com diagramas em `img/`.
- Entrega ponta a ponta: aplicação, base de dados, website e ecrãs documentados.

## Funcionalidades
- Gestão de clientes, professores, salas, modalidades, exercícios e aulas de grupo.
- Edição de planos de treino; consulta das aulas do dia e dos horários.
- Landing page estática HTML/CSS com formulário de registo (`site/`).
- Capturas de todos os ecrãs e diagramas da base de dados em `img/`.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `spump/` | Solução WPF (Visual Studio) |
| `site/` | Website promocional |
| `img/` | Capturas, diagramas de BD, anexos |

## Execução
1. Criar uma base de dados MySQL `gym` em `localhost` (esquema nos anexos).
2. Abrir `spump/spump.sln` no Visual Studio, restaurar os pacotes NuGet e executar.
3. A password é lida da variável de ambiente `GYM_DB_PASSWORD` (`spump/spump/BD.cs`).

## Notas
- Os textos da interface estão em português.

## Documentação

- [Auditoria da documentação](../docs/AUDIT.pt.md#pap-gym) — estado e lacunas deste projeto
- [Capturas, ícones e anexos](img/)
