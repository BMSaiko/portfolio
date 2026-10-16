# ADR 0001 — WPF com navegação MVVM e SQL direto nas vistas

> Documentação adicionada em 2026 (portfolio). Reconstruída a partir dos ficheiros do repositório; o raciocínio do autor original não está registado, por isso as motivações abaixo estão marcadas como inferidas. English version: [0001-wpf-mvvm-navigation-direct-sql.md](0001-wpf-mvvm-navigation-direct-sql.md).

- **Estado:** Aceite (tal como implementado no repositório)
- **Evidência:** `spump/spump/MVM/ViewModel/MainViewModel.cs`, `App.xaml`, `Core/RelayCommand.cs`, `BD.cs`, `MVM/View/*.xaml.cs`, `img/screenshoots/`
- **Confirmar com o autor:** TODO: confirmar com o BMS (se o MVVM era requisito do curso; quanto do desenho foi ensinado e quanto foi escolhido)

## Contexto

O projeto final da PAP pede um sistema completo: uma aplicação desktop para um ginásio com a sua base de dados, mais um website, entregue no 12.º ano. A aplicação gere clientes, professores, salas, modalidades, exercícios, planos de treino e aulas de grupo com inscrição.

## Decisão

| Preocupação | Escolha | Alternativa não tomada (inferido) |
|---|---|---|
| Tecnologia de interface | WPF/XAML em .NET Framework | WinForms, uma aplicação web |
| Navegação | MVVM: o `MainViewModel` expõe `CurrentView` e `RelayCommand`s; o `App.xaml` liga cada tipo de view model a um `UserControl` com um `DataTemplate` | Uma `Frame` com páginas, ou troca de janelas no code-behind |
| Lógica das vistas | Code-behind de cada vista executa SQL e preenche `DataGrid`s | View models que detêm os dados e comandos |
| Persistência | MySQL via `MySql.Data`; uma classe `BD` que constrói a string de ligação e abre/fecha a ligação | Um ORM (Entity Framework), uma camada de acesso a dados |
| Ecrãs | Uma vista de lista mais um formulário `BD*` por entidade, aberto numa janela à parte | Edição inline na grelha |
| Documentação | Capturas de todos os ecrãs e diagramas da base de dados em `img/BD` | Um relatório escrito no repositório |

## Consequências

- **Bom:** a janela principal é limpa e extensível (acrescentar um ecrã é um view model, uma vista e um `DataTemplate`); o modelo de dados é normalizado com tabelas de ligação (`clientesporaula`, `musculosporexercicios`); cada ecrã e tabela está documentado com imagens.
- **Mau:**
  - O MVVM aplica-se só à navegação. Os oito view models dos ecrãs são cascas de 12 linhas e a lógica de dados está no code-behind das vistas, por isso nada é testável por unidade e as vistas ficam presas ao MySQL.
  - SQL construído a partir de input do utilizador (injeção), sem proteção de capacidade na base de dados e uma ligação aberta por ecrã.
  - Os conceitos de negócio estão codificados no esquema de forma solta: um plano de treino não tem tabela de cabeçalho (as linhas partilham um nome) e a `quantidade` do plano é uma constante.
  - Nenhum script cria a base de dados, por isso o sistema não se reproduz só com o repositório.
- **Se fosse refeito:** manter a janela de navegação; dar a cada view model os seus dados (`ObservableCollection`) e comandos; pôr as consultas atrás de um repositório com `MySqlCommand` parametrizados; acrescentar um `schema.sql`; e mover a verificação de capacidade da sala para uma transação ou restrição. São sugestões, não alterações feitas ao projeto.
