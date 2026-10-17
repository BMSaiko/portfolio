## NÍVEL 1 (ARRIVALS) - PROJETO FINAL

---

## 1. Infraestrutura Existente - Nível 1

### 1.1. Dados do Enunciado

| Parâmetro | Valor |
|:----------|:------|
| **Dimensões do piso** | 200m × 200m |
| **Salas com tomadas** | 1, 2, 3, 4, 5, 7, 8 |
| **Salas sem tomadas** | 6 (técnica), 9, 10, 11 (técnica), 12 (técnica) |
| **Salas técnicas (para HCs)** | 6, 11, 12 |
| **Paredes exteriores com tomadas** | Direita e Inferior (1 tomada/5m) |
| **Distância vertical à vala externa** | 10 metros |
| **Pé direito / teto falso** | 5m / 4m |
| **Access Points** | 25 (distribuição uniforme no teto falso) |
| **Underfloor raceway** | 11 → 12 → 6 → 5 → 4 → 3 → 2 → 1 |

### 1.2. Infraestrutura Física Disponível

| Elemento | Localização | Descrição |
|:---------|:------------|:----------|
| **Shafts (passagens verticais)** | Salas 6, 11 e 12 | Pontos com ligação entre pisos e à vala externa. |
| **Underfloor Raceway** | 11 → 12 → 6 → 5 → 4 → 3 → 2 → 1 | Canal subterrâneo que liga todas estas salas. |
| **Floor Cable Passageways** | Cada sala no percurso | Mínimo 2 acessos ao underfloor por sala. |
| **Ligação à vala externa** | Salas 6, 11 e 12 | Conexão ao campus backbone (distância vertical: 10m). |

### 1.3. Escala e Conversões

| Parâmetro | Valor |
|:----------|:------|
| **Escala do desenho** | 3.1 cm = 50 metros |
| **Fator de conversão** | 1 cm = 16.13 metros |
| **Dimensões do piso no desenho** | ≈ 12.4 cm × 12.4 cm |

---

## 2. Cálculo de Tomadas - Nível 1

**Critérios:**
- **Interior (salas):** 2 tomadas por cada 10 m² de área (norma ISO/IEC 11801).
- **Paredes Exteriores:** 1 tomada a cada 5 metros lineares nas paredes **direita e inferior** (conforme enunciado).
- **Access Points (APs):** Distribuição uniforme no teto falso para cobrir toda a área de 200m × 200m. Serão instalados **25 APs**, formando uma grelha aproximadamente 5 × 5.
- **Salas técnicas (6, 11, 12):** Apenas tomadas para os APs (1 por sala técnica), sem tomadas de utilizador.

| Divisão | Área Real (m²) | Cálculo (2/10m²) | Tomadas Ext. (5m) | Tomadas AP | **Total Tomadas** | Status |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| Sala 1 | 441 | (441/10) × 2 = 88.2 → **88** | 0 | 0 | **88** | Com tomadas |
| Sala 2 | 338 | (338/10) × 2 = 67.6 → **68** | 0 | 0 | **68** | Com tomadas |
| Sala 3 | 520 | (520/10) × 2 = **104** | 0 | 0 | **104** | Com tomadas |
| Sala 4 | 597 | (597/10) × 2 = 119.4 → **119** | 0 | 0 | **119** | Com tomadas |
| Sala 5 | 520 | (520/10) × 2 = **104** | 0 | 0 | **104** | Com tomadas |
| **Sala 6** | 92 | **0** (sala técnica) | 0 | 1 | **1** | **Técnica (HC-C)** |
| Sala 7 | 441 | (441/10) × 2 = 88.2 → **88** | 0 | 0 | **88** | Com tomadas |
| Sala 8 | 441 | (441/10) × 2 = 88.2 → **88** | 0 | 0 | **88** | Com tomadas |
| **Sala 9** | 469 | **0** (sem tomadas) | 0 | 0 | **0** | Sem tomadas |
| **Sala 10**| 469 | **0** (sem tomadas) | 0 | 0 | **0** | Sem tomadas |
| **Sala 11**| 128 | **0** (sala técnica) | 0 | 1 | **1** | **Técnica (HC-A)** |
| **Sala 12**| 128 | **0** (sala técnica) | 0 | 1 | **1** | **Técnica (MC)** |
| **Parede Exterior Direita** | 200m lineares | - | 200/5 = 40 | 0 | **40** | 1 tomada/5m |
| **Parede Exterior Inferior**| 200m lineares | - | 200/5 = 40 | 0 | **40** | 1 tomada/5m |
| **Teto Falso (APs)** | Cobertura total | - | - | 25 | **(25)** | Distribuição uniforme |
| **TOTAL NÍVEL 1** | | **659** | **80** | **3*** | **739** | ✓ |

*\*As 3 tomadas de AP nas salas técnicas estão incluídas na contagem das próprias salas. Os restantes 22 APs estão no teto falso.*

---

## 3. Solução Proposta - HCs e CPs

### 3.1. Localização dos Cross-connects

| Designação | Localização | Função | Shafts |
|:-----------|:------------|:-------|:-------|
| **HC-A** | Sala 11 | HC Zona Oeste + IC | 1 shaft |
| **HC-C** | Sala 6 | HC Zona Este + IC | 1 shaft |
| **MC** | Sala 12 | Main Cross-connect | 2 shafts |

### 3.2. Pontos de Consolidação (CPs)

| Designação | Localização | Alimentado por | Função |
|:-----------|:------------|:---------------|:--------|
| **CP-5** | Sala 5 | Fibra do HC-C (S6) | Distribuir para S1, S2, S3, S4, S5 |
| **CP-7** | Sala 7 | Fibra do HC-C (S6) | Distribuir para S7 |
| **CP-8** | Junto à Sala 8 | Fibra do HC-A (S11) | Distribuir para S8 |

### 3.3. Distâncias dos Backbones de Fibra (medidas reais)

| Percurso | Medida no Desenho (cm) | Cálculo | Distância Real (m) |
|:---------|:-----------------------:|:--------|:------------------:|
| HC-A (S11) ↔ MC (S12) | 12.2 - 0.7 - 0.7 = **10.8 cm** | 10.8 × 16.13 | **174 m** |
| HC-C (S6) ↔ MC (S12) | 12.2 - 0.7 - 0.7 = **10.8 cm** | 10.8 × 16.13 | **174 m** |
| HC-C (S6) ↔ CP-5 (S5) | **2.2 cm** | 2.2 × 16.13 | **35 m** |
| HC-C (S6) ↔ CP-7 (S7) | 8.2 + 4 = **12.2 cm** | 12.2 × 16.13 | **197 m** |
| HC-A (S11) ↔ CP-8 | 1.5 + 1.2 + 3 + 0.8 = **6.5 cm** | 6.5 × 16.13 | **105 m** |

### 3.4. Building Backbone (Fibra OM4) - Nível 1

| Percurso | Distância Real (m) | Nº Cabos (24 fibras) | Total Fibra (m) | Função |
|:---------|:------------------:|:---------------------:|:---------------:|:-------|
| HC-A (S11) ↔ MC (S12) | 174 m | 2 | 348 m | Interligação HCs |
| HC-C (S6) ↔ MC (S12) | 174 m | 2 | 348 m | Interligação HCs |
| HC-C (S6) ↔ CP-5 (S5) | 35 m | 2 | 70 m | Alimentação do CP-5 |
| HC-C (S6) ↔ CP-7 (S7) | 197 m | 2 | 394 m | Alimentação do CP-7 |
| HC-A (S11) ↔ CP-8 | 105 m | 2 | 210 m | Alimentação do CP-8 |
| HC-A (S11) → Shaft (vala) | 10 m | 2 | 20 m | Ligação Vertical |
| MC (S12) → Shaft (vala) | 10 m | 2 | 20 m | Ligação Vertical |
| HC-C (S6) → Shaft (vala) | 10 m | 2 | 20 m | Ligação Vertical |
| **TOTAL FIBRA OM4** | | | **1.430 m** | |

### 3.5. Alturas e Distâncias Fixas

| Parâmetro | Valor |
|:----------|:-----:|
| Pé direito | 5m |
| Teto falso (altura do chão) | 4m |
| Altura da tomada ao chão | 0.3m |
| Altura da tomada ao teto falso | 3.7m |
| Folga para manutenção | 0.5m |
| **Total subida tomada → teto falso** | **4.2m** |
| Altura da rack (fundo) | 0.5m do chão |
| Descida teto falso → fundo rack | 3.5m |
| **Total descida teto → rack** | **4m** |

### 3.6. Comprimento do Cabo Horizontal (CAT7) por Zona

| Zona | Nº Tomadas (Utilizador) | Nº APs | Dist. Média Horizontal | Total por Tomada | Total Cabo CAT7 |
|:-----|:-----------------------:|:------:|:----------------------:|:----------------:|:---------------:|
| HC-A (direto) | 21 | 8 | 30 m | 38.2 m → **40 m** | 29 × 40 = **1.160 m** |
| HC-C (direto S6 + S4?) | 41 | 9 | 40 m | 48.2 m → **50 m** | 50 × 50 = **2.500 m** |
| MC (direto) | 41 | 8 | 30 m | 38.2 m → **40 m** | 49 × 40 = **1.960 m** |
| CP-5 | 483 | 0 | 45 m | 53.2 m → **55 m** | 483 × 55 = **26.565 m** |
| CP-7 | 88 | 0 | 30 m | 38.2 m → **40 m** | 88 × 40 = **3.520 m** |
| CP-8 | 88 | 0 | 30 m | 38.2 m → **40 m** | 88 × 40 = **3.520 m** |
| **TOTAL NÍVEL 1** | **762** | **25** | | | **39.225 m** |

---

## 4. Cobertura Wireless - Access Points (APs)

- **Total:** 25 Access Points
- **Distribuição:** Grelha 5×5, espaçamento ≈40m
- **Zonas de cobertura:**
  - Zona Oeste (HC-A): ~8 APs
  - Zona Este (HC-C): ~9 APs
  - Zona Sul (MC): ~8 APs
- **Localização:** Todos no teto falso

---

## 5. Inventário de Hardware - Nível 1

### 5.1. HC-A (Sala 11)

| Item | Tipo / Especificação | Quantidade |
|:-----|:----------------------|:----------:|
| Tomadas Utilizador | Keystone CAT7 (T568B) | 21 |
| Tomadas AP | Keystone CAT7 (T568B) | 8 |
| Cabo Horizontal CAT7 | CAT7 S/FTP LSZH | 1.160 m |
| Patch Panel Cobre | CAT7 48 portas 1U | 1 |
| Patch Panel Fibra | OM4 24 portas 1U | 4 |
| Organizador de Cabos | 1U horizontal | 5 |
| Patch Cord Cobre (0.5m) | CAT7 S/FTP | 29 |
| Patch Cord Cobre (3m) | CAT7 S/FTP | 29 |
| Patch Cord Fibra (1m) | OM4 LC-LC duplex | 96 |
| Armário Telecom. | Rack 19" 18U | 1 |

### 5.2. HC-C (Sala 6)

| Item | Tipo / Especificação | Quantidade |
|:-----|:----------------------|:----------:|
| Tomadas Utilizador | Keystone CAT7 (T568B) | 41 |
| Tomadas AP | Keystone CAT7 (T568B) | 9 |
| Cabo Horizontal CAT7 | CAT7 S/FTP LSZH | 2.500 m |
| Patch Panel Cobre | CAT7 48 portas 1U | 2 |
| Patch Panel Fibra | OM4 24 portas 1U | 8 |
| Organizador de Cabos | 1U horizontal | 10 |
| Patch Cord Cobre (0.5m) | CAT7 S/FTP | 50 |
| Patch Cord Cobre (3m) | CAT7 S/FTP | 50 |
| Patch Cord Fibra (1m) | OM4 LC-LC duplex | 192 |
| Armário Telecom. | Rack 19" 24U | 1 |

### 5.3. MC (Sala 12)

| Item | Tipo / Especificação | Quantidade |
|:-----|:----------------------|:----------:|
| Tomadas Utilizador | Keystone CAT7 (T568B) | 41 |
| Tomadas AP | Keystone CAT7 (T568B) | 8 |
| Cabo Horizontal CAT7 | CAT7 S/FTP LSZH | 1.960 m |
| Patch Panel Cobre | CAT7 48 portas 1U | 1 |
| Patch Panel Fibra | OM4 24 portas 1U | 8 |
| Organizador de Cabos | 1U horizontal | 9 |
| Patch Cord Cobre (0.5m) | CAT7 S/FTP | 49 |
| Patch Cord Cobre (3m) | CAT7 S/FTP | 49 |
| Patch Cord Fibra (1m) | OM4 LC-LC duplex | 192 |
| Armário MC | Rack 19" 24U | 1 |

### 5.4. CP-5 (Sala 5)

| Item | Tipo / Especificação | Quantidade |
|:-----|:----------------------|:----------:|
| Tomadas Utilizador | Keystone CAT7 (T568B) | 483 |
| Cabo Horizontal CAT7 | CAT7 S/FTP LSZH | 26.565 m |
| Patch Panel Cobre | CAT7 48 portas 1U | 11 |
| Patch Panel Fibra | OM4 24 portas 1U | 2 |
| Organizador de Cabos | 1U horizontal | 13 |
| Patch Cord Cobre (0.5m) | CAT7 S/FTP | 483 |
| Patch Cord Cobre (3m) | CAT7 S/FTP | 483 |
| Patch Cord Fibra (1m) | OM4 LC-LC duplex | 48 |
| Armário CP | Rack 19" 42U | 1 |

### 5.5. CP-7 (Sala 7)

| Item | Tipo / Especificação | Quantidade |
|:-----|:----------------------|:----------:|
| Tomadas Utilizador | Keystone CAT7 (T568B) | 88 |
| Cabo Horizontal CAT7 | CAT7 S/FTP LSZH | 3.520 m |
| Patch Panel Cobre | CAT7 48 portas 1U | 2 |
| Patch Panel Fibra | OM4 24 portas 1U | 2 |
| Organizador de Cabos | 1U horizontal | 4 |
| Patch Cord Cobre (0.5m) | CAT7 S/FTP | 88 |
| Patch Cord Cobre (3m) | CAT7 S/FTP | 88 |
| Patch Cord Fibra (1m) | OM4 LC-LC duplex | 48 |
| Armário CP | Rack 19" 18U | 1 |

### 5.6. CP-8 (junto à Sala 8)

| Item | Tipo / Especificação | Quantidade |
|:-----|:----------------------|:----------:|
| Tomadas Utilizador | Keystone CAT7 (T568B) | 88 |
| Cabo Horizontal CAT7 | CAT7 S/FTP LSZH | 3.520 m |
| Patch Panel Cobre | CAT7 48 portas 1U | 2 |
| Patch Panel Fibra | OM4 24 portas 1U | 2 |
| Organizador de Cabos | 1U horizontal | 4 |
| Patch Cord Cobre (0.5m) | CAT7 S/FTP | 88 |
| Patch Cord Cobre (3m) | CAT7 S/FTP | 88 |
| Patch Cord Fibra (1m) | OM4 LC-LC duplex | 48 |
| Armário CP | Rack 19" 18U | 1 |

---

## 6. Resumo do Nível 1

| Categoria | Tipo | Quantidade |
|:----------|:-----|-----------:|
| **Tomadas de Utilizador** | Keystone CAT7 blindado (T568B) | 739 |
| **Tomadas para Access Points** | Keystone CAT7 blindado (T568B) | 25 |
| **Access Points** | Wi-Fi 6/7 | 25 |
| **Cabo Horizontal** | CAT7 S/FTP LSZH | 39.225 m |
| **Cabo Building Backbone** | Fibra OM4 24 fibras | 1.430 m |
| **Patch Panels Cobre** | CAT7 48 portas 1U | 19 |
| **Patch Panels Fibra** | OM4 24 portas 1U | 26 |
| **Patch Cords Cobre (0.5m)** | CAT7 S/FTP | 787 |
| **Patch Cords Cobre (3m)** | CAT7 S/FTP | 787 |
| **Patch Cords Fibra (1m)** | OM4 LC-LC duplex | 624 |
| **Armários 42U** | Rack 19" | 1 |
| **Armários 24U** | Rack 19" | 2 |
| **Armários 18U** | Rack 19" | 3 |

---