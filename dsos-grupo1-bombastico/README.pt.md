# DSOS Grupo 1 — Plataforma de Propostas de Projeto

[🇬🇧 English](README.md) · 🇵🇹 Português

Aplicação web em Laravel desenvolvida para a UC de Desenvolvimento de Sistemas Orientados a Serviços (ISEP, 2023/24). Gere o ciclo de vida de propostas de estágio e projeto entre **alunos, docentes e empresas**.

## O que faz
- Registo, login e recuperação de password.
- Empresas/docentes publicam **propostas**; alunos candidatam-se, docentes candidatam-se como orientadores.
- Aceitar ou rejeitar candidaturas de alunos; estados de propostas e candidaturas.
- Entrega de projetos (submissão de ficheiros) e **feedback** sobre os projetos.
- Edição de perfil, listagens de empresas e alunos, PDF de candidatura.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `DSOS-grupo1-Bombastico/` | Aplicação Laravel (`app/Models`, `app/Http/Controllers`, `routes/web.php`, migrations) |
| `DB/2023_ERM_DSOS_G1.sql` | Dump da base de dados (utilizadores, alunos, docentes, empresas, propostas, projetos, relações) |

## Stack
PHP · Laravel · MySQL · Blade · Bootstrap (Material Dashboard) · Composer · npm

## Como executar
```bash
cd DSOS-grupo1-Bombastico
composer install
cp .env.example .env      # definir credenciais da BD
php artisan key:generate
php artisan migrate       # ou importar ../DB/2023_ERM_DSOS_G1.sql
php artisan serve
```
