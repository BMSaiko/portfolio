# Project Proposals Platform (DSOS)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Plataforma web em Laravel para gerir propostas de estágio e projeto entre alunos, docentes e empresas.

> Anteriormente `dsos-grupo1-bombastico`.

| | |
|---|---|
| **Contexto** | CTESP — Desenvolvimento de Sistemas Orientados a Serviços (DSOS) |
| **Ano** | 2023/24 |
| **Equipa** | Grupo 1 |
| **Stack** | PHP · Laravel · MySQL · Blade · Bootstrap (Material Dashboard) · Composer · npm |

## O que este projeto demonstra
- **MVC com Laravel**: modelos, controllers, views Blade, migrações e rotas.
- Fluxos multi-perfil (aluno, docente, empresa) com controlo de estados.
- Desenho de base de dados e dump (`DB/`).

## Funcionalidades
- Registo, login e recuperação de password.
- Empresas/docentes publicam propostas; alunos candidatam-se, docentes candidatam-se como orientadores.
- Aceitar/rejeitar candidaturas; acompanhar estados de propostas e candidaturas.
- Entrega de projeto (submissão de ficheiros) e feedback.
- Edição de perfil, listagens de empresas e alunos, PDF da candidatura.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `DSOS-grupo1-Bombastico/` | Aplicação Laravel (`app/Models`, `app/Http/Controllers`, `routes/web.php`, migrações) |
| `DB/2023_ERM_DSOS_G1.sql` | Dump da base de dados |

## Execução
```bash
cd DSOS-grupo1-Bombastico
composer install
cp .env.example .env      # definir credenciais da BD
php artisan key:generate
php artisan migrate       # ou importar ../DB/2023_ERM_DSOS_G1.sql
php artisan serve
```

## Notas
- `vendor/` e `.env` não estão incluídos.

## Documentação

- [Auditoria da documentação](../docs/AUDIT.pt.md#project-proposals-platform) — estado e lacunas deste projeto
- [Arquitetura, modelo de dados e problemas conhecidos](docs/ARCHITECTURE.pt.md)
- [Setup e resolução de problemas](docs/SETUP.pt.md)
- [ADR 0001 — Laravel MVC, papéis por tabela](docs/adr/0001-laravel-mvc-roles-by-table.pt.md)
- [Script SQL da base de dados](DB/2023_ERM_DSOS_G1.sql)
