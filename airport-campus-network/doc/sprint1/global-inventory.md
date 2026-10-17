# Inventário Global do Campus Aeroportuário - Sprint 1

**Data:** 2026-03-15
**Sprint Master:** Bruno Silva (1221514)

Este documento consolida os inventários de hardware de todos os terminais (T2, T3, T4, T5) e do campus backbone, conforme definido nas tarefas T.1.1 a T.1.4. Todos os valores respeitam as normas ISO/IEC 11801 e as decisões técnicas definidas no `planning.md`.

| Item | Especificação Técnica | T2 (1221514) | T3 (1240904) | T4 (1240935) | T5 (1241165) | **TOTAL GERAL** |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Tomadas Utilizador + APs** | Keystone CAT7 (T568B) | 1.576 | 1.768 | 1.350 | 2.779 | **7.473** |
| **Cabo Horizontal** | CAT7 S/FTP LSZH | 99.055 m | 160.806 m | 31.859 m | (a calcular pelo T5) | **~291.720 m** |
| **Fibra Building Backbone (OM4)** | OM4, 24 fibras (uso interno no edifício) | 2.702 m | 1.468 m | 2.303 m | (a calcular pelo T5) | **~6.473 m** |
| **Fibra Campus Backbone (OM4)** | OM4, 24 fibras (ligação MC-T2 aos outros terminais) | 5.517 m | - | - | - | **5.517 m** |
| **Patch Panel Cobre** | CAT7, 48 portas, 1U | 46 | 37 | 30 | (a calcular pelo T5) | **~113** |
| **Patch Panel Fibra (OM4)** | 24 portas (LC duplex), 1U | 50 | 9 (IC) + 4 (HCs N2) = 13 | 5 | (a calcular pelo T5) | **~68** |
| **Organizador de Cabos** | 1U, horizontal | 50 | 44 | 35 | (a calcular pelo T5) | **~129** |
| **Patch Cords Cobre (0.5m)** | CAT7 S/FTP | 1.576 | 1.768 | 1.350 | (a calcular pelo T5) | **~4.694** |
| **Patch Cords Cobre (3m)** | CAT7 S/FTP | 1.576 | 1.768 | 1.350 | (a calcular pelo T5) | **~4.694** |
| **Patch Cords Fibra (1m)** | OM4, LC-LC duplex | 1.200 | 0 (a definir) | 0 (a definir) | (a calcular pelo T5) | **~1.200** |
| **Armários 42U** | Rack 19" | 2 | 7 (3 N1 + 4 N2) | 3 (2 HCs + 1 IC) | (a calcular pelo T5) | **~12** |
| **Armários 24U** | Rack 19" | 1 | 0 | 0 | (a calcular pelo T5) | **~1** |
| **Armários 18U/12U/9U** | Rack 19" | 5 (3x18U, 1x12U, 1x9U) | 0 | 0 | (a calcular pelo T5) | **~5** |
| **Access Points (Wi-Fi)** | - | 50 | 60 | 0 (incluídos nas tomadas) | (a calcular pelo T5) | **~110** |

**Notas Consolidadas:**
- **Cabo Horizontal:** O total do T5 deverá ser adicionado para o valor final. Os valores do T2 e T3 representam a maior parte da infraestrutura de cobre.
- **Armários:** O T2 e o T3 apresentam estratégias distintas. O T2 usa mais armários de menores dimensões (CPs), enquanto o T3 concentra tudo em armários 42U nos HCs.
- **Access Points:** A contagem do T4 está incluída no valor das tomadas, seguindo a metodologia do membro. Para o inventário global, o total de tomadas já reflete esse número.