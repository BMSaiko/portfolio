# Setup — AMD static website (LP1)

> Documentation added in 2026 (portfolio). The local-server commands below were **run** on 2026-10-17 (Python 3, Windows); the deploy section is guidance, not something the team did. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Tool | Notes |
|---|---|
| A web browser | Any current one |
| Python 3 (optional) | Only for the local server; any static server works |

No build step, package manager, database, environment variables or secrets. The page loads Font Awesome (cdnjs) and Poppins (Google Fonts) from the internet, so icons and the font need a connection.

## Run locally

Serve the project folder **from its root** (see why below):

```bash
cd amd-static-website
python -m http.server 8000
# open http://localhost:8000/
```

Result when tested: `/`, `/pages/history.html`, `/pages/arch.html`, `/pages/sockets.html`, `/pages/amdvsintel.html` and `/styles/styles.css` returned 200; `/pages/amdvsintel` (the link used on the home page, without `.html`) returned 404.

### Why not just open `index.html`?

The README says to open the file directly. The home page does open, but the navigation uses root-absolute URLs (`/pages/history.html`, `/styles/styles.css`), which on `file://` point to the drive root, so links and styles break. Use a local server. Details: [ARCHITECTURE §5](ARCHITECTURE.md#5-verified-issues-read-from-the-code), issues 1–3.

## Editing guide

| To change | Edit |
|---|---|
| Text of a page | The matching file in `pages/` (or `index.html`) |
| Look of one page group | `styles/<group>/…css` (`history.css`, `arch/`, `sockets/`, `amdintel/`) |
| Colours/fonts shared by the home page and most pages | `styles/styles.css` (`arch.html` uses its own `styles/arch/styles.css`) |
| Navigation | **All 5 HTML files** — the nav is copy-pasted, there is no include |
| Images | `assets/imgs/…`; keep the file-name case identical to the `src` (issue 4) |

## Deploy

There is no deploy configuration in the repository. Any static host works (GitHub Pages, Netlify, an Apache/Nginx folder). Constraints from the code:

- Serve at the **domain root** (for example a user/organisation Pages site or a custom domain). Under a sub-path such as `https://user.github.io/portfolio/amd-static-website/` the `/styles/…` and `/pages/…` URLs resolve to the wrong place. To use a sub-path, convert them to relative URLs first.
- Use a host that serves `/pages/amdvsintel` as `amdvsintel.html`, or add `.html` to that link.
- Use a case-insensitive file system or fix the upper-case image extensions (issue 4).

TODO: confirmar com o BMS — whether the site was ever hosted and where.

## Tests

None.
