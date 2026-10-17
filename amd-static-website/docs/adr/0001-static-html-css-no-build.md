# ADR 0001 — Plain static HTML/CSS with no build step or framework

> Documentation added in 2026 (portfolio). Reconstructed from the code; the team's reasoning is not recorded. Portuguese version: [0001-static-html-css-no-build.pt.md](0001-static-html-css-no-build.pt.md).

- **Status:** Inferred — TODO: confirmar com o BMS
- **Evidence:** no `package.json`, bundler or template files in the project; 5 hand-written pages; nav and the `hamburgerMenu()` script copied into each page ([ARCHITECTURE](../ARCHITECTURE.md))

## Context

A first-year programming-lab assignment (CTESP, LP1, 2022): a multi-page informational site about AMD, written by a group of four (author photos on the home page). The goal was content and consistent navigation, not an application.

## Decision

Write the site as plain HTML pages with per-group CSS files, one tiny inline script for the mobile menu, and third-party resources (Font Awesome, Poppins) loaded from CDNs.

## Consequences

- **Positive:** nothing to install or build; opens in any browser or static host; easy for a beginner team to split pages between members.
- **Negative:** the navigation is duplicated in 5 files, so a menu change means 5 edits; styles are partly duplicated between `styles/styles.css` and `styles/arch/styles.css`; root-absolute URLs tie the site to the domain root ([SETUP](../SETUP.md#deploy)); icons and font need internet.

## Alternatives not taken

- A static-site generator or templating include — would remove the nav duplication, but adds tooling beyond the course scope. Reasonable if the site were maintained.
- Relative URLs everywhere — would make `file://` and sub-path hosting work; a small change if someone revisits it.
