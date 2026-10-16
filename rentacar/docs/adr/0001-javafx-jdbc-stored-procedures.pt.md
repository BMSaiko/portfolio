# ADR 0001 — Cliente desktop JavaFX com JDBC direto e lógica no SQL Server

> Documentação adicionada em 2026 (portfolio). Reconstruída a partir dos ficheiros do repositório; o raciocínio da equipa original não está registado, por isso as motivações abaixo estão marcadas como inferidas. English version: [0001-javafx-jdbc-stored-procedures.md](0001-javafx-jdbc-stored-procedures.md).

- **Estado:** Aceite (tal como implementado no repositório)
- **Evidência:** `RentACar/build.gradle`, `module-info.java`, `Database/DatabaseConnection.java`, `Menu/LoginService.java`, `Aluguer/Criar_AluguerController.java`, `src/main/resources/**/*.fxml`
- **Confirmar com o autor:** TODO: confirmar com o BMS (motivações, porque não Swing/web, quem escreveu os procedimentos armazenados)

## Contexto

O projeto de grupo do CTESP pede uma aplicação para gerir uma empresa de aluguer de automóveis: clientes, staff, veículos, alugueres, orçamentos e pagamentos, com dashboards de manutenção. A cadeira fornece uma instância SQL Server partilhada por grupo e a equipa trabalha em Java.

## Decisão

| Preocupação | Escolha | Alternativas não tomadas (inferido) |
|---|---|---|
| Interface | JavaFX 19 com vistas FXML, um controlador por ecrã | Swing (sem layout declarativo), interface web (exigiria camada de servidor) |
| Persistência | JDBC simples (`mssql-jdbc`) através de um `DatabaseConnection` singleton | JPA/Hibernate, uma camada DAO |
| Regras de negócio | Procedimentos armazenados (`Alugar_carro`, `Atualizar_aluguer`, `InserirCliente`, ...), uma função de login e vistas (`ListarVeiculo`, `ConsultarAluguer`) no SQL Server | Regras em serviços Java |
| Estado de sessão partilhado | `SingletonData` / `Controller` (padrão Singleton) | Injeção de dependências, passar o utilizador por construtores |
| Build | Gradle com os plugins de módulo, JavaFX e jlink | Maven |

## Consequências

- **Bom:** cada ecrã é pequeno e fácil de localizar (FXML + controlador); a lógica de aluguer está centralizada na base de dados; a aplicação é uma imagem única distribuível (`jlink`).
- **Mau:**
  - A base de dados guarda lógica que não está versionada: o `olabomdia.sql` versionado não tem os procedimentos, vistas e tabelas que o código chama.
  - O SQL é construído por concatenação de strings (injeção de SQL) e a ligação é um singleton partilhado sem recuperação de erros.
  - O controlo de acessos é só na interface.
  - O código Java não é testável sem uma base de dados ativa.
- **Se fosse refeito:** `PreparedStatement`/`CallableStatement` em todas as consultas, uma camada de repositórios atrás de interfaces (para testar controladores com fakes), esquema e procedimentos em scripts de migração versionados, e autorização no servidor. São sugestões, não alterações feitas ao projeto.
