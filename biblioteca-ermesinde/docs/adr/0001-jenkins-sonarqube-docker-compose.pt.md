# ADR 0001 — Jenkins + SonarQube + Docker Compose como toolchain de entrega

> Documentação adicionada em 2026 (portfolio). Reconstruída a partir dos ficheiros do repositório; o raciocínio original da equipa não está registado, por isso as motivações abaixo estão marcadas como inferidas. English version: [0001-jenkins-sonarqube-docker-compose.md](0001-jenkins-sonarqube-docker-compose.md).

- **Estado:** Aceite (como implementado no repositório)
- **Evidência:** `jenkinsfile`, `dockerfile`, `docker-compose.yml`, `vagrant/Vagrantfile`, `Relatorio_de_DOS.docx`
- **Confirmar com o autor:** TODO: confirmar com o BMS (motivações, porque o Bitbucket como origem)

## Contexto

A UC (CTESP, Desenvolvimento Ágil de Software) pede uma API REST entregue com uma toolchain automatizada: automação de build, análise estática e ambientes reproduzíveis. A API depende de SQL Server, que difere entre máquinas de desenvolvimento.

## Decisão

Correr toda a toolchain em contentores descritos num único `docker-compose.yml`:

| Preocupação | Escolha | Ficheiro |
|---|---|---|
| Runtime da API | `dockerfile` multi-estágio (`sdk:7.0` build, `aspnet:7.0` runtime) | `dockerfile` |
| Base de dados | Contentor SQL Server 2022 (ou VM Vagrant) | `docker-compose.yml`, `vagrant/Vagrantfile` |
| CI | Jenkins LTS, pipeline como código (`Checkout` → `Build`) | `jenkinsfile` |
| Análise estática | SonarQube com `dotnet-sonarscanner` | `dockerfile`, `docker-compose.yml` |
| Segredos | Variáveis de ambiente e uma credencial Jenkins | `docker-compose.yml`, `jenkinsfile` |

## Consequências

Positivas
- Um comando (`docker compose up --build`) levanta API, base de dados, CI e análise.
- A pipeline fica versionada com o código.
- Os segredos devem vir do ambiente e não do repositório.

Negativas / compromissos (verificados nos ficheiros)
- A pipeline Jenkins não tem estágio de testes nem de Sonar; o Sonar corre num estágio Docker separado de que a imagem final não depende.
- O scanner Sonar aponta para `localhost:9000`, que dentro de um build não é o contentor Sonar.
- Mantêm-se um endereço de base de dados fixo e uma password de admin do SonarQube em texto simples em `docker-compose.yml` / `BackEndContext.cs`.
- Tags `latest` (`sonarqube:latest`) tornam os builds não reproduzíveis.

## Alternativas (inferidas, não documentadas pela equipa)

- GitHub Actions ou Azure Pipelines em vez de um Jenkins próprio: menos infraestrutura para manter.
- SonarCloud em vez de SonarQube auto-alojado.
