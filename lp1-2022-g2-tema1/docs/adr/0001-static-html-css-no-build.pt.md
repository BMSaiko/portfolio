# ADR 0001 — HTML/CSS estático simples, sem build nem framework

> Documentação adicionada em 2026 (portfolio). Reconstruído a partir do código; o raciocínio da equipa não está registado. Versão inglesa: [0001-static-html-css-no-build.md](0001-static-html-css-no-build.md).

- **Estado:** Inferido — TODO: confirmar com o BMS
- **Evidência:** sem `package.json`, bundler nem ficheiros de template no projeto; 5 páginas escritas à mão; navegação e script `hamburgerMenu()` copiados para cada página ([ARCHITECTURE](../ARCHITECTURE.pt.md))

## Contexto

Trabalho de Laboratório de Programação de 1.º ano (CTESP, LP1, 2022): um site informativo de várias páginas sobre a AMD, feito por um grupo de quatro (fotos dos autores na página inicial). O objetivo era conteúdo e navegação consistente, não uma aplicação.

## Decisão

Escrever o site como páginas HTML simples com ficheiros CSS por grupo, um pequeno script inline para o menu móvel, e recursos de terceiros (Font Awesome, Poppins) carregados de CDNs.

## Consequências

- **Positivas:** nada para instalar nem compilar; abre em qualquer browser ou alojamento estático; fácil para uma equipa iniciante repartir páginas pelos membros.
- **Negativas:** a navegação está duplicada em 5 ficheiros, por isso alterar o menu são 5 edições; os estilos estão em parte duplicados entre `styles/styles.css` e `styles/arch/styles.css`; os URLs absolutos à raiz prendem o site à raiz do domínio ([SETUP](../SETUP.pt.md#deploy)); ícones e fonte precisam de internet.

## Alternativas não tomadas

- Gerador de sites estáticos ou include de templates — eliminaria a duplicação da navegação, mas traz ferramentas fora do âmbito da cadeira. Razoável se o site fosse mantido.
- URLs relativos em todo o lado — faria `file://` e alojamento em sub-caminho funcionarem; alteração pequena se alguém voltar ao projeto.
