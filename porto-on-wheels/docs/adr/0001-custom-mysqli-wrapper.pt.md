# ADR 0001 — PHP sem framework com um wrapper mysqli próprio

> Documentação adicionada em 2026 (portfolio). Reconstruída a partir dos ficheiros do repositório; o raciocínio da equipa original não está registado, por isso as motivações abaixo estão marcadas como inferidas. English version: [0001-custom-mysqli-wrapper.md](0001-custom-mysqli-wrapper.md).

- **Estado:** Aceite (tal como implementado no repositório)
- **Evidência:** `src/database/db.php`, `src/database/conn.php`, `admin/db/*.php`, `src/database/auth.php`
- **Confirmar com o autor:** TODO: confirmar com o BMS (porquê sem framework; de onde vem a classe `db`, que se assemelha a um snippet PHP/MySQLi muito copiado)

## Contexto

O projeto web do CTESP pede um site de aluguer com catálogo público, contas de utilizador e área de administração. A equipa tinha de entregar uma aplicação PHP e MySQL funcional sem framework imposta.

## Decisão

| Preocupação | Escolha | Alternativa não tomada (inferido) |
|---|---|---|
| Estrutura da aplicação | Um ficheiro PHP por página na raiz do repositório, com cabeçalho/rodapé partilhados por include | Front controller e router, ou uma framework como o Laravel |
| Acesso a dados | Uma única classe `db` sobre `mysqli`: `query($sql, ...$params)` prepara a instrução, infere os tipos dos parâmetros (`s`, `i`, `d`, `b`) e faz o bind; `fetchAll()` / `fetchArray()` devolvem arrays associativos | PDO, um ORM |
| Configuração | O `conn.php` constrói a ligação a partir das variáveis de ambiente `PORTOONWHEELS_DB_*` (acrescentado quando o repositório foi limpo para o portfolio) | Credenciais fixas no código (o original) |
| Área de admin | Cópia do template SB Admin 2 com uma página por tabela e um `db/insert|update|delete.php` genérico com `switch` sobre o campo `table` | Controladores separados por recurso |
| Carrinho | `localStorage` do browser, enviado ao servidor em JSON no checkout | Carrinho em sessão no servidor |
| Imagens | Guardadas como BLOB na base de dados | Ficheiros em disco com uma coluna de caminho |

## Consequências

- **Bom:** muito pouco código e nenhuma ferramenta a aprender; todas as consultas usam prepared statements, logo a classe de bugs de injeção de SQL é evitada na camada de dados; o CRUD de admin é uniforme.
- **Mau:**
  - O wrapper guarda uma única instrução aberta no objeto, por isso não se podem intercalar consultas enquanto se itera um resultado; `error()` termina com a mensagem MySQL em bruto.
  - Sem front controller, o controlo de acesso tem de se repetir em cada página, e foi esquecido em `admin/db/*.php` (ver [Problemas conhecidos](../ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos) #3).
  - Passwords em texto simples e confiança em ids enviados pelo cliente (`userID`, `new_id`) mostram que a segurança não fez parte do desenho.
  - As regras de negócio (planos, disponibilidade, `quantity`) vivem no código das páginas e em listas `<option>` fixas; não há verificação de stock ao alugar.
  - O esquema não está versionado, por isso a base de dados não se recria a partir do repositório.
- **Se fosse refeito:** manter a ideia do wrapper mas passar a PDO com exceções, acrescentar um include de autenticação/autorização que termina em caso de falha, guardar passwords com `password_hash`, tirar as identidades da sessão e acrescentar um `schema.sql`. São sugestões, não alterações feitas ao projeto.
