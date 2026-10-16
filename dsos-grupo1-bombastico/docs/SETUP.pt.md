# Setup — Plataforma de Propostas de Projeto (DSOS)

> Documentação adicionada em 2026 (portfolio). Derivada do código-fonte, do `composer.json` e do dump SQL. **Nenhum comando abaixo foi corrido**: esta máquina não tem PHP, Composer nem MySQL preparados para o projeto (marcado `não testado`). English version: [SETUP.md](SETUP.md).

## Pré-requisitos

| Item | Versão | Origem |
|---|---|---|
| PHP | 8.1 ou superior, com as extensões habituais do Laravel (`mbstring`, `openssl`, `pdo_mysql`, `fileinfo`, `xml`, `ctype`, `tokenizer`) | `composer.json` |
| Composer | 2.x | `composer.lock` |
| MySQL / MariaDB | qualquer versão capaz de importar o dump (exportado de MySQL 8.0.35 com `utf8mb4`) | `DB/2023_ERM_DSOS_G1.sql` |
| Node.js | não é necessário para executar: as vistas Blade usam os assets já compilados em `public/assets` e o `package.json` não tem scripts | `package.json` |

## Variáveis de ambiente

Definem-se no ficheiro `.env` criado no passo 2 (o repositório só traz `.env.example`; nunca fazer commit do `.env`).

| Variável | Significado |
|---|---|
| `APP_KEY` | Gerada por `php artisan key:generate` |
| `APP_URL` | URL base, por exemplo `http://127.0.0.1:8000` |
| `DB_CONNECTION` | `mysql` |
| `DB_HOST`, `DB_PORT`, `DB_DATABASE`, `DB_USERNAME`, `DB_PASSWORD` | A tua própria instância MySQL e credenciais |
| `MAIL_MAILER`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_FROM_ADDRESS` | Usadas pelo e-mail de reposição de password; usa `MAIL_MAILER=log` para ler a ligação em `storage/logs/laravel.log` |

O `.env.example` aponta para o servidor MySQL da cadeira; substitui esses valores pelos teus em vez de os usares.

## Passos

Todos os comandos correm a partir de `dsos-grupo1-bombastico/DSOS-grupo1-Bombastico/` (não testado).

1. **Instalar as dependências PHP**
   ```
   composer install
   ```
   O `minimum-stability` é `dev`, por isso a resolução pode escolher pacotes pré-lançamento; o `composer.lock` está versionado, usa-o.
2. **Criar o ficheiro de ambiente**
   - PowerShell: `Copy-Item .env.example .env`
   - bash: `cp .env.example .env`

   Depois edita o `.env` com a tua base de dados e corre `php artisan key:generate`.
3. **Criar a base de dados e carregar o dump** (recomendado: o dump é a fonte fiável)
   ```
   mysql -u <user> -p -e "CREATE DATABASE dsos CHARACTER SET utf8mb4;"
   mysql -u <user> -p dsos < ../DB/2023_ERM_DSOS_G1.sql
   ```
   Define `DB_DATABASE=dsos` no `.env`. O dump contém o esquema, as linhas de apoio (estados) e dados de exemplo.
   - Alternativa: `php artisan migrate` constrói o esquema a partir das migrações, mas estas não coincidem com o dump (coluna `Tipo de Trabalho` em vez de `Tipo_de_Trabalho`; sem a tabela `rel_proposta_aluno_pdf`) e não há seeders, por isso a aplicação não se comporta corretamente. Ver [ARCHITECTURE](ARCHITECTURE.pt.md#modelo-de-dados).
4. **Ligação ao armazenamento público** (ficheiros carregados; não testado): `php artisan storage:link`
5. **Executar**
   ```
   php artisan serve
   ```
   Abre `http://127.0.0.1:8000/` (página de login).

## Contas

- As empresas podem registar-se em `/registar` e têm de ser aprovadas por um admin antes de ficarem `aprovada`.
- Os alunos são criados por um admin através da importação CSV (`POST /aluno/upload`, 12 colunas: nome, data de nascimento, e-mail, NIF, país, cidade, info, telefone, imagem, username, password, turma). Como o importador guarda a password sem hash (problema conhecido #3), põe no CSV um hash bcrypt ou corrige na base de dados.
- Não há ecrã para criar admins ou docentes. Insere linhas em `acesso`, `dados_pessoais` e `admin` / `docentes` à mão; obtém o hash com `php artisan tinker` → `bcrypt('<password>')`.
- Não reutilizes contas que vêm com o dump: contém dados de aspeto real.

## Testes

`php artisan test` corre `tests/Feature` (testes de exemplo do Laravel mais `Auth` e `ProfileTest`, ao estilo Breeze) e `tests/Unit/ExampleTest.php`. Não foram corridos; os testes de autenticação foram escritos para o scaffolding por omissão do Laravel e não se espera que coincidam com o controlador `Login` personalizado. Não há CI.

## Resolução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| `No application encryption key has been specified` | Falta `APP_KEY` | `php artisan key:generate` |
| `SQLSTATE[HY000] [2002]` / `[1045]` | Servidor ou credenciais erradas; o `.env` ainda aponta para o servidor da cadeira | Corrigir `DB_*` no `.env` e `php artisan config:clear` |
| `Base table or view not found: rel_proposta_aluno_pdf` | Essa tabela não está no dump nem nas migrações | Só afeta candidaturas em PDF; problema conhecido |
| `Unknown column 'Tipo_de_Trabalho'` | Esquema criado com `migrate` em vez do dump | Importar o dump |
| Erros ao listar ou fazer cache de rotas (`php artisan route:list`, `route:cache`) | Rotas em string ao estilo antigo no fim de `routes/web.php` | Problema conhecido #8; as rotas com nome no grupo `site.` são as que funcionam |
| Não chega o e-mail de reposição | `MAIL_HOST=mailpit` não tem servidor | Correr o Mailpit ou usar `MAIL_MAILER=log` |
| Ficheiros carregados dão 404 | Falta a ligação pública | `php artisan storage:link` |
| 419 Page Expired num POST | Sessão ou cookie CSRF perdidos | Limpar cookies, `php artisan config:clear` |

## Notas de segurança

- Nunca fazer commit do `.env`; rodar qualquer credencial que tenha sido versionada (ver [ARCHITECTURE](ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos) #7).
- Manter `APP_DEBUG=false` fora de uso local.
- Apagar ou anonimizar os dados pessoais do dump antes de partilhar uma cópia.
