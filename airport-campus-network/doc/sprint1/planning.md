# Sprint 1 Planning - Grupo G2 (Turma DE)

**Data:** 2026-02-26

**Sprint Master:** Bruno Silva 1221514

**Duração do Sprint:** 2026-02-23 a 2026-03-15

## 1. Sprint Backlog Summary (T.1.1 - T.1.4)

Este sprint corresponde ao desenvolvimento do projeto de cablagem estruturada para o campus do aeroporto, abrangendo quatro terminais (T2, T3, T4, T5). O backlog é composto pelas seguintes tarefas:

- **T.1.1:** Desenvolvimento do projeto de cablagem estruturada para o Terminal 2 (pisos 1 e 4), incluindo o **campus backbone** (ligação entre edifícios).
- **T.1.2:** Desenvolvimento do projeto de cablagem estruturada para o Terminal 3 (pisos 1 e 2).
- **T.1.3:** Desenvolvimento do projeto de cablagem estruturada para o Terminal 5 (pisos 0 e 2).
- **T.1.4:** Desenvolvimento do projeto de cablagem estruturada para o Terminal 4 (pisos 0 e 2).

O objetivo é produzir, para cada edifício, plantas esquemáticas com a localização de tomadas, pontos de acesso wireless, cross-connects e cablagem, acompanhadas de cálculos, justificações e inventários de hardware.

---

## 2. Decisões Técnicas Globais (Coordenação)

De forma a garantir consistência entre os projetos desenvolvidos por cada membro da equipa e a viabilidade técnica da solução global, foram tomadas as seguintes decisões técnicas durante a reunião de planeamento.

### 2.1. Padrão de Cablagem de Cobre (568A vs 568B)

- **Decisão Adotada:** **T568B**
- **Justificação Técnica:**
    A principal diferença entre o padrão T568A e T568B é a ordem dos pares de fios (cores) no conector RJ45. Embora ambos sejam igualmente válidos e suportem as mesmas frequências (se a cablagem for de categoria adequada), o padrão T568B é o mais comum e amplamente utilizado em instalações comerciais e empresariais na Europa e América do Norte. A adoção do padrão mais comum facilita a manutenção futura e a substituição de componentes por técnicos alheios ao projeto inicial.
- **Porquê não escolher o T568A?**
    O T568A é funcionalmente idêntico e é interoperável com o T568B (desde que ambos os lados do cabo sigam o *mesmo* padrão). No entanto, é mais frequentemente encontrado em instalações governamentais dos EUA ou residenciais. Para um ambiente de aeroporto (infraestrutura crítica empresarial), o T568B é a escolha *de facto* da indústria, garantindo maior familiaridade para equipas de suporte futuras. A consistência é fundamental: todos os cabos, patch panels e tomadas no projeto seguirão o padrão T568B.

### 2.2. Tipos de Cabo para Backbone (Campus e Edifício)

- **Decisão Adotada:** **Fibra Ótica Multimodo OM4 (50/125 µm)** com cabos de **24 fibras** para todos os backbones.
- **Justificação Técnica:**
    A fibra ótica é obrigatória para backbones por várias razões: imunidade a interferência eletromagnética (EMI) - crucial num ambiente com maquinaria pesada como um aeroporto; suporte a distâncias superiores a 90/100 metros (o *campus backbone* entre terminais facilmente excede este limite); e capacidade de suportar larguras de banda futuras (40Gbps e 100Gbps) sem necessidade de substituição do cabo. A escolha da **OM4** (multimodo de alto desempenho) oferece um excelente equilíbrio entre custo e desempenho para distâncias até 400-550 metros a 10/40/100Gbps, o que é mais que suficiente para as distâncias envolvidas no projeto (o terminal maior tem 400m no eixo maior).
- **Especificação Técnica:**
    - **Tipo de Fibra:** OM4 (50/125 µm) - suporta 10/40/100Gbps até 400-550 metros.
    - **Número de Fibras por Cabo:** **24 fibras** (12 pares duplex).
    - **Justificação do Número de Fibras:**
        - O documento `TP01.pdf` recomenda "pelo menos 8 fibras para fornecer 4 ligações full-duplex paralelas".
        - A opção por 24 fibras:
            1. Permite **4 ligações ativas** (8 fibras) com **redundância 1:1** (outras 8 fibras);
            2. Deixa **8 fibras de reserva** para crescimento futuro;
            3. É um número comercial standard (patch panels de 24 fibras são comuns).
    - **Conector:** LC duplex (standard para OM4).
- **Porquê não escolher CAT7 ou CAT8 para o backbone?**
    - **CAT7 (Cobre):** Embora suporte 10Gbps até 100m, seria insuficiente para o *campus backbone* (distâncias entre edifícios >100m) e seria suscetível a EMI. Além disso, o conector GG45 ou TERRA é menos comum que o LC para fibra.
    - **CAT8 (Cobre):** Suporta 25/40Gbps, mas apenas até 30 metros. Totalmente inadequado para ligações entre edifícios.
    - **Fibra Monomodo (OS2):** É a tecnologia superior para distâncias muito longas (quilómetros), mas é mais cara (cabos e transceivers) e desnecessária para as distâncias do projeto. A OM4 é a escolha *ótima* para *data centers* e *campus* de médio porte como este.
- **Exemplo de Equipamento:**
    - **Cabo:** Cabo de fibra ótica OM4, 24 fibras, duplex LC-LC, multifibra.
    - **Patch Panel (Fibra):** Patch Panel para fibra OM4, com acopladores duplex LC, 1U para 24 fibras (ex: *Cisco FP-1U-24LC* ou equivalente genérico).
    - **Transceiver (para ativos no Sprint 2/3):** *Cisco GLC-SX-MMD* (1000BASE-SX para OM4) ou *SFP-10G-SR-X* (10GBASE-SR para OM4).

### 2.3. Redundância de Backbone (Fault Tolerance)

- **Decisão Adotada:** **Implementar caminhos fisicamente redundantes** para todos os backbones críticos.
- **Justificação Técnica:**
    A especificação do projeto para o Sprint 2 exige uma rede *"fault-tolerant"*. A tolerância a falhas começa na camada física. Se um cabo backbone for cortado durante uma obra acidental, a existência de um caminho alternativo fisicamente separado permite que os protocolos de rede (Spanning Tree no Sprint 2 ou routing dinâmico no Sprint 3) restabeleçam a comunicação automaticamente. A separação física é crucial: um caminho redundante não deve partilhar a mesma conduta ou caminho, caso contrário, um único acidente pode danificar ambos os cabos.
- **Implementação da Redundância Física:**

    Para garantir uma verdadeira tolerância a falhas, os caminhos redundantes serão **fisicamente separados**:

    1. **Campus Backbone (Vala Técnica):**
        - A vala técnica subterrânea (representada a azul nas plantas) será assumida como tendo **duas condutas independentes** (Lado A e Lado B), separadas por uma barreira física.
        - Serão instalados **2 cabos de fibra OM4 de 24 fibras**:
            - **Cabo A** na conduta A
            - **Cabo B** na conduta B
        - Esta separação garante que um acidente localizado (ex: rotura durante uma escavação) afete apenas um dos cabos.

    2. **Building Backbone (Passagens Verticais):**
        - Em cada edifício, serão utilizadas **duas caixas de passagem vertical (shafts) diferentes** para os cabos redundantes (ex: Shaft 1 e Shaft 2, identificados nas plantas).
        - Distância entre shafts: garante que um incêndio ou alagamento num shaft não afeta ambos os cabos.

    3. **Dimensionamento:**
        - Cada cabo de 24 fibras suporta até **12 ligações lógicas duplex**.
        - Com 2 cabos, existem **24 ligações lógicas disponíveis**, permitindo:
            - Ligações ativas no Cabo A
            - Ligações de reserva no Cabo B (para failover automático via protocolos como Spanning Tree ou LACP)

- **Porquê não confiar apenas num único caminho?**
    Um único cabo (ou dois cabos no mesmo duto) representa um *single point of failure* (ponto único de falha). A promessa de "tolerância a falhas" exige a eliminação destes pontos. A redundância geográfica de caminhos é a única forma de garantir continuidade de serviço perante um desastre localizado.

### 2.4. Localização do Cross-connect Principal (MC)

- **Decisão Adotada:** O **Main Cross-connect (MC)** será instalado no **Terminal 2**, na sala adequada para infraestrutura (ex: Sala 11 ou 12 do piso 1, ou sala 8, 17 ou 18 do piso 4, a definir pelo membro responsável pelo T.1.1).
- **Justificação Técnica:**
    O MC é o ponto central de distribuição do campus. A sua localização deve ser o mais central possível para minimizar distâncias de fibra e latência. Analisando a Figura 1 do enunciado, o Terminal 2 ocupa uma posição central no mapa do aeroporto, entre os Terminais 3, 4 e 5. Alojar o MC no T2 otimiza as distâncias do *campus backbone* para todos os outros terminais.
- **Porquê não escolher outro terminal (T3, T4 ou T5)?**
    - **T3 e T4:** Estão localizados nas extremidades do complexo. Colocar o MC num deles faria com que as ligações para o terminal oposto (ex: T3 para T5) fossem muito longas, embora ainda dentro do alcance da fibra, mas menos eficientes.
    - **T5:** É o maior edifício, mas está localizado numa das pontas. Serviria bem o T4, mas penalizaria as ligações ao T3.
    O T2 oferece a localização mais equilibrada e central, sendo a escolha lógica para o "coração" da rede.

### 2.5. Tipo de Cabo para Cablagem Horizontal

- **Decisão Adotada:** Cabo de Cobre **S/FTP Categoria 7 (Classe F)** .
- **Justificação Técnica:**
    A cablagem horizontal liga as tomadas dos utilizadores aos HC. Dispositivos comuns (PCs, impressoras, telefones IP) utilizam interfaces de cobre (RJ45). O padrão mais atual e com visão de futuro é o CAT7, que suporta largura de banda até 600 MHz e velocidades de 10Gbps até 100 metros (o limite da cablagem horizontal). Isto garante que a infraestrutura passiva suportará as necessidades de largura de banda por muitos anos, mesmo que os dispositivos atuais (Wi-Fi 6/7, PCs) possam não a exigir agora.

    **Porquê S/FTP em vez de U/UTP?**
    O ambiente aeroportuário é particularmente agressivo em termos de interferência eletromagnética (EMI) devido a:
    - **Sistemas de radar** (controlo de tráfego aéreo)
    - **Comunicações rádio** (torres de controlo, equipamentos de solo)
    - **Maquinaria pesada** (bagagens, manutenção)
    - **Cabos de alimentação elétrica** (paralelos aos cabos de dados)

    A construção **S/FTP (Shielded Foiled Twisted Pair)** oferece:
    1. **Blindagem global** (malha trançada) que protege contra EMI externo;
    2. **Blindagem individual por par** (folha de alumínio) que elimina a diafonia (crosstalk) entre pares;
    3. **Integridade de sinal** garantida mesmo em ambientes ruidosos;
    4. **Conformidade com normas aeroportuárias** que frequentemente exigem blindagem em cabos de dados.

    Embora a versão U/UTP seja mais flexível e de menor custo, a **S/FTP é a escolha tecnicamente superior** para a longevidade e fiabilidade da infraestrutura num aeroporto.

- **Porquê não escolher CAT6A ou Fibra para o horizontal?**
    - **CAT6A:** Suporta 10Gbps até 100m com largura de banda de 500 MHz. É uma alternativa válida, mas o CAT7 oferece uma margem de desempenho superior (600 MHz) e melhor shielding. A diferença de custo entre CAT6A e CAT7 tem vindo a diminuir, justificando a escolha pela "camada física mais avançada".
    - **Fibra Ótica:** É uma solução excelente em desempenho, mas impraticável para tomadas de utilizador final. A maioria dos dispositivos não tem porta ótica, exigindo um *media converter* ou placa de rede especial em cada tomada, o que aumenta exponencialmente o custo e a complexidade. A fibra fica reservada para os backbones.

- **Exemplo de Equipamento:**
    - **Cabo:** Cabo de cobre CAT7 S/FTP de 4 pares, LSZH (Low Smoke Zero Halogen - obrigatório em espaços públicos como aeroportos).
    - **Tomada (Outlet):** Tomada Keystone RJ45 CAT6A/CAT7 blindada, com suporte para a norma T568B.
    - **Patch Panel (Cobre):** Patch Panel de cobre CAT7 blindado, com 24 ou 48 portas RJ45, 1U ou 2U, com ligação à terra.

### 2.6. Pontos de Consolidação (CP)

**Decisão Adotada:** Utilização seletiva de Pontos de Consolidação (CP) em zonas críticas onde a distância ao Horizontal Cross-connect (HC) exceda os limites regulamentares da cablagem horizontal (80 metros em linha reta / 90 metros de comprimento de cabo).

#### 2.6.1. Contexto e Justificação Técnica

A cablagem horizontal está limitada a **90 metros de comprimento máximo por cabo** (e 80 metros em linha reta entre o HC e a tomada). Em edifícios de grande dimensão como os presentes neste projeto, esta limitação técnica impossibilita que um único HC sirva todas as tomadas e Access Points (APs) do piso.

Os Pontos de Consolidação funcionam como **pontos de interconexão intermédios passivos**, permitindo:

1. **Respeitar os limites físicos** da cablagem horizontal em cada troço (HC → CP e CP → tomada/AP);
2. **Alcançar zonas periféricas** até aproximadamente 160-170 metros do HC original;
3. **Centralizar a distribuição de cabos** em corredores principais, otimizando percursos;
4. **Garantir a cobertura Wi-Fi total** em edifícios de grande dimensão, permitindo que os APs das extremidades sejam alcançados.

#### 2.6.2. Onde serão utilizados CPs?

Após análise das plantas e dimensões de cada edifício, foram identificadas as seguintes situações que justificam a utilização de CPs:

| Cenário | Descrição | Exemplos |
| :--- | :--- | :--- |
| **Paredes externas com tomadas a cada 5 metros** | A distância linear entre o HC (geralmente central) e as extremidades das paredes externas ultrapassa os 80m em edifícios como T2, T3 e T5. | T2 (paredes direita/inferior), T3 (paredes esquerda/superior), T5 (paredes direita/superior) |
| **Access Points nas extremidades** | Para garantir cobertura Wi-Fi contínua, os APs são frequentemente colocados em zonas periféricas, longe do HC. | Todos os edifícios com Wi-Fi obrigatório |
| **Edifícios com dimensão superior a 150 metros** | Em edifícios como o T5 (400m x 150m), a distância entre extremidades opostas é muito superior ao limite da cablagem horizontal. | T5 (Piso 0 e Piso 2) |
| **Tomadas especiais em zonas remotas** | Salas localizadas nos extremos do edifício podem ficar fora do raio de ação do HC. | T4 (paredes direita/inferior no Piso 2) |

#### 2.6.3. Onde NÃO utilizar CPs?

- Em salas comuns com tomadas a menos de **80 metros do HC**;
- Em áreas técnicas sem tomadas de utilizador;
- Em zonas onde um HC adicional seja uma solução mais adequada (ex: T5, onde se recomenda **múltiplos HCs** em vez de CPs em cascata).

#### 2.6.4. Implementação Técnica dos CPs

Cada Ponto de Consolidação será implementado da seguinte forma:

| Componente | Especificação |
| :--- | :--- |
| **Armário** | Armário de parede 6U-9U (19"), instalado em teto falso ou parede de corredor, em local de fácil acesso para manutenção. |
| **Patch Panel** | Patch Panel de cobre CAT7 blindado, 24 portas RJ45, 1U, com terminação T568B e ligação à terra. |
| **Organizador** | Organizador de cabos horizontal (1U) para gestão dos patch cords. |
| **Conexões** | Todos os cabos que chegam ao CP são terminados no patch panel. A ligação entre o patch panel e os cabos de saída é feita através de **patch cords curtos (0.5m - 1m)** CAT7 S/FTP. |

#### 2.6.5. Responsabilidades Individuais

Cada membro da equipa deverá, para o(s) edifício(s) sob sua responsabilidade:

1. **Identificar nas plantas** a localização exata de cada CP necessário;
2. **Justificar cada CP** com base nas distâncias envolvidas (apresentar cálculos aproximados);
3. **Incluir os CPs no inventário de hardware** (armários, patch panels, patch cords);
4. **Representar os CPs nos esquemas de cablagem** e percursos de cabos.

#### 2.6.6. Estimativa Global de CPs

Com base na análise preliminar das dimensões de cada edifício, estima-se a seguinte distribuição de CPs:

| Edifício | Pisos | CPs Estimados | Observações |
| :--- | :--- | :--- | :--- |
| Terminal 2 | P1 + P4 | 4 (2 por piso) | Paredes direita e inferior |
| Terminal 3 | P1 + P2 | 3-4 | Paredes esquerda e superior + APs |
| Terminal 5 | P0 + P2 | *Estrutura complexa* | **Recomendação: múltiplos HCs** (análise detalhada necessária) |
| Terminal 4 | P0 + P2 | 3 | Paredes direita e inferior (P2) + zona afastada (P0) |

> **Nota:** A equipa responsável pelo Terminal 5 deverá avaliar cuidadosamente se a solução mais adequada será a utilização de múltiplos HCs em vez de CPs em cascata, dada a dimensão longitudinal do edifício (400m).

### 2.7. Dimensionamento de Armários de Telecomunicações (Sobredimensionamento)

- **Decisão Adotada:** Aplicar a regra de **sobredimensionamento de 100%** prevista no guia da aula PL01, considerando o espaço para equipamento ativo (switches) e passivo (patch panels), e arredondar para o tamanho comercial disponível imediatamente superior.
- **Justificação Técnica:**
    A norma e as boas práticas de engenharia recomendam que a infraestrutura passiva (armários) seja planeada para o futuro. O equipamento ativo (switches) ocupa aproximadamente o mesmo espaço que os patch panels que serve. Adicionar 100% de espaço extra garante que há folga para:
    1.  Crescimento futuro (novas tomadas, novos equipamentos).
    2.  Equipamentos de maior dimensão no futuro.
    3.  Instalação de outros dispositivos (UPS, gestão, etc.).
    Como a tabela de dimensionamento de armários não é linear, calcula-se a necessidade, aplica-se o fator de crescimento e escolhe-se o modelo comercial mais próximo que seja igual ou superior ao valor calculado.

---

## 3. Atribuição de Tarefas aos Membros da Equipa

De acordo com o backlog do sprint e assegurando que cada membro fica responsável por um edifício, a atribuição é a seguinte:

| Membro da Equipa (Número de Aluno) | Tarefa Atribuída | Descrição |
| :--- | :--- | :--- |
| **1221514 Bruno Silva** | **T.1.1** | Terminal 2 (Nível 1 e 4) + **Campus Backbone** (coordenação global da ligação entre edifícios e inventário global do campus). |
| **1240904 [Nome do Aluno]** | **T.1.2** | Terminal 3 (Nível 1 e 2) |
| **1241165 [Nome do Aluno]** | **T.1.3** | Terminal 5 (Nível 0 e 2) |
| **1240935 [Nome do Aluno]** | **T.1.4** | Terminal 4 (Nível 0 e 2) |

**Nota:** O Sprint Master é, para este sprint, o membro com o menor número de aluno **1221514**. As suas tarefas, para além de coordenar a equipa e criar este documento, incluem a criação do **repositório GitHub**, a **submissão final no Moodle** e a criação do **inventário global do campus** com base nos inventários individuais de cada membro.