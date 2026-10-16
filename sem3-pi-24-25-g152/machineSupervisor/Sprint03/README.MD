# US Assignment and Program Usage Explanation

## USAC11
**Responsável:** Tiago
### Descrição
A USAC11 consiste em implementar e montar o dispositivo da máquina para que ela possa emular as máquinas do plantfloor.

O código da máquina foi implementado no arquivo **machine.ino**, e a máquina está devidamente montada. No entanto, devido a imprevistos com membros do grupo que desistiram da disciplina e ficaram com o raspberry pi, não foi possível integrar essa lógica diretamente no programa principal.

Para evitar atrasos no projeto, foi desenvolvido o arquivo **machineEmulator.c**, que contém a função `simulate_machine`. Esta função recebe como argumento o comando (`cmd`) e retorna uma string no seguinte formato:  
`"TEMP&unit:celsius&value:%d#HUM&unit:percentage&value:%d"`

Essa abordagem permite simular o Raspberry Pi de forma eficiente enquanto os componentes físicos não estão totalmente integrados.

## USAC12
**Responsável:** Vitor Carneiro
### Descrição
A USAC12 trata da configuração inicial das estruturas de dados das máquinas utilizando um arquivo de texto. A implementação foi realizada no arquivo **machmanager.c**.

### Funções Implementadas

#### `void initialize_machine(Machine *machine, int id, const char *name, int buffer_size)`
Responsável por inicializar os dados básicos de uma máquina:
- Configura o identificador (`id`) e o nome (`name`) da máquina.
- Aloca memória para os buffers de temperatura e umidade, com tamanho definido por `buffer_size`.
- Define o estado inicial da máquina como `"OFF"`.
- Inicializa as operações e suas contagens como vazias.

#### `Machine* load_machine_setup_dynamic(const char *filename, int *count)`
Carrega a configuração das máquinas a partir de um arquivo CSV de forma dinâmica:
- Lê o arquivo especificado (`"data/machine_setup.csv"`) e utiliza `strtok` para interpretar cada linha.
- Converte valores numéricos (identificadores, ranges, tamanhos de buffer) e configura os atributos das máquinas.
- Aloca memória dinâmica para o array de máquinas, ajustando sua capacidade conforme necessário.
- Retorna um ponteiro para o array de máquinas carregadas e a quantidade delas via o parâmetro `count`.
- Em caso de erros (leitura ou alocação), libera os recursos alocados e retorna `NULL`.

#### `int carregar_configuracao(Machine **machines, int *machine_count)`
Esta função é responsável por carregar a configuração das máquinas a partir de um arquivo de configuração CSV e exibir os detalhes das máquinas carregadas.
- Utiliza a função `load_machine_setup_dynamic` para carregar as configurações das máquinas a partir do arquivo **data/machine_setup.csv**. Ela passa o ponteiro `machine_count` para que o número de máquinas carregadas seja atualizado.
- Se a carga das máquinas falhar (ou seja, se o ponteiro `machines` for `NULL`), a função exibe uma mensagem de erro `"Failed to load machine setup."` e retorna `1` para indicar falha.
- Caso contrário, a função exibe a quantidade de máquinas carregadas e os detalhes de cada máquina.
- 
### Notas Importantes
- O arquivo CSV deve seguir um formato predefinido com as seguintes colunas:
id,name,temp_min,temp_max,hum_min,hum_max,buffer_size,moving_median_window

## USAC13
**Responsável:** Vitor Carneiro
### Descrição
A USAC13 é responsável por criar um arquivo CSV contendo o histórico de operações realizadas por uma máquina específica. Para isso, utiliza-se as funções implementadas no arquivo **machmanager.c**, sendo elas **initialize_csv** e algumas funcionalidades em **log_operation**.

#### `void initialize_csv(int machine_id)`
Esta função é utilizada para criar um arquivo CSV no diretório **data**, se este não existir. O arquivo é nomeado com base no ID da máquina (por exemplo, `machine_1_operations.csv`). O arquivo é inicializado com o cabeçalho adequado para registrar as operações realizadas pela máquina.

#### Uso em `void log_operation(Operation op, PlantFloorManager *plantManager)`
Quando a função **log_operation** é chamada, ela atualiza o arquivo CSV da máquina com os detalhes de uma operação realizada. O formato da linha adicionada ao arquivo contém o ID da operação, o ID da máquina, o nome da operação, o horário de início e a duração.

## USAC14
**Responsável:** Miguel Oliveira
### Descrição
A USAC14 trata da gestão das máquinas no PlantFloor, permitindo a adição e remoção de máquinas da planta. A implementação das funções necessárias para essa gestão foi realizada no arquivo **machmanager.c**, utilizando as funções **add_machine_to_plantfloor_from_existing** e **remove_machine_from_plantfloor**.

#### `void add_machine_to_plantfloor_from_existing(Machine *existing_machines, int existing_count, Machine **plant_floor_machines, int *plant_count, int machine_id)`
Esta função permite adicionar uma máquina ao PlantFloor, copiando-a de um conjunto de máquinas existentes. Ela recebe o ID da máquina a ser adicionada e a insere no array de máquinas do PlantFloor, ajustando o contador de máquinas.

#### `void remove_machine_from_plantfloor(Machine **plant_floor_machines, int *plant_count, int machine_id)`
Esta função permite remover uma máquina do PlantFloor com base no seu ID. Antes de remover a máquina, a função verifica se ela não está em operação. Caso a máquina esteja em operação, a remoção é bloqueada para garantir que não haja interferência com os processos em andamento.

**Comportamento:**
- **Adição de Máquinas:** As máquinas existentes são copiadas para o plantfloor.
- **Remoção de Máquinas:** As máquinas só podem ser removidas se não estiverem em operação, garantindo que a remoção não afete o andamento das operações no plantfloor.
## USAC15
**Responsável:** Tiago
### Descrição
A USAC15 trata da atribuição de operações a uma máquina no PlantFloor. A implementação foi feita em dois arquivos: **ui.c** com a função **atribuir_operacao**, e **machmanager.c** com a função **log_operation**.

#### `void atribuir_operacao(Machine *machines, int machine_count, PlantFloorManager *plantManager)`
Esta função é responsável por permitir que o usuário atribua uma operação a uma máquina específica no PlantFloor. O processo ocorre em etapas:

1. **Verificação de Máquinas Disponíveis:** A função começa verificando se há máquinas registradas no **PlantFloorManager**. Caso contrário, exibe uma mensagem e retorna.
2. **Exibição das Máquinas no PlantFloor:** Em seguida, são exibidas as máquinas disponíveis, mostrando seu ID, nome e estado atual.
3. **Seleção da Máquina:** O usuário insere o ID da máquina para a qual deseja atribuir uma operação.
4. **Validação do Estado da Máquina:**
    - Caso a máquina não esteja ligada ou já esteja em operação, a função retorna com uma mensagem apropriada.
5. **Criação de Nova Operação:** Se a máquina for válida, a função cria uma nova operação com um ID único (baseado nas operações anteriores), solicita o nome da operação e sua duração.
6. **Registro da Operação e Alteração do Estado:** A operação é registrada no arquivo CSV correspondente à máquina usando a função **log_operation**, e o estado da máquina é alterado para "OP" (em operação).

#### `void log_operation(Operation op, PlantFloorManager *plantManager)`
Esta função é responsável por registrar a operação no arquivo CSV da máquina e atualizar o histórico de operações. Ela também simula a máquina com o comando correspondente, obtendo uma resposta que é processada e associada à máquina no PlantFloor.

**Comportamento:**
- A operação é atribuída a uma máquina, e o estado da máquina é alterado para "OP" para indicar que está em operação.
- A função também realiza uma verificação do estado da máquina antes de permitir que a operação seja atribuída, garantindo que a operação só seja realizada se a máquina estiver em um estado adequado.
- As informações sobre a operação são salvas em um arquivo CSV de operações, garantindo o registro do histórico de ações realizadas na máquina.

**Notas Importantes:**
- A operação é registrada com um ID único, gerado com base nas operações já existentes.
- O nome da operação e a duração são fornecidos pelo usuário, enquanto o horário de início é capturado automaticamente.

## USAC16
**Responsável:** Vitor Carneiro
### Descrição
A USAC16 envolve a exibição do estado da máquina por dois segundos ao gerenciar as operações do PlantFloor. Esta funcionalidade foi planejada para ser implementada com a utilização do Raspberry Pi, que seria responsável por fornecer as informações de estado em tempo real e exibi-las por um intervalo de dois segundos.

### Implementação Não Concluída
Devido à **falta do Raspberry Pi**, que era essencial para a resolução desta US, não foi possível implementar a funcionalidade conforme planejado. Sem o Raspberry Pi, a captura e exibição em tempo real do estado da máquina não puderam ser realizadas, o que impediu a conclusão da tarefa.

## USAC17
**Responsável:** Miguel Oliveira
### Descrição
A USAC17 tem como objetivo alimentar o sistema com uma lista de instruções a partir de um arquivo de texto, especificamente o arquivo `instructions.csv`. As instruções carregadas incluem detalhes como ID da operação, ID da máquina, nome da operação, horário de início e duração. O sistema processa essas instruções de forma ordenada, garantindo que as operações sejam executadas na sequência correta.

### Funções Utilizadas
As funções implementadas em `machmanager.c` para esta tarefa são:

- **`load_instructions`**:
    - Carrega as instruções do arquivo `instructions.csv`.
    - A função lê as linhas do arquivo CSV, processa os dados e armazena as instruções em um array dinâmico de operações.
    - Retorna o número total de instruções carregadas.

- **`carregar_e_processar_instrucoes`**:
    - A função que processa as operações carregadas, garantindo que sejam realizadas na sequência correta.
    - Primeiramente, as operações são ordenadas de acordo com o horário de início (usando uma ordenação por data).
    - A seguir, para cada operação, o sistema aguarda o horário de início especificado, verifica se a máquina está disponível e executa a operação.

### Fluxo de Execução:
1. **Carregamento de Instruções**: O arquivo `instructions.csv` é lido e as instruções são carregadas para a memória.
2. **Ordenação das Instruções**: As instruções são ordenadas pela data de início para garantir a execução na sequência correta.
3. **Execução das Instruções**: Cada operação é processada, aguardando o horário de início, verificando se a máquina está disponível e, se estiver, iniciando a operação. A operação é registrada e a máquina tem seu estado alterado.
4. **Finalização da Operação**: Após a execução da operação, a máquina volta ao estado "ON" e o próximo passo é realizado.

### Arquivo CSV:
O arquivo `instructions.csv` contém as instruções de operação, e cada linha no arquivo segue o formato:operation_id,machine_id,operation_name,start_time,duration
O sistema utiliza essas instruções para controlar o fluxo de operações no PlantFloor.

### Conclusão
Esta implementação permite que o sistema receba um conjunto de instruções externas, garantindo que as operações sejam realizadas de maneira eficiente e organizada. A leitura do arquivo CSV e o processamento das operações são fundamentais para a automação das atividades no PlantFloor.
## USAC18
**Responsável:** Vitor Carneiro
### Descrição
A USAC18 visa notificar o Product Owner caso os valores de temperatura e/ou humidade de uma máquina estejam fora dos níveis configurados. Essa funcionalidade foi implementada dentro da função `process_machine_response`.

### Contexto de Implementação
A função `process_machine_response` é responsável por processar a resposta de uma máquina. Durante esse processamento, ela realiza as verificações necessárias para identificar se os valores de temperatura ou humidade estão fora dos limites aceitáveis.

### Fluxo da Verificação:
1. **Recebimento da Resposta da Máquina**:
    - A resposta enviada pela máquina é processada para extrair os dados de temperatura e humidade.

2. **Armazenamento dos Dados**:
    - Os valores extraídos são enfileirados nos buffers correspondentes (temperatura ou humidade) da máquina.

3. **Verificação de Temperatura**:
    - Quando há elementos suficientes no buffer, é calculada a mediana dos últimos valores registrados.
    - Caso a mediana esteja fora do intervalo permitido (muito alta ou muito baixa), um alerta é gerado com informações detalhadas.

4. **Verificação de Humidade**:
    - O mesmo processo é realizado para os valores de humidade: cálculo da mediana e geração de alertas caso os limites sejam excedidos.

5. **Liberação de Memória**:
    - Após a análise, os recursos alocados dinamicamente são liberados para evitar vazamentos de memória.

### Alertas Gerados:
- **Temperatura**:
    - **Muito Alta**: Quando a mediana da temperatura excede o limite máximo.
    - **Muito Baixa**: Quando a mediana da temperatura está abaixo do limite mínimo.
- **Humidade**:
    - **Muito Alta**: Quando a mediana da humidade excede o limite máximo.
    - **Muito Baixa**: Quando a mediana da humidade está abaixo do limite mínimo.

### Conclusão
A USAC18 foi implementada de forma eficiente dentro da função `process_machine_response`. Esse método monitora continuamente as condições das máquinas, garantindo que o Product Owner seja notificado sempre que houver desvios nos parâmetros de temperatura ou humidade. Essa abordagem permite identificar e corrigir problemas de forma rápida e proativa.
