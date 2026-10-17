# ADR 0001 — Validar o XML do fornecedor contra um XSD antes de o importar

> Documentação adicionada em 2026 (portfolio). Reconstruída a partir dos ficheiros do repositório; o raciocínio da equipa original não está registado, por isso as motivações abaixo estão marcadas como inferidas. English version: [0001-xsd-validated-xml-import.md](0001-xsd-validated-xml-import.md).

- **Estado:** Aceite (tal como implementado no repositório)
- **Evidência:** `gp2-lp3/XSDverificator.xsd` (cópia em `SheetsAndPicks/src/main/`), `ConfirmaXMLController.java`, `BL/Stock/StockFunctions.java`, `XMLs/XML-Sample-4.*.xml`
- **Confirmar com o autor:** TODO: confirmar com o BMS (quem definiu o formato XML: o enunciado da cadeira ou a equipa)

## Contexto

O enunciado de LP3 faz os fornecedores enviar confirmações de encomenda em ficheiros XML (`OrderConfirmation`, com cabeçalho, morada do fornecedor e linhas com part numbers, preço, quantidade e unidades de medida). A aplicação tem de os carregar para o stock sem deixar ficheiros mal formados chegar à base de dados.

## Decisão

| Preocupação | Escolha | Alternativa não tomada (inferido) |
|---|---|---|
| Contrato | Um XSD (`XSDverificator.xsd`) que descreve `OrderConfirmation` | Verificações ad hoc durante o parse |
| Validação | `javax.xml.validation` (`SchemaFactory` + `Validator`) antes de qualquer leitura | Apanhar erros de parse no ciclo DOM |
| Leitura | DOM (`DocumentBuilder`), lendo cada `OrderConfirmationLineItem` | SAX/StAX, binding JAXB |
| Persistência | Uma chamada por linha ao procedimento `Criar_Encomenda` via `DBstatements.enviarDados` | Inserção em lote numa transação |
| Feedback | Diálogos JavaFX de sucesso/erro com a mensagem do validador | Ficheiro de log |

## Consequências

- **Bom:** os ficheiros inválidos são rejeitados com uma mensagem precisa (linha e elemento) antes de tocarem na base de dados; o XSD serve também de documentação do formato; existem cinco ficheiros de exemplo em `XMLs/`.
- **Mau:**
  - O caminho do XSD está fixo em dois sítios e só um é relativo (ver [Problemas conhecidos](../ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos) #1), logo a validação depende da máquina.
  - Cada linha é uma chamada separada sem transação envolvente: uma falha na linha N deixa as linhas 1..N-1 inseridas.
  - O parser usa `getElementsByTagName(...).item(0)`, por isso assume uma forma fixa (dois `ProductIdentifier`, três `Value` por linha) que o XSD tem de continuar a impor.
  - O IVA (23%) está fixo no código Java em vez de vir da coluna `Pais.taxa`.
- **Se fosse refeito:** carregar o XSD do classpath, envolver um ficheiro numa única transação e ler a taxa de imposto da base de dados. São sugestões, não alterações feitas ao projeto.
