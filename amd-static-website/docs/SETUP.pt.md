# Setup — Site estático da AMD (LP1)

> Documentação adicionada em 2026 (portfolio). Os comandos de servidor local abaixo foram **executados** a 2026-10-17 (Python 3, Windows); a secção de deploy é orientação, não algo que a equipa tenha feito. Versão inglesa: [SETUP.md](SETUP.md).

## Pré-requisitos

| Ferramenta | Notas |
|---|---|
| Um browser | Qualquer um atual |
| Python 3 (opcional) | Só para o servidor local; serve qualquer servidor estático |

Sem passo de build, gestor de pacotes, base de dados, variáveis de ambiente nem segredos. A página carrega Font Awesome (cdnjs) e Poppins (Google Fonts) da internet, por isso os ícones e a fonte precisam de ligação.

## Correr localmente

Servir a pasta do projeto **a partir da raiz** (ver porquê abaixo):

```bash
cd amd-static-website
python -m http.server 8000
# abrir http://localhost:8000/
```

Resultado do teste: `/`, `/pages/history.html`, `/pages/arch.html`, `/pages/sockets.html`, `/pages/amdvsintel.html` e `/styles/styles.css` devolveram 200; `/pages/amdvsintel` (a ligação usada na página inicial, sem `.html`) devolveu 404.

### Porque não abrir só o `index.html`?

O README diz para abrir o ficheiro diretamente. A página inicial abre, mas a navegação usa URLs absolutos à raiz (`/pages/history.html`, `/styles/styles.css`), que em `file://` apontam para a raiz do disco, e as ligações e os estilos falham. Usar um servidor local. Detalhes: [ARCHITECTURE §5](ARCHITECTURE.pt.md), problemas 1–3.

## Guia de edição

| Para alterar | Editar |
|---|---|
| Texto de uma página | O ficheiro correspondente em `pages/` (ou `index.html`) |
| Aspeto de um grupo de páginas | `styles/<grupo>/…css` (`history.css`, `arch/`, `sockets/`, `amdintel/`) |
| Cores/fontes partilhadas pela página inicial e pela maioria das páginas | `styles/styles.css` (`arch.html` usa o seu `styles/arch/styles.css`) |
| Navegação | **Os 5 ficheiros HTML** — a navegação está copiada, não há include |
| Imagens | `assets/imgs/…`; manter as maiúsculas/minúsculas do ficheiro iguais ao `src` (problema 4) |

## Deploy

Não há configuração de deploy no repositório. Serve qualquer alojamento estático (GitHub Pages, Netlify, uma pasta Apache/Nginx). Restrições que vêm do código:

- Servir na **raiz do domínio** (por exemplo um site Pages de utilizador/organização ou domínio próprio). Num sub-caminho como `https://user.github.io/portfolio/amd-static-website/` os URLs `/styles/…` e `/pages/…` apontam para o sítio errado. Para usar sub-caminho, converter primeiro para URLs relativos.
- Usar um alojamento que sirva `/pages/amdvsintel` como `amdvsintel.html`, ou acrescentar `.html` a essa ligação.
- Usar um sistema de ficheiros que ignore maiúsculas/minúsculas ou corrigir as extensões de imagem em maiúsculas (problema 4).

TODO: confirmar com o BMS — se o site chegou a ser alojado e onde.

## Testes

Nenhum.
