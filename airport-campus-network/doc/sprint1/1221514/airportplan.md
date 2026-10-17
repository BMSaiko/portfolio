## CAMPUS BACKBONE - PROJETO FINAL

---

## 1. Infraestrutura Geral do Campus

### 1.1. Dados do Enunciado

| Parâmetro | Valor |
|:----------|:------|
| **Terminais** | T2, T3, T4, T5 |
| **Dimensões T2** | 200m × 200m |
| **Dimensões T3** | 200m × 200m |
| **Dimensões T4** | 200m × 100m |
| **Dimensões T5** | 400m × 150m (aproximado) |
| **Infraestrutura** | Vala técnica subterrânea com passagens para todos os edifícios |
| **Localização do MC** | Terminal 2 (conforme decisão no `planning.md`) |

### 1.2. Distâncias Verticais dos Terminais à Vala

| Terminal | Nível | Distância à Vala |
|:---------|:-----:|:----------------:|
| **T2** | Nível 1 | 10 m |
| **T2** | Nível 4 | 25 m |
| **T3** | Nível 1 | 10 m |
| **T3** | Nível 2 | 16 m |
| **T4** | Nível 0 | 3 m |
| **T4** | Nível 2 | 15 m |
| **T5** | Nível 0 | 3 m |
| **T5** | Nível 2 | 15 m |

---

## 2. Escala e Conversões

### 2.1. Dados da Escala

| Parâmetro | Valor |
|:----------|:------|
| **Escala do desenho** | 0.8 cm = 100 metros |
| **Fator de conversão** | 1 cm = 100 / 0.8 = **125 metros/cm** |

### 2.2. Medidas dos Terminais no Desenho

| Terminal | Medidas no Desenho (cm) | Dimensões Reais (m) | Conferência |
|:---------|:------------------------:|:-------------------:|:------------|
| **T2** | 1.6 × 1.6 cm | 1.6 × 125 = **200 m** × **200 m** | ✓ OK |
| **T3** | 1.6 × 1.6 cm | 1.6 × 125 = **200 m** × **200 m** | ✓ OK |
| **T4** | 1.6 × 0.8 cm | 1.6 × 125 = **200 m** × 0.8 × 125 = **100 m** | ✓ OK |
| **T5** | 1.2 × 3.4 cm | 1.2 × 125 = **150 m** × 3.4 × 125 = **425 m** | Aprox. 400×150 m |

---

## 3. Distâncias entre Terminais

### 3.1. Medidas no Desenho

| Percurso | Medida no Desenho (cm) | Fórmula |
|:---------|:-----------------------:|:--------|
| **T4 ↔ T2** | **4.4 cm** | Direta |
| **T2 ↔ T3** | 2 + 2.3 = **4.3 cm** | Soma |
| **T3 ↔ T5** | 1.8 + 6 + 0.9 = **8.7 cm** | Soma |

### 3.2. Distâncias Reais

| Percurso | Cálculo | Distância Real (m) |
|:---------|:--------|:------------------:|
| **T4 ↔ T2** | 4.4 cm × 125 = | **550 m** |
| **T2 ↔ T3** | 4.3 cm × 125 = | **537,5 m** |
| **T3 ↔ T5** | 8.7 cm × 125 = | **1.087,5 m** |

### 3.3. Distâncias a partir do MC (T2)

Considerando que o **MC está localizado no Terminal 2** (Sala 12, Nível 1):

| Percurso | Distância | Cálculo |
|:---------|:---------:|:--------|
| **MC (T2) ↔ T3** | **537,5 m** | T2↔T3 |
| **MC (T2) ↔ T4** | **550 m** | T2↔T4 |
| **MC (T2) ↔ T5** | **1.625 m** | T2↔T3 (537,5m) + T3↔T5 (1.087,5m) |

---

## 4. Distâncias do MC à Vala e aos Terminais

### 4.1. Distância do MC à Vala no T2

| Percurso | Distância |
|:---------|:---------:|
| MC (S12, Nível 1) → Vala técnica | **10 m** (vertical) |

### 4.2. Distância Total do MC a cada Terminal

| Terminal | Percurso | Cálculo | Distância Total |
|:---------|:---------|:--------|:---------------:|
| **T3** | MC → Vala (10m) + T2↔T3 (537,5m) + Vala→T3 (10m) | 10 + 537,5 + 10 | **557,5 m** |
| **T4** | MC → Vala (10m) + T2↔T4 (550m) + Vala→T4 (3m) | 10 + 550 + 3 | **563 m** |
| **T5** | MC → Vala (10m) + T2↔T3 (537,5m) + T3↔T5 (1.087,5m) + Vala→T5 (3m) | 10 + 537,5 + 1.087,5 + 3 | **1.638 m** |

---

## 5. Cálculo da Cablagem Campus Backbone

### 5.1. Cabos de Fibra (com Redundância)

Conforme decidido no `planning.md`, serão instalados **2 cabos de 24 fibras** por percurso, em **caminhos fisicamente separados** na vala técnica.

| Percurso | Distância | Nº Cabos (24 fibras) | Total Fibra (m) |
|:---------|:---------:|:---------------------:|:---------------:|
| **MC (T2) ↔ T3** | 557,5 m | 2 | **1.115 m** |
| **MC (T2) ↔ T4** | 563 m | 2 | **1.126 m** |
| **MC (T2) ↔ T5** | 1.638 m | 2 | **3.276 m** |
| **TOTAL FIBRA OM4 (CAMPUS)** | | | **5.517 m** |

---

## 6. Resumo por Terminal

### 6.1. Terminal 3 (T3)

| Parâmetro | Valor |
|:----------|:------|
| **Distância do MC** | 557,5 m |
| **Cabos que chegam** | 2 × 24 fibras OM4 |
| **Total de fibra (só T3)** | 1.115 m |
| **Distância vertical à vala (N1)** | 10 m |
| **Distância vertical à vala (N2)** | 16 m |
| **Shafts de entrada** | 4 shafts (conforme enunciado) |

### 6.2. Terminal 4 (T4)

| Parâmetro | Valor |
|:----------|:------|
| **Distância do MC** | 563 m |
| **Cabos que chegam** | 2 × 24 fibras OM4 |
| **Total de fibra (só T4)** | 1.126 m |
| **Distância vertical à vala (N0)** | 3 m |
| **Distância vertical à vala (N2)** | 15 m |
| **Shaft de entrada** | Sala 4 (único shaft do edifício) |

### 6.3. Terminal 5 (T5)

| Parâmetro | Valor |
|:----------|:------|
| **Distância do MC** | 1.638 m |
| **Cabos que chegam** | 2 × 24 fibras OM4 |
| **Total de fibra (só T5)** | 3.276 m |
| **Distância vertical à vala (N0)** | 3 m |
| **Distância vertical à vala (N2)** | 15 m |
| **Shafts de entrada** | 4 shafts (conforme enunciado) |

---

## 7. Justificação Técnica

### 7.1. Tipo de Fibra Escolhido

| Parâmetro | Especificação |
|:----------|:--------------|
| **Tipo de Fibra** | Multimodo OM4 (50/125 µm) |
| **Justificação** | Suporta 10/40/100 Gbps até 400-550 metros. Para a ligação ao T5 (1.638 m), será necessário equipamento ativo adequado (transceivers de longo alcance) ou considerar fibra monomodo (OS2) no futuro. A OM4 foi mantida por coerência com os backbones internos. |
| **Número de Fibras por Cabo** | 24 fibras (12 pares duplex) |
| **Justificação do Número** | 4 ligações ativas (8 fibras) + 4 de redundância (8 fibras) + 8 de reserva |

### 7.2. Redundância de Caminhos

Conforme decidido no `planning.md`:

| Requisito | Implementação |
|:----------|:--------------|
| **Caminhos fisicamente separados** | A vala técnica subterrânea será assumida com **duas condutas independentes** (Lado A e Lado B) |
| **Cabos redundantes** | 2 cabos de 24 fibras por percurso, instalados em condutas diferentes |
| **Objetivo** | Garantir tolerância a falhas (um acidente localizado afeta apenas um cabo) |

### 7.3. Pontos de Entrada nos Terminais

| Terminal | Ponto de Entrada | Distância Vertical |
|:---------|:-----------------|:------------------:|
| **T2** | Sala 12 (MC) | 10 m (N1) |
| **T3** | A definir pelo responsável T.1.2 | 10 m (N1) / 16 m (N2) |
| **T4** | Sala 4 (único shaft) | 3 m (N0) / 15 m (N2) |
| **T5** | A definir pelo responsável T.1.3 | 3 m (N0) / 15 m (N2) |

---

## 8. Inventário de Hardware - Campus Backbone

### 8.1. No MC (Sala 12, T2)

| Item | Tipo / Especificação | Quantidade | Cálculo / Justificação |
| :--- | :--- | :--- | :--- |
| **Patch Panel Fibra (para Campus)** | OM4, 24 portas (LC duplex), 1U | 6 | T3(2) + T4(2) + T5(2) |
| **Patch Cord Fibra** | OM4, LC-LC duplex, 1m | 144 | 6 painéis × 24 portas |
| **Organizador de Cabos** | 1U, horizontal | 6 | 1 por painel |

### 8.2. Em cada Terminal

| Terminal | Painéis de Fibra Necessários (para ligação ao MC) |
|:---------|:--------------------------------------------------:|
| **T3** | 2 painéis OM4 24 portas (redundância) |
| **T4** | 2 painéis OM4 24 portas (redundância) |
| **T5** | 2 painéis OM4 24 portas (redundância) |

---

## 9. Resumo do Campus Backbone

| Categoria | Tipo | Quantidade |
|:----------|:-----|-----------:|
| **Cabo Campus Backbone** | Fibra OM4 24 fibras | **5.517 m** |
| **Patch Panels Fibra (no MC)** | OM4 24 portas 1U | 6 |
| **Patch Panels Fibra (nos terminais)** | OM4 24 portas 1U | 6 |
| **Patch Cords Fibra (1m)** | OM4 LC-LC duplex | 288 |
| **Organizadores de Cabos** | 1U horizontal | 12 |

---

## 10. Conformidade com as Decisões do Planning.md

| Decisão | Verificação |
|:--------|:------------|
| ✅ **Fibra OM4 para backbones** | Todos os cabos campus são OM4 24 fibras |
| ✅ **Redundância de caminhos** | 2 cabos por percurso em condutas separadas |
| ✅ **MC no Terminal 2** | Todas as distâncias calculadas a partir do T2 |
| ✅ **Sobredimensionamento** | 24 fibras por cabo (muito acima do mínimo de 8) |

---

## 11. Notas para os Responsáveis Individuais

### Para o responsável do **T3 (T.1.2)**:
- O cabo campus chega ao T3 com **2 × 24 fibras** (total 48 fibras)
- Distância do MC: **557,5 m**
- Distância vertical a considerar: **10 m** (N1) ou **16 m** (N2)
- Necessário prever **2 patch panels de fibra OM4** no IC do T3
- Os 4 shafts existentes permitem redundância interna

### Para o responsável do **T4 (T.1.4)**:
- O cabo campus chega ao T4 com **2 × 24 fibras** (total 48 fibras)
- Distância do MC: **563 m**
- Distância vertical a considerar: **3 m** (N0) ou **15 m** (N2)
- Ponto de entrada obrigatório: **Sala 4** (único shaft do edifício)
- Necessário prever **2 patch panels de fibra OM4** no IC do T4

### Para o responsável do **T5 (T.1.3)**:
- O cabo campus chega ao T5 com **2 × 24 fibras** (total 48 fibras)
- Distância do MC: **1.638 m** (a mais longa)
- Distância vertical a considerar: **3 m** (N0) ou **15 m** (N2)
- Necessário prever **2 patch panels de fibra OM4** no IC do T5
- Os 4 shafts existentes permitem redundância interna
- **Atenção:** A distância de 1.638 m excede o alcance recomendado para OM4 a 100 Gbps. Recomendar transceivers de longo alcance ou considerar futura atualização para fibra monomodo (OS2) se necessário.

---

## 12. Conclusão

O campus backbone foi dimensionado com base nas **medidas exatas fornecidas** e respeita todas as decisões técnicas do `planning.md`:

| Parâmetro | Valor |
|:----------|:------|
| **Fibra utilizada** | OM4 24 fibras (2 cabos por percurso) |
| **Distância T2-T3** | 557,5 m |
| **Distância T2-T4** | 563 m |
| **Distância T2-T5** | 1.638 m |
| **Total de fibra** | **5.517 m** |
| **Redundância** | ✓ Caminhos fisicamente separados |
| **Capacidade futura** | 40/100 Gbps com equipamento adequado |

A infraestrutura está preparada para suportar as necessidades atuais e futuras do campus aeroportuário, garantindo **tolerância a falhas** e **escalabilidade**.

---