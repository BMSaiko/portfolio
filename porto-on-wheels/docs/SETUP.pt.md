# Setup — Porto on Wheels

> Documentação adicionada em 2026 (portfolio). Derivada do código-fonte. **Nenhum comando abaixo foi corrido**: esta máquina não tem PHP nem MySQL preparados para o projeto (marcado `não testado`). O repositório original não tem esquema, por isso o SQL do passo 2 é **reconstruído a partir das consultas do código** e pode não coincidir com a base de dados original. English version: [SETUP.md](SETUP.md).

## Pré-requisitos

| Item | Versão | Origem |
|---|---|---|
| PHP | 7.4 ou superior com `mysqli`; `file_uploads` ativo e `upload_max_filesize` suficiente para imagens de produtos | `src/database/db.php`, `admin/db/insert.php` |
| MySQL / MariaDB | qualquer versão recente | `conn.php` |
| Acesso à internet | para os assets de CDN (Bootstrap, DataTables, Font Awesome) e o mapa Mapbox | `src/layout/header.php`, `contact.php` |

Não há passo de Composer nem de npm.

## Variáveis de ambiente

| Variável | Usada por | Por omissão |
|---|---|---|
| `PORTOONWHEELS_DB_HOST` | `src/database/conn.php` | `localhost` |
| `PORTOONWHEELS_DB_USER` | `src/database/conn.php` | `root` |
| `PORTOONWHEELS_DB_PASSWORD` | `src/database/conn.php` | vazio |

O nome da base de dados `portoonwheels` está fixo em `conn.php`. Para o mapa, substitui o marcador `<MAPBOX_PUBLIC_TOKEN>` em `contact.php` pelo teu token público (não o versionar).

## Passos

Executar a partir de `porto-on-wheels/` (não testado).

1. **Definir as variáveis**
   - PowerShell: `$env:PORTOONWHEELS_DB_USER = "<user>"; $env:PORTOONWHEELS_DB_PASSWORD = "<password>"`
   - bash: `export PORTOONWHEELS_DB_USER='<user>' PORTOONWHEELS_DB_PASSWORD='<password>'`

   Usa a mesma shell para as exportar e para arrancar o servidor embutido do PHP.
2. **Criar a base de dados** (reconstruída, tipos inferidos):
   ```sql
   CREATE DATABASE portoonwheels CHARACTER SET utf8mb4;
   USE portoonwheels;
   CREATE TABLE accounts (
     id INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(100), email VARCHAR(150), password VARCHAR(255),
     phone_number VARCHAR(30), role VARCHAR(20) DEFAULT 'USER',
     profile_img MEDIUMBLOB
   );
   CREATE TABLE products (
     id INT AUTO_INCREMENT PRIMARY KEY,
     type_id INT NOT NULL,            -- 1 bicicletas, 2 trotinetes, 3 carros
     name VARCHAR(100), quantity INT, image MEDIUMBLOB
   );
   CREATE TABLE plans (id INT AUTO_INCREMENT PRIMARY KEY, hours INT, price DECIMAL(8,2));
   CREATE TABLE partners (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), image MEDIUMBLOB);
   CREATE TABLE rents (
     id INT AUTO_INCREMENT PRIMARY KEY,
     product_id INT, account_id INT, date VARCHAR(30),
     quantity INT, plan INT, payment TINYINT DEFAULT 0, state TINYINT DEFAULT 0
   );
   ```
   `role` tem de ser exatamente `ADMIN` para um administrador. `rents.date` é texto porque o formulário envia `yyyy-dd-mm HH:MM`; o tipo original é desconhecido.
3. **Criar um admin**: regista um utilizador normal em `register.php` e depois põe `role = 'ADMIN'` em `accounts`. Por causa do problema conhecido #2 a password guardada é igual ao e-mail, por isso entra com o e-mail como password ou atualiza a coluna `password` à mão.
4. **Executar**
   ```
   php -S localhost:8000
   ```
   Abre `http://localhost:8000/` (site público) e `http://localhost:8000/admin/login.php` (back-office).

## Testes

Nenhum. Não há suite de testes nem CI.

## Resolução de problemas

| Sintoma | Causa provável | Solução |
|---|---|---|
| `Failed to connect to MySQL - Access denied` impresso na página | Utilizador ou password errados, ou as variáveis não estão exportadas na shell que arrancou o `php -S` | Exportar de novo e reiniciar o servidor |
| `Failed to connect to MySQL - Unknown database 'portoonwheels'` | Base de dados não criada | Passo 2 |
| `Unable to prepare MySQL statement` | Uma tabela ou coluna difere do código | Alinhar o esquema com as consultas (ver [ARCHITECTURE](ARCHITECTURE.pt.md#modelo-de-dados-inferido)) |
| `Class "mysqli" not found` | Extensão `mysqli` desativada | Ativar `extension=mysqli` no `php.ini` |
| Um utilizador registado não consegue entrar | Problema conhecido #2 (e-mail guardado como password) | Entrar com o e-mail como password |
| Falha o upload de imagens ou ficam vazias | Limites de upload ou `max_allowed_packet` demasiado pequenos | Aumentar `upload_max_filesize`, `post_max_size` e `max_allowed_packet` |
| Mapa em branco | Token Mapbox por preencher | Pôr o teu token |
| Faltam estilos | Sem internet (CDN) | Ligar-se, ou alojar localmente os ficheiros da CDN |

## Notas de segurança

- As passwords são texto simples e vários endpoints de admin não têm autenticação (ver [Problemas conhecidos](ARCHITECTURE.pt.md#problemas-conhecidos-verificados-no-código-não-corrigidos) #1 e #3): usar só em localhost e nunca expor a uma rede.
- Nunca fazer commit de credenciais reais da base de dados nem de um token Mapbox.
