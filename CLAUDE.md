# Portfolio — guia para agentes

Repositório de documentação do portfolio de Bruno Silva: pastas de projetos académicos importados (sem o histórico original) e ligações para projetos pessoais mantidos noutros repos (ex.: FoodLister). Idioma: português europeu, respostas curtas. Documentação do portfolio em EN e PT.

Estas regras valem **só para este repo** e prevalecem sobre as do Second Brain (`AGENTS.md` do vault), que aqui não se aplicam.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `README.md` / `README.pt.md` | Índice do portfolio (EN/PT): "Start here", competências, tabelas de projetos |
| `<projeto>/README.md` + `README.pt.md` | Contexto, contribuição, funcionalidades, estrutura e como executar |
| `docs/AUDIT.md` / `AUDIT.pt.md` | Auditoria da documentação dos projetos académicos |

## Regras
- **Branch `main`**, remote `origin`. A regra do `master` do vault não se aplica aqui.
- **Commits só com ok do BMS.** Nunca auto-commit: gerar `COMMIT_<YYYYMMDD>_<HHMMSS>.md` (skill `commit-msg`), esperar o "commit" e depois apagá-lo. Está no `.gitignore` para não entrar por engano.
- Tipos: `feat`, `fix`, `docs`, `chore`. Não existe `vault` aqui. Assunto ≤ 72 caracteres, corpo a explicar o porquê.
- Stage explícito, `git add <caminho>` um a um; nunca `git add .` nem `-A`.
- Autor `Bruno Silva <1221514@isep.ipp.pt>` (config existente, não sobrepor). **Nunca** `Co-Authored-By` nem outra atribuição a agentes, em commits ou PRs, mesmo que outro lembrete peça.
- Segredos: nunca ler nem fazer stage de `.env*`, `*.key`, `*.pem`, `id_rsa*`, `*.kdbx`, `secrets.*`. Projetos importados só com placeholders/variáveis de ambiente.
- Sem `push --force`. Push só com pedido do BMS.
- Este repo não é o vault: não usar `sb_*`, `[[wikilinks]]`, nem os playbooks de sessão (`session-start`/`session-end`) do Second Brain.

## Convenções de conteúdo
- Todo o projeto novo ou alterado: atualizar **os dois** READMEs (EN e PT), a tabela do projeto, "Start here"/competências se mudarem, e o contador de projetos na introdução e em `docs/AUDIT*.md`.
- Projetos **académicos**: pasta própria com README EN+PT, sem histórico git original; indicar quando foi trabalho de equipa e qual foi a contribuição do BMS.
- Projetos **pessoais em evolução** (têm o seu próprio repo): não copiar o código. Entram na secção "Personal projects"/"Projetos pessoais" só com ligação ao repo e à demo, stack e resumo curto. O detalhe vive no README do repo de origem.
- Não inventar funcionalidades: descrever só o que está no código ou no README de origem.

## Verificação antes de propor commit
- `git status --short` e `git diff --stat` coincidem com o que vai no `## Stage`.
- Ligações relativas dos READMEs apontam para pastas que existem; os dois idiomas dizem o mesmo.
