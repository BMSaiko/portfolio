RCOMP 2025-2026 Project 1 - Sprint 1 review
============================================
### Sprint master: 1221514 (Bruno Silva) ###

## 1. Sprint's backlog ##
O backlog deste sprint consistiu no desenvolvimento do projeto de cablagem estruturada para o campus aeroportuário, abrangendo quatro terminais (T2, T3, T4, T5). As tarefas foram:
- **T.1.1 (1221514):** Projeto de cablagem para o Terminal 2 (Níveis 1 e 4) e para o **campus backbone** (ligação entre edifícios), incluindo a criação do inventário global.
- **T.1.2 (1240904):** Projeto de cablagem para o Terminal 3 (Níveis 1 e 2).
- **T.1.3 (1241165):** Projeto de cablagem para o Terminal 5 (Níveis 0 e 2).
- **T.1.4 (1240935):** Projeto de cablagem para o Terminal 4 (Níveis 0 e 2).

O objetivo principal era produzir, para cada edifício, plantas esquemáticas com a localização de tomadas, pontos de acesso wireless, cross-connects e cablagem, acompanhadas de cálculos, justificações e inventários de hardware, seguindo as normas e as decisões técnicas de coordenação.

## 2. Subtasks assessment ##

### 2.1. 1221514 (Bruno Silva) - T.1.1: Structured cable design for Terminal 2 and Campus Backbone ###
#### Totally implemented with no issues. ####
O projeto para o Terminal 2 foi completamente desenvolvido, incluindo os Níveis 1 e 4. Os cálculos de tomadas para interiores e paredes exteriores estão corretos e justificados. A topologia de HCs e CPs foi bem planeada, respeitando a distância máxima de 90m para o cabo horizontal. O dimensionamento da cablagem de fibra para o building backbone e, crucialmente, para o campus backbone (incluindo as distâncias para T3, T4 e T5) foi realizado com base nas escalas fornecidas. O inventário de hardware do T2 está detalhado e a criação do inventário global consolida todos os dados da equipa. As decisões técnicas do `planning.md` (OM4, redundância de caminhos, CAT7 S/FTP) foram integralmente respeitadas.

### 2.2. 1240904 - T.1.2: Structured cable design for Terminal 3 ###
#### Totally implemented with issues. ####
O projeto para o Terminal 3 é muito detalhado e a topologia de 3 HCs no Nível 1 e 4 HCs no Nível 2 está tecnicamente correta e bem justificada para respeitar os 90 metros. O cálculo do número de tomadas (1.768) e o dimensionamento do cabo horizontal (~160km) estão de acordo com os requisitos. No entanto, existem duas questões a serem resolvidas no próximo sprint:
- **Issue 1:** O cálculo da fibra OM4 para o building backbone (HCs -> IC) não está apresentado de forma clara. A tabela na secção 4.3.2 apresenta valores que parecem inconsistentes com a redundância (ex: a ligação HC1A -> IC está com 10m, mas não multiplicada por 2 para redundância). O membro deverá refinar esta tabela e garantir que o total de fibra no inventário (listado como ~600m e ~868m) é a soma correta dos percursos redundantes.
- **Issue 2:** O inventário de hardware para o IC menciona "2 Patch Panels Fibra (Receção HCs)" para agregar as ligações de 7 HCs. Com 7 HCs e usando painéis de 24 portas (para 12 ligações duplex cada), 2 painéis são suficientes, mas a justificação deve ser mais explícita para garantir que há portas para os links principais e redundantes.

### 2.3. 1241165 - T.1.3: Structured cable design for Terminal 5 ###
#### Partially implemented with issues. ####
O projeto do Terminal 5 tem uma base sólida no que diz respeito ao cálculo do número de tomadas (2.779), o que demonstra a compreensão da dimensão do edifício. No entanto, a tarefa está incompleta, faltando secções cruciais para a avaliação, que deverão ser desenvolvidas no Sprint 2:
- **Missing Item 1:** Faltam as secções de "Implantação de Cross-connects (HC/IC/CP)" e "Tipos de Cabo". É necessário definir quantos HCs serão utilizados para cobrir um edifício de 400m, onde os localizar, e como será feita a distribuição (backbone vertical) para o Nível 2. A nota no `planning.md` sobre a necessidade de "múltiplos HCs" deve ser endereçada.
- **Missing Item 2:** Falta o dimensionamento da cablagem (cálculo de comprimentos de cabo CAT7 e de fibra OM4) e, consequentemente, o inventário de hardware completo (cabos, patch panels, armários). Sem estes cálculos, o projeto não pode ser considerado completo.
- **Issue:** O número de Access Points (APs) parece subdimensionado. A atribuição de 1 ou 2 APs por sala, sem uma grelha de cobertura para o piso todo, pode não garantir a cobertura total exigida.

### 2.4. 1240935 - T.1.4: Structured cable design for Terminal 4 ###
#### Totally implemented with no issues. ####
O projeto para o Terminal 4 está bem estruturado e completo. Os cálculos das áreas e do número de tomadas (1.350) estão claros e corretos. A topologia com um HC por piso (Salas 4 e 3) é adequada para as dimensões do edifício. O dimensionamento da cablagem horizontal está detalhado e o cálculo da fibra para o building backbone, incluindo os CPs, está apresentado. O inventário de hardware está completo e bem justificado. O projeto segue todas as diretrizes definidas no `planning.md`.
