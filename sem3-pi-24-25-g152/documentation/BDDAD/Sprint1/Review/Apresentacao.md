# Apresentação BDDAD Sprint 1 - Notas e Recomendações

## Estrutura das User Stories (US)

- Cada **User Story (US)** deve incluir um **README** que forneça uma explicação básica sobre o que foi implementado.
- Criar uma pasta **sprint1** dentro da pasta de **Documentação** para organizar os documentos específicos desta sprint.
- Melhorar a documentação e adicionar comentários detalhados nas queries para facilitar o entendimento e a manutenção.

## Documentação Necessária

### Glossário

- Criar um **glossário** completo com termos utilizados no projeto, visando facilitar a compreensão para novos membros e interessados.

### Requisitos Não Funcionais

- Adicionar mais requisitos não funcionais, pois alguns aspectos essenciais ainda estão em falta.

## Modelo Conceptual

### Ajustes e Simplificações

- **Focar** nas tabelas mais importantes, convertendo tabelas secundárias em **campos** dentro das principais, para simplificar o modelo conceptual.
- Rever a **ligação de `productionOrder`** para garantir a consistência e clareza na representação de ordens de produção.

### Estrutura do Modelo de Produção

- Garantir que cada **produto** possua uma **árvore** que descreva a sua estrutura.
- Cada **`productionOrder`** deve estar associado a um único **produto**.
- Rever a ligação entre `Client`, `Production Order` e `Order` para garantir que todos os relacionamentos estão bem definidos.

### Revisão do Modelo e Inclusão de Novas US

- Refazer o modelo conceptual para refletir melhor os requisitos e aproveitar esta revisão para incluir elementos que serão necessários em futuras User Stories.

## Documentação de Scripts

- Documentar todos os scripts, incluindo explicações detalhadas sobre o funcionamento e propósito de cada um.
- Organizar a documentação no formato **Markdown** para manter a consistência e facilitar o acesso.

## Feedback do Professor

- **Opinião:** O trabalho apresentado está bom e corresponde aos requisitos solicitados, mas foram identificados alguns pontos de melhoria que devem ser ajustados nas próximas sprints.
