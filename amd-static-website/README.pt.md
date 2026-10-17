# AMD — Static Website (LP1)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Website informativo multi-página sobre a AMD: história, arquiteturas, sockets, AMD vs Intel.

> Anteriormente `lp1-2022-g2-tema1`.

| | |
|---|---|
| **Contexto** | CTESP — Laboratório de Programação 1 |
| **Ano** | 2022 |
| **Equipa** | Grupo 2 |
| **Stack** | HTML · CSS · JavaScript (no build step) |

## O que este projeto demonstra
- HTML/CSS semânticos e navegação consistente entre páginas.
- Pesquisa, estruturação e escrita de conteúdo técnico para público geral.

## Funcionalidades
- `index.html` — página inicial e créditos da equipa.
- `pages/history.html` — história da empresa e cronologia de processadores.
- `pages/arch.html` — K8, K10, Llano, Bobcat, Jaguar/Puma, Zen até Zen 4.
- `pages/sockets.html` — sockets desktop/workstation e servidores.
- `pages/amdvsintel.html` — diferenças entre Intel e AMD.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `pages/` | Páginas de conteúdo |
| `styles/` | CSS |
| `assets/` | Imagens e multimédia |

## Execução
Abrir `index.html` num browser.

## Notas
- O conteúdo do site está em português.

## Documentação

- [Arquitetura](docs/ARCHITECTURE.pt.md) — mapa do site, páginas/estilos, 9 problemas verificados
- [Setup](docs/SETUP.pt.md) — servidor local, guia de edição, restrições de deploy
- [ADR 0001](docs/adr/0001-static-html-css-no-build.pt.md) — porquê HTML/CSS estático simples
- [Auditoria da documentação](../docs/AUDIT.pt.md#amd-static-website) — estado e lacunas deste projeto
