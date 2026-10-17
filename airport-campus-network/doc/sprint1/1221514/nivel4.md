## NÍVEL 4 (DEPARTURES) - PROJETO FINAL

---

## 1. Infraestrutura Existente - Nível 4

### 1.1. Dados do Enunciado

| Parâmetro | Valor |
|:----------|:------|
| **Dimensões do piso** | 200m × 200m |
| **Salas com tomadas** | 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14 |
| **Salas sem tomadas** | 8 (técnica), 15, 16, 17 (técnica), 18 (técnica) |
| **Salas técnicas (para HCs)** | 8, 17, 18 |
| **Paredes exteriores com tomadas** | Direita e Inferior (1 tomada/5m) |
| **Distância vertical à vala externa** | 25 metros |
| **Pé direito / teto falso** | 5m / 4m |
| **Access Points** | 25 (distribuição uniforme no teto falso) |
| **Underfloor raceway** | 17 → 18 → 8 → 7 → 6 → 5 → 4 → 3 → 2 → 1 |

### 1.2. Infraestrutura Física Disponível

| Elemento | Localização | Descrição |
|:---------|:------------|:----------|
| **Shafts (passagens verticais)** | Sala 18 (2 shafts), Salas 8 e 17 (1 shaft cada) | Pontos com ligação entre pisos e à vala externa. |
| **Underfloor Raceway** | 17 → 18 → 8 → 7 → 6 → 5 → 4 → 3 → 2 → 1 | Canal subterrâneo que liga todas estas salas. |
| **Floor Cable Passageways** | Salas 5, 6, 7: 2 acessos; Salas 1, 2, 3, 4: 1 acesso | Acessos ao underfloor. |
| **Ligação à vala externa** | Salas 8, 17 e 18 | Conexão ao campus backbone (distância vertical: 25m). |

### 1.3. Escala e Conversões

| Parâmetro | Valor |
|:----------|:------|
| **Escala do desenho** | 3.1 cm = 50 metros |
| **Fator de conversão** | 1 cm = 16.13 metros |
| **Dimensões do piso no desenho** | ≈ 12.4 cm × 12.4 cm |

---

## 2. Cálculo de Tomadas - Nível 4

**Critérios:**
- **Interior (salas):** 2 tomadas por cada 10 m² de área.
- **Paredes Exteriores:** 1 tomada a cada 5 metros nas paredes **direita e inferior**.
- **Access Points (APs):** 25 APs distribuídos uniformemente no teto falso.
- **Salas técnicas (8, 17, 18):** Apenas tomadas para os APs (1 por sala).

| Divisão | Área Real (m²) | Cálculo (2/10m²) | Tomadas Ext. | Tomadas AP | **Total** | Status |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| Sala 1 | 255 | **51** | 0 | 0 | **51** | Com tomadas |
| Sala 2 | 255 | **51** | 0 | 0 | **51** | Com tomadas |
| Sala 3 | 219 | **44** | 0 | 0 | **44** | Com tomadas |
| Sala 4 | 219 | **44** | 0 | 0 | **44** | Com tomadas |
| Sala 5 | 561 | **112** | 0 | 0 | **112** | Com tomadas |
| Sala 6 | 468 | **94** | 0 | 0 | **94** | Com tomadas |
| Sala 7 | 515 | **103** | 0 | 0 | **103** | Com tomadas |
| **Sala 8** | 117 | **0** | 0 | 1 | **1** | **Técnica (HC-B)** |
| Sala 9 | 250 | **50** | 0 | 0 | **50** | Com tomadas |
| Sala 10 | 250 | **50** | 0 | 0 | **50** | Com tomadas |
| Sala 11 | 250 | **50** | 0 | 0 | **50** | Com tomadas |
| Sala 12 | 250 | **50** | 0 | 0 | **50** | Com tomadas |
| Sala 13 | 250 | **50** | 0 | 0 | **50** | Com tomadas |
| Sala 14 | 166 | **33** | 0 | 0 | **33** | Com tomadas |
| **Sala 15**| 532 | **0** | 0 | 0 | **0** | Sem tomadas |
| **Sala 16**| 532 | **0** | 0 | 0 | **0** | Sem tomadas |
| **Sala 17**| 128 | **0** | 0 | 1 | **1** | **Técnica (HC-A)** |
| **Sala 18**| 128 | **0** | 0 | 1 | **1** | **Técnica (MC)** |
| **Parede Direita** | 200m | - | 40 | 0 | **40** | 1 tomada/5m |
| **Parede Inferior**| 200m | - | 40 | 0 | **40** | 1 tomada/5m |
| **Teto Falso (APs)** | - | - | - | 25 | **(25)** | APs |
| **TOTAL** | | **732** | **80** | **3** | **812** | ✓ |

---

## 3. Solução Proposta - HCs e CPs

### 3.1. Localização dos Cross-connects

| Designação | Localização | Função | Shafts |
|:-----------|:------------|:-------|:-------|
| **HC-A** | Sala 17 | HC Zona Oeste | 1 shaft |
| **HC-B** | Sala 8 | HC Zona Central/Leste | 1 shaft |
| **MC** | Sala 18 | Main Cross-connect | 2 shafts |

### 3.2. Pontos de Consolidação (CPs)

| Designação | Localização | Alimentado por | Função |
|:-----------|:------------|:---------------|:--------|
| **CP-5** | Sala 5 | Fibra do HC-B (S8) | Distribuir para S5, S9, S10, S11, S12, S13, S14 |
| **CP-3** | Sala 3 | Fibra do HC-B (S8) | Distribuir para S1, S2, S3 |

### 3.3. Acessos ao Underfloor

| Sala | Acessos | Utilização |
|:-----|:--------|:-----------|
| S5 | 2 acessos | Uplink fibra + distribuição S9-14 |
| S6 | 2 acessos | Passagem de cabos |
| S7 | 2 acessos | Ligação direta ao HC-B |
| S4 | 1 acesso | Ligação ao HC-B/CP-3 |
| S3 | 1 acesso | Uplink fibra do CP-3 |
| S2 | 1 acesso | Ligação ao CP-3 |
| S1 | 1 acesso | Ligação ao CP-3 |

### 3.4. Distâncias dos Backbones de Fibra (medidas reais)

| Percurso | Medida no Desenho (cm) | Cálculo | Distância Real (m) |
|:---------|:-----------------------:|:--------|:------------------:|
| HC-A (S17) ↔ MC (S18) | 12.2 - 0.7 - 0.7 = **10.8 cm** | 10.8 × 16.13 | **174 m** |
| MC (S18) ↔ HC-B (S8) | 12.2 - 0.7 - 0.7 = **10.8 cm** | 10.8 × 16.13 | **174 m** |
| HC-B (S8) ↔ CP-5 (S5) | **4.2 cm** | 4.2 × 16.13 | **68 m** |
| HC-B (S8) ↔ CP-3 (S3) | 4.2 + 2.4 + 1.2 + 1.2 = **9.0 cm** | 9.0 × 16.13 | **145 m** |

### 3.5. Building Backbone (Fibra OM4) - Nível 4

| Percurso | Distância Real (m) | Nº Cabos | Total Fibra (m) | Função |
|:---------|:------------------:|:--------:|:---------------:|:-------|
| HC-A (S17) ↔ MC (S18) | 174 m | 2 | 348 m | Interligação HCs |
| MC (S18) ↔ HC-B (S8) | 174 m | 2 | 348 m | Interligação HCs |
| HC-B (S8) ↔ CP-5 (S5) | 68 m | 2 | 136 m | Alimentação CP-5 |
| HC-B (S8) ↔ CP-3 (S3) | 145 m | 2 | 290 m | Alimentação CP-3 |
| HC-A (S17) → Shaft | 25 m | 2 | 50 m | Ligação Vertical |
| MC (S18) → Shaft | 25 m | 2 | 50 m | Ligação Vertical |
| HC-B (S8) → Shaft | 25 m | 2 | 50 m | Ligação Vertical |
| **TOTAL FIBRA OM4** | | | **1.272 m** | |

### 3.6. Alturas e Distâncias Fixas

| Parâmetro | Valor |
|:----------|:-----:|
| Pé direito | 5m |
| Teto falso | 4m |
| Subida tomada → teto falso | 4.2m |
| Descida teto → rack | 4m |

### 3.7. Comprimento do Cabo Horizontal (CAT7) por Zona

| Zona | Tomadas Utilizador | Nº APs | Dist. Média | Total por Tomada | Total Cabo CAT7 |
|:-----|:------------------:|:------:|:-----------:|:----------------:|:---------------:|
| HC-A (direto) | 2 | 8 | 30 m | 38.2 m → **40 m** | 10 × 40 = **400 m** |
| HC-B (direto S7,S6,S4) | 242 | 9 | 45 m | 53.2 m → **55 m** | 251 × 55 = **13.805 m** |
| MC (direto + S9-14) | 324 | 8 | 40 m | 48.2 m → **50 m** | 332 × 50 = **16.600 m** |
| CP-5 (S5 + S9-14) | 395 | 0 | 45 m | 53.2 m → **55 m** | 395 × 55 = **21.725 m** |
| CP-3 (S1-3) | 146 | 0 | 40 m | 48.2 m → **50 m** | 146 × 50 = **7.300 m** |
| **TOTAL** | **1.109** | **25** | | | **59.830 m** |

---

## 4. Cobertura Wireless - Access Points (APs)

- **Total:** 25 Access Points
- **Distribuição:** Grelha 5×5, espaçamento ≈40m
- **Zonas:**
  - Zona Oeste (HC-A): ~8 APs
  - Zona Central (HC-B): ~9 APs
  - Zona Sul (MC): ~8 APs

---

## 5. Inventário de Hardware - Nível 4

### 5.1. HC-A (Sala 17)

| Item | Quantidade |
|:-----|-----------:|
| Tomadas Utilizador | 2 |
| Tomadas AP | 8 |
| Cabo Horizontal CAT7 | 400 m |
| Patch Panel Cobre | 1 |
| Patch Panel Fibra | 4 |
| Patch Cord Cobre (0.5m) | 10 |
| Patch Cord Fibra (1m) | 96 |
| Armário | Rack 12U |

### 5.2. HC-B (Sala 8)

| Item | Quantidade |
|:-----|-----------:|
| Tomadas Utilizador | 242 |
| Tomadas AP | 9 |
| Cabo Horizontal CAT7 | 13.805 m |
| Patch Panel Cobre | 6 |
| Patch Panel Fibra | 8 |
| Patch Cord Cobre (0.5m) | 251 |
| Patch Cord Fibra (1m) | 192 |
| Armário | Rack 42U |

### 5.3. MC (Sala 18)

| Item | Quantidade |
|:-----|-----------:|
| Tomadas Utilizador | 324 |
| Tomadas AP | 8 |
| Cabo Horizontal CAT7 | 16.600 m |
| Patch Panel Cobre | 7 |
| Patch Panel Fibra | 8 |
| Patch Cord Cobre (0.5m) | 332 |
| Patch Cord Fibra (1m) | 192 |
| Armário | Rack 42U |

### 5.4. CP-5 (Sala 5)

| Item | Quantidade |
|:-----|-----------:|
| Tomadas Utilizador | 395 |
| Cabo Horizontal CAT7 | 21.725 m |
| Patch Panel Cobre | 9 |
| Patch Panel Fibra | 2 |
| Patch Cord Cobre (0.5m) | 395 |
| Patch Cord Fibra (1m) | 48 |
| Armário | Rack 42U |

### 5.5. CP-3 (Sala 3)

| Item | Quantidade |
|:-----|-----------:|
| Tomadas Utilizador | 146 |
| Cabo Horizontal CAT7 | 7.300 m |
| Patch Panel Cobre | 4 |
| Patch Panel Fibra | 2 |
| Patch Cord Cobre (0.5m) | 146 |
| Patch Cord Fibra (1m) | 48 |
| Armário | Rack 24U |

---

## 6. Resumo do Nível 4

| Categoria | Quantidade |
|:----------|-----------:|
| Tomadas de Utilizador | 812 |
| Tomadas para APs | 25 |
| Access Points | 25 |
| Cabo Horizontal CAT7 | 59.830 m |
| Cabo Fibra OM4 | 1.272 m |
| Patch Panels Cobre | 27 |
| Patch Panels Fibra | 24 |
| Patch Cords Cobre (0.5m) | 1.134 |
| Patch Cords Fibra (1m) | 576 |
| Armários 42U | 2 |
| Armários 24U | 1 |
| Armários 12U | 1 |

---