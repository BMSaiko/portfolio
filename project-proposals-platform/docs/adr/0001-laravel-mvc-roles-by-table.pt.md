# ADR 0001 — Laravel MVC com Eloquent e papéis resolvidos por pertença a tabela

> Documentação adicionada em 2026 (portfolio). Reconstruída a partir dos ficheiros do repositório; o raciocínio da equipa original não está registado, por isso as motivações abaixo estão marcadas como inferidas. English version: [0001-laravel-mvc-roles-by-table.md](0001-laravel-mvc-roles-by-table.md).

- **Estado:** Aceite (tal como implementado no repositório)
- **Evidência:** `routes/web.php`, `app/Http/Controllers/Login.php`, `app/Models/*`, `database/migrations/*`, `DB/2023_ERM_DSOS_G1.sql`
- **Confirmar com o autor:** TODO: confirmar com o BMS (porquê Laravel; porque não há coluna de papel; papel dos pacotes Inertia/Vue)

## Contexto

O enunciado de DSOS pede uma plataforma web onde empresas publicam propostas de estágio ou de projeto, docentes orientam e alunos se candidatam, com estados de aprovação e entrega de projeto. A equipa precisava de três tipos de utilizador que partilham login e dados pessoais mas diferem no que podem fazer.

## Decisão

| Preocupação | Escolha | Alternativa não tomada (inferido) |
|---|---|---|
| Framework | Laravel 10, MVC, vistas Blade renderizadas no servidor | SPA separada mais API (os pacotes Inertia/Vue estão instalados mas as vistas são Blade) |
| Persistência | Modelos Eloquent sobre MySQL, com um dump SQL como base de dados entregue | Migrações mais seeders como fonte única |
| Identidade | `acesso` (username + bcrypt) separado de `dados_pessoais` (perfil) | Uma só tabela `users` (uma tabela `users` e um modelo `User` por omissão do Laravel também permanecem) |
| Papéis | Uma tabela por papel (`aluno`, `empresa`, `docentes`, `admin`), cada uma a apontar para `acesso` e `dados_pessoais`; o papel descobre-se testando em que tabela está o utilizador | Uma coluna `role` ou um pacote de permissões |
| Estado do fluxo | Tabelas de apoio `estado_proposta` e `estado_propostas_aluno` referenciadas por id | Colunas enum |
| Onboarding em massa | Importação CSV de alunos | Auto-registo |

## Consequências

- **Bom:** cada papel guarda os seus atributos (`n_mecanografico`/`Turma` nos alunos, `diretor`/`aprovada` nas empresas, `regente` nos docentes) sem colunas anuláveis; os estados são dados, não código; a app é PHP simples sem passo de build.
- **Mau:**
  - Cada pedido que precisa do papel faz até quatro consultas para o descobrir, e a ordem das verificações (aluno, empresa, admin, docente) decide o resultado se um utilizador estiver em duas tabelas.
  - Nada impõe papéis ao nível das rotas (só `auth`), por isso a autorização depende de cada controlador e várias ações não a têm.
  - O dump e as migrações divergiram, logo há duas fontes de esquema em competição.
  - Números mágicos para estados (`id_estado = 1`) repetem-se nos controladores.
- **Se fosse refeito:** uma única resolução de papel num middleware (`role:admin`) aplicado aos grupos de rotas, `Gate`/policies para as aprovações, migrações sincronizadas com seeders e enums ou constantes para os estados. São sugestões, não alterações feitas ao projeto.
