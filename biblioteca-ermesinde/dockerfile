# Estágio de Construção
FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build-env
WORKDIR ./Biblioteca_Ermesinde

# Copia apenas o arquivo .csproj para restaurar as dependências
COPY Biblioteca_Ermesinde/*.csproj ./
RUN dotnet restore

# Copia todo o código-fonte e compila o aplicativo
COPY Biblioteca_Ermesinde/. ./
RUN rm -f $(find . -name '*.AssemblyInfo.cs')
RUN dotnet publish -c Release -o out

# Estágio de Análise SonarQube
FROM mcr.microsoft.com/dotnet/sdk:7.0 AS sonar-scanner
WORKDIR ./Biblioteca_Ermesinde

# Instalação do Scanner .NET Core Global Tool
RUN dotnet tool install --global dotnet-sonarscanner

# Execução do Scanner
ARG SONAR_TOKEN
RUN dotnet sonarscanner begin /k:"bibloteca-ermesindesonar" /d:sonar.host.url="http://localhost:9000" /d:sonar.token="${SONAR_TOKEN}" && \
    dotnet build && \
    dotnet sonarscanner end /d:sonar.token="${SONAR_TOKEN}"

# Estágio de Publicação
FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS runtime
WORKDIR ./Biblioteca_Ermesinde

# Copia os arquivos publicados do estágio de construção
COPY --from=build-env /Biblioteca_Ermesinde/out .

# Expor a porta em que o aplicativo será executado
EXPOSE 8080

# Define o comando padrão para iniciar o aplicativo quando o contêiner é iniciado
ENTRYPOINT ["dotnet", "Biblioteca_Ermesinde.dll"]
