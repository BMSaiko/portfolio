# AMD — Static Website (LP1)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Multi-page informational website about AMD: history, architectures, sockets, AMD vs Intel.

| | |
|---|---|
| **Context** | CTESP — Programming Lab 1 |
| **Year** | 2022 |
| **Team** | Group 2 |
| **Stack** | HTML · CSS · JavaScript (no build step) |

## What this project shows
- Semantic HTML/CSS and consistent multi-page navigation.
- Research, structuring and writing technical content for a general audience.

## Features
- `index.html` — home and team credits.
- `pages/history.html` — company history and processor timeline.
- `pages/arch.html` — K8, K10, Llano, Bobcat, Jaguar/Puma, Zen to Zen 4.
- `pages/sockets.html` — desktop/workstation and server sockets.
- `pages/amdvsintel.html` — differences between Intel and AMD.

## Structure
| Path | Content |
|---|---|
| `pages/` | Content pages |
| `styles/` | CSS |
| `assets/` | Images and media |

## Running
Open `index.html` in a browser.

## Notes
- Site content is in Portuguese.

## Documentation

- [Architecture](docs/ARCHITECTURE.md) — site map, pages/styles, 9 verified issues
- [Setup](docs/SETUP.md) — local server, editing guide, deploy constraints
- [ADR 0001](docs/adr/0001-static-html-css-no-build.md) — why plain static HTML/CSS
- [Documentation audit](../docs/AUDIT.md#lp1-2022-g2-tema1) — status and gaps for this project
