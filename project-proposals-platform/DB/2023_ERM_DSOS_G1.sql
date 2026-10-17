-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 15, 2024 at 05:39 AM
-- Server version: 8.0.35-0ubuntu0.20.04.1
-- PHP Version: 7.4.3-4ubuntu2.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `2023_ERM_DSOS_G1`
--

-- --------------------------------------------------------

--
-- Table structure for table `acesso`
--

CREATE TABLE `acesso` (
  `id` int UNSIGNED NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `acesso`
--

INSERT INTO `acesso` (`id`, `username`, `password`) VALUES
(1, 'nttdata', '202cb962ac59075b964b07152d234b70'),
(2, 'Roberto', '$2a$12$2JGtKolMP6Vfmgr8niGWDeDdKS51aqC5aloSi5WHS4lOxST5bflwW\r\n'),
(14, 'Paulo', '$2y$12$MojhSfWizcFWIKU5YdvtbOS4wypkljbJFWQZnXbAfHmt7lr5DSutC'),
(15, 'admin', '$2y$12$1Skl5iWZD/I4FgNkTYA.pehr3IrdVasI/SFG.Yp1GXmYNH3qc8cvu'),
(18, 'Carlos Carcoso', '$2y$12$pe9jHqCLzqWDB7dnO3PqkeLnHxcsnOnLX7AFOGWcoxHuSDlLmojPG'),
(19, 'Geraldo', '$2y$12$GwzsSJjWJcqFqGkvBktnxu7Q8G.4IGlhEZkD.3zCk5zsD/cvDk0JC'),
(28, 'Manuel', '$2y$12$s/9A6uTZsMmEli4bhXr6JOC2Ho68J88fRFXEmgazwnLLLYmLF.KtG'),
(40, 'joao2A', '1234'),
(41, 'maria2A', '1234'),
(42, 'carlos2A', '1234'),
(43, 'ana2A', '1234'),
(44, 'Marcelo', '$2y$12$R.2FSrfU0AJVem7xmnKIh.h.iobjJ.KMYvSHLz7GPl/dPHHrqV5Hm');

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id` int UNSIGNED NOT NULL,
  `id_dados` int UNSIGNED NOT NULL,
  `id_acesso` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id`, `id_dados`, `id_acesso`) VALUES
(1, 7, 15);

-- --------------------------------------------------------

--
-- Table structure for table `aluno`
--

CREATE TABLE `aluno` (
  `n_mecanografico` int UNSIGNED NOT NULL,
  `id_dados` int UNSIGNED NOT NULL,
  `id_acesso` int UNSIGNED NOT NULL,
  `Turma` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `aluno`
--

INSERT INTO `aluno` (`n_mecanografico`, `id_dados`, `id_acesso`, `Turma`) VALUES
(1, 3, 2, '1A'),
(5, 8, 18, '2A'),
(7, 14, 40, '2A'),
(8, 15, 41, '2A'),
(9, 16, 42, '2A'),
(10, 17, 43, '2A'),
(11, 11, 28, '3A');

-- --------------------------------------------------------

--
-- Table structure for table `dados_pessoais`
--

CREATE TABLE `dados_pessoais` (
  `id` int UNSIGNED NOT NULL,
  `Nome` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Data_nascimento` date NOT NULL,
  `Email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `NIF` int NOT NULL,
  `id_Pais` int UNSIGNED NOT NULL,
  `Ativo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Cidade` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Informação` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Telemovel` int NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `dados_pessoais`
--

INSERT INTO `dados_pessoais` (`id`, `Nome`, `Data_nascimento`, `Email`, `NIF`, `id_Pais`, `Ativo`, `Cidade`, `Informação`, `Telemovel`, `image`) VALUES
(1, 'NttData', '2001-12-21', 'geral@nttdata.com', 512987321, 2, 'S', 'Toquio', 'Eu sou eu', 912333212, ''),
(3, 'Roberto', '2023-09-07', 'roberto@gmail.com', 2, 1, 'S', 'Porto', '', 0, ''),
(6, 'ActuallyWorks', '2024-01-04', '1221521@isep.ipp.pt', 922222222, 4, 'S', 'Porto', 'Olá nos estamos aqui aqui', 934122443, ''),
(7, 'admin', '2024-01-05', 'admin@gmail.com', 23732771, 1, 'S', 'Porto', 'Eu seu mesmo fixe', 9232323, ''),
(8, 'Carlos', '2004-06-09', 'jpcmont@gmail.com', 2712737, 1, 'S', 'Porto', '', 0, ''),
(9, 'Geraldo', '1995-06-15', 'geraldo@gmail.com', 2321233, 2, 'S', 'Hiroshima', '', 0, ''),
(11, 'Manuel Sousa', '2024-01-04', 'manuelsoueu@gmail.com', 3737272, 1, 'S', 'Lisboa', 'Escreva alguma coisa aqui quando desejar!', 932122432, 'none'),
(14, 'João', '2003-12-21', 'joao@gmail.com', 250543123, 1, 'S', 'Porto', '', 912456213, 'none'),
(15, 'Maria', '2002-12-21', 'maria@gmail.com', 250543124, 1, 'S', 'Porto', '', 934532131, 'none'),
(16, 'Carlos', '2000-12-21', 'carlos@gmail.com', 250543125, 1, 'S', 'Coimbra', '', 934532132, 'none'),
(17, 'Ana', '2004-12-21', 'ana@gmail.com', 250543126, 1, 'S', 'Leiria', '', 924532131, 'none'),
(18, 'Marcelo Teatro', '1992-02-04', 'marcelo@gmail.com', 37472722, 2, 'S', 'Nagasaki', 'Escreva alguma coisa aqui quando desejar!', 934555212, 'none');

-- --------------------------------------------------------

--
-- Table structure for table `docentes`
--

CREATE TABLE `docentes` (
  `id` int UNSIGNED NOT NULL,
  `id_dados` int UNSIGNED NOT NULL,
  `id_acesso` int UNSIGNED NOT NULL,
  `regente` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ativo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `docentes`
--

INSERT INTO `docentes` (`id`, `id_dados`, `id_acesso`, `regente`, `ativo`) VALUES
(1, 9, 19, 'Departamento de Informatica', 'S'),
(2, 18, 44, 'Departamento de Balneario', 'S');

-- --------------------------------------------------------

--
-- Table structure for table `empresa`
--

CREATE TABLE `empresa` (
  `id` int UNSIGNED NOT NULL,
  `id_dados` int UNSIGNED NOT NULL,
  `id_acesso` int UNSIGNED NOT NULL,
  `diretor` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `aprovada` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `empresa`
--

INSERT INTO `empresa` (`id`, `id_dados`, `id_acesso`, `diretor`, `aprovada`) VALUES
(4, 1, 1, 'Tiago Barroso', 'Sim'),
(8, 6, 14, 'Paulo', 'Sim');

-- --------------------------------------------------------

--
-- Table structure for table `estado_proposta`
--

CREATE TABLE `estado_proposta` (
  `id` int NOT NULL,
  `Estado` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `estado_proposta`
--

INSERT INTO `estado_proposta` (`id`, `Estado`) VALUES
(1, 'Em Aprovação'),
(2, 'A decorrer'),
(3, 'Finalizado');

-- --------------------------------------------------------

--
-- Table structure for table `estado_propostas_aluno`
--

CREATE TABLE `estado_propostas_aluno` (
  `id` int UNSIGNED NOT NULL,
  `estado` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `estado_propostas_aluno`
--

INSERT INTO `estado_propostas_aluno` (`id`, `estado`) VALUES
(1, 'Em Espera'),
(2, 'Aprovado'),
(3, 'Rejeitado');

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint UNSIGNED NOT NULL,
  `uuid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `connection` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `queue` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `payload` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `exception` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int UNSIGNED NOT NULL,
  `migration` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `batch` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '2014_10_12_000000_create_users_table', 1),
(2, '2014_10_12_100000_create_password_reset_tokens_table', 1),
(3, '2019_08_19_000000_create_failed_jobs_table', 1),
(4, '2019_12_14_000001_create_personal_access_tokens_table', 1),
(5, '2023_12_15_215012_create_dados_pessoais_table', 1),
(6, '2023_12_15_215014_create_docentes_table', 1),
(7, '2023_12_15_215015_create_propostas_table', 1),
(8, '2023_12_15_215016_create_projetos_table', 1),
(9, '2023_12_15_215017_create_pais_table', 1),
(10, '2023_12_15_215018_create_supervisores_table', 1),
(11, '2023_12_15_215019_create_rel_supervisores_propostas_table', 1),
(12, '2023_12_15_215020_create_rel_proposta_alunos_table', 1),
(13, '2023_12_15_215021_create_aluno_table', 1),
(14, '2023_12_15_215022_create_empresa_table', 1),
(15, '2023_12_15_215023_create_rel_projeto_aluno_table', 1),
(16, '2023_12_15_215025_create_acesso_table', 1),
(17, '2023_12_31_115005_rel_proposta_aluno_pdf', 2),
(18, '2024_01_05_210802_rel_projetos_propostas', 3),
(19, '2024_01_05_212349_estado_propostas_aluno', 4),
(20, '2014_10_12_100000_create_password_resets_table', 5);

-- --------------------------------------------------------

--
-- Table structure for table `pais`
--

CREATE TABLE `pais` (
  `id` int UNSIGNED NOT NULL,
  `Pais` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pais`
--

INSERT INTO `pais` (`id`, `Pais`) VALUES
(4, 'Alemanha'),
(3, 'Espanha'),
(2, 'Japão'),
(1, 'Portugal');

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

CREATE TABLE `password_resets` (
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `password_reset_tokens`
--

INSERT INTO `password_reset_tokens` (`email`, `token`, `created_at`) VALUES
('1221521@isep.ipp.pt', 'b0z20I74KS7DPj12j4q0IYk5wR3gCdohEcZMCvYOoyig59oyOBBwwvx3BU5w', '2024-01-14 16:13:59'),
('jpcmont@gmail.com', 'jz261dhNU3qSibFulUVD6oZ2ihnGnc6bqaSC2hKZQCS6MRTnncPWXJecZsZy', '2024-01-14 16:14:42');

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tokenable_id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `abilities` text COLLATE utf8mb4_unicode_ci,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `projetos`
--

CREATE TABLE `projetos` (
  `id` int UNSIGNED NOT NULL,
  `Descricao` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Titulo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Tipo_de_Trabalho` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DataPublicacao` timestamp NOT NULL,
  `id_docente` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `projetos`
--

INSERT INTO `projetos` (`id`, `Descricao`, `Titulo`, `Tipo_de_Trabalho`, `DataPublicacao`, `id_docente`) VALUES
(7, 'ldkjflj', 'dknfdl', 'fdjfdfn', '2024-01-15 01:32:27', 2),
(8, 'dkjfldkj', 'dsfkjdsl', 'kdjfjk', '2024-01-15 01:35:47', 2),
(9, 'ola', 'aOla', 'ola', '2024-01-15 01:36:38', 2),
(10, 'ljsdls', 'asdjsa', 'skljdk', '2024-01-15 01:37:47', 2);

-- --------------------------------------------------------

--
-- Table structure for table `propostas`
--

CREATE TABLE `propostas` (
  `id` int UNSIGNED NOT NULL,
  `id_empresa` int UNSIGNED NOT NULL,
  `Titulo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Descricao` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Tipo_de_Trabalho` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `localização` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vagas` int NOT NULL,
  `DataPublicacao` timestamp NOT NULL,
  `id_estado` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `propostas`
--

INSERT INTO `propostas` (`id`, `id_empresa`, `Titulo`, `Descricao`, `Tipo_de_Trabalho`, `localização`, `vagas`, `DataPublicacao`, `id_estado`) VALUES
(1, 4, 'Quero alguem que tenha 4 anos de experiência', 'Nos vimos o teu curriculo e pareces alguem com um grande triunfo em experiência. Gostariamos de entrar em contacto consigo para ver se era possível trabalhar connosco.', 'Help Desk', 'Porto', 20, '2024-01-12 14:27:51', 2),
(2, 8, 'Por favor estamos desesperados', 'Eu preciso de alguem JÁ!', 'Ajudante de copos', 'Lisboa', 1, '2024-01-13 14:59:27', 2),
(8, 8, 'teste', 'teste', 'teste', 'teste', 122, '2024-01-13 21:35:46', 2),
(9, 8, 'teste2', 'teste2', 'teste2', 'teste2', 10, '2024-01-13 21:37:34', 2),
(10, 8, 'Burros de serviço', 'precisamos de burros', 'ser burro', 'Porto', 69, '2024-01-13 21:38:59', 2),
(11, 8, 'Precisamos de pessoas capazes', '200 EUROS por dia', 'Arrumar carros', 'Lisboa', 11, '2024-01-14 18:00:41', 2),
(12, 8, 'sd', 'sd', 'sd', 'ds', 2, '2024-01-14 18:05:28', 2),
(13, 8, 'fodjhkjh', 'djfsjdklfhkj', 'jdfhkjlsdfhkl', 'djskfjhks', 34, '2024-01-14 18:07:11', 1),
(14, 8, 'otighepodhgrpç-', 'dfjsdkfj', 'dfjbsdfkb', 'dsdfs', 12, '2024-01-14 18:33:03', 1),
(15, 8, 'sdfkjdsjk', 'kdfjdkj', 'kjdfkjd', 'sada', 122, '2024-01-15 02:41:54', 1),
(16, 8, 'sdfkjdsjk', 'kdfjdkj', 'kjdfkjd', 'sada', 122, '2024-01-15 02:43:31', 1),
(17, 8, 'sdfkjdsjk', 'kdfjdkj', 'kjdfkjd', 'sada', 122, '2024-01-15 02:43:47', 1),
(18, 8, 'sdfkjdsjk', 'kdfjdkj', 'kjdfkjd', 'sada', 122, '2024-01-15 02:44:16', 1),
(19, 8, 'sdfkjdsjk', 'kdfjdkj', 'kjdfkjd', 'sada', 122, '2024-01-15 02:44:43', 1);

-- --------------------------------------------------------

--
-- Table structure for table `rel_docente_proposta`
--

CREATE TABLE `rel_docente_proposta` (
  `id` int NOT NULL,
  `id_docente` int UNSIGNED NOT NULL,
  `id_proposta` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `rel_docente_proposta`
--

INSERT INTO `rel_docente_proposta` (`id`, `id_docente`, `id_proposta`) VALUES
(1, 1, 1),
(2, 2, 11);

-- --------------------------------------------------------

--
-- Table structure for table `rel_feedback_projeto`
--

CREATE TABLE `rel_feedback_projeto` (
  `id` int NOT NULL,
  `id_rel_projeto` int UNSIGNED NOT NULL,
  `feedback` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `rel_feedback_projeto`
--

INSERT INTO `rel_feedback_projeto` (`id`, `id_rel_projeto`, `feedback`) VALUES
(1, 1, 'banana');

-- --------------------------------------------------------

--
-- Table structure for table `rel_projeto_aluno`
--

CREATE TABLE `rel_projeto_aluno` (
  `id` int UNSIGNED NOT NULL,
  `id_aluno` int UNSIGNED NOT NULL,
  `id_projeto` int UNSIGNED NOT NULL,
  `file` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `feedback` text COLLATE utf8mb4_unicode_ci,
  `apresentacao` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `rel_projeto_aluno`
--

INSERT INTO `rel_projeto_aluno` (`id`, `id_aluno`, `id_projeto`, `file`, `feedback`, `apresentacao`) VALUES
(1, 11, 10, NULL, 'banana', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `rel_projeto_docente`
--

CREATE TABLE `rel_projeto_docente` (
  `id` int NOT NULL,
  `id_docente` int UNSIGNED NOT NULL,
  `id_projeto` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `rel_proposta_alunos`
--

CREATE TABLE `rel_proposta_alunos` (
  `id` int UNSIGNED NOT NULL,
  `id_proposta` int UNSIGNED NOT NULL,
  `id_Aluno` int UNSIGNED NOT NULL,
  `id_estado` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `rel_proposta_alunos`
--

INSERT INTO `rel_proposta_alunos` (`id`, `id_proposta`, `id_Aluno`, `id_estado`) VALUES
(1, 1, 5, 2),
(5, 1, 11, 3),
(7, 2, 5, 2),
(9, 8, 5, 3),
(10, 11, 5, 2),
(11, 2, 11, 2);

-- --------------------------------------------------------

--
-- Table structure for table `rel_supervisores_propostas`
--

CREATE TABLE `rel_supervisores_propostas` (
  `id` int UNSIGNED NOT NULL,
  `id_proposta` int UNSIGNED NOT NULL,
  `id_supervisor` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `supervisores`
--

CREATE TABLE `supervisores` (
  `id` int UNSIGNED NOT NULL,
  `id_acesso` int NOT NULL,
  `id_dados` int UNSIGNED NOT NULL,
  `id_empresa` int UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `remember_token` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `acesso`
--
ALTER TABLE `acesso`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `acesso_username_unique` (`username`);

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_acesso` (`id_acesso`),
  ADD KEY `fk_dados` (`id_dados`);

--
-- Indexes for table `aluno`
--
ALTER TABLE `aluno`
  ADD PRIMARY KEY (`n_mecanografico`),
  ADD KEY `aluno_id_acesso_foreign` (`id_acesso`),
  ADD KEY `aluno___fk` (`id_dados`);

--
-- Indexes for table `dados_pessoais`
--
ALTER TABLE `dados_pessoais`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `dados_pessoais_email_unique` (`Email`),
  ADD KEY `dados_pessoais_id_pais_foreign` (`id_Pais`);

--
-- Indexes for table `docentes`
--
ALTER TABLE `docentes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `docentes_id_dados_foreign` (`id_dados`),
  ADD KEY `docentes_id_acesso_foreign` (`id_acesso`);

--
-- Indexes for table `empresa`
--
ALTER TABLE `empresa`
  ADD PRIMARY KEY (`id`),
  ADD KEY `empresa_id_acesso_foreign` (`id_acesso`),
  ADD KEY `empresa_id_dados_foreign` (`id_dados`);

--
-- Indexes for table `estado_proposta`
--
ALTER TABLE `estado_proposta`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `estado_propostas_aluno`
--
ALTER TABLE `estado_propostas_aluno`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pais`
--
ALTER TABLE `pais`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `pais_pais_unique` (`Pais`);

--
-- Indexes for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD KEY `password_resets_email_index` (`email`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`);

--
-- Indexes for table `projetos`
--
ALTER TABLE `projetos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `projeto_id_docente_foreign` (`id_docente`);

--
-- Indexes for table `propostas`
--
ALTER TABLE `propostas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `propostas_id_empresa_foreign` (`id_empresa`),
  ADD KEY `propostas_estado_proposta_id_foreign` (`id_estado`);

--
-- Indexes for table `rel_docente_proposta`
--
ALTER TABLE `rel_docente_proposta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rel_docente_proposta_id_docente_foreign` (`id_docente`),
  ADD KEY `rel_docente_proposta_id_proposta_foreign` (`id_proposta`);

--
-- Indexes for table `rel_feedback_projeto`
--
ALTER TABLE `rel_feedback_projeto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rel_feedback_id_rel_projeto` (`id_rel_projeto`);

--
-- Indexes for table `rel_projeto_aluno`
--
ALTER TABLE `rel_projeto_aluno`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rel_projeto_aluno_id_aluno_foreign` (`id_aluno`),
  ADD KEY `rel_projeto_aluno_id_projeto_foreign` (`id_projeto`);

--
-- Indexes for table `rel_projeto_docente`
--
ALTER TABLE `rel_projeto_docente`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rel_projeto_docente_id_docente_foreign` (`id_docente`),
  ADD KEY `rel_projeto_docente_id_projeto_foreign` (`id_projeto`);

--
-- Indexes for table `rel_proposta_alunos`
--
ALTER TABLE `rel_proposta_alunos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rel_proposta_alunos_id_proposta_foreign` (`id_proposta`),
  ADD KEY `rel_proposta_alunos_aluno_id` (`id_Aluno`),
  ADD KEY `rel_proposta_alunos_estado_id_foreign` (`id_estado`);

--
-- Indexes for table `rel_supervisores_propostas`
--
ALTER TABLE `rel_supervisores_propostas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `rel_supervisores_propostas_id_proposta_foreign` (`id_proposta`),
  ADD KEY `rel_supervisores_propostas_id_supervisor_foreign` (`id_supervisor`);

--
-- Indexes for table `supervisores`
--
ALTER TABLE `supervisores`
  ADD PRIMARY KEY (`id`),
  ADD KEY `supervisores_id_empresa_foreign` (`id_empresa`),
  ADD KEY `supervisores_id_dados_foreign` (`id_dados`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `acesso`
--
ALTER TABLE `acesso`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `aluno`
--
ALTER TABLE `aluno`
  MODIFY `n_mecanografico` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `dados_pessoais`
--
ALTER TABLE `dados_pessoais`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `docentes`
--
ALTER TABLE `docentes`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `empresa`
--
ALTER TABLE `empresa`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `estado_proposta`
--
ALTER TABLE `estado_proposta`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `estado_propostas_aluno`
--
ALTER TABLE `estado_propostas_aluno`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `pais`
--
ALTER TABLE `pais`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `projetos`
--
ALTER TABLE `projetos`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `propostas`
--
ALTER TABLE `propostas`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `rel_docente_proposta`
--
ALTER TABLE `rel_docente_proposta`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `rel_feedback_projeto`
--
ALTER TABLE `rel_feedback_projeto`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `rel_projeto_aluno`
--
ALTER TABLE `rel_projeto_aluno`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `rel_projeto_docente`
--
ALTER TABLE `rel_projeto_docente`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `rel_proposta_alunos`
--
ALTER TABLE `rel_proposta_alunos`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `rel_supervisores_propostas`
--
ALTER TABLE `rel_supervisores_propostas`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `supervisores`
--
ALTER TABLE `supervisores`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `fk_acesso` FOREIGN KEY (`id_acesso`) REFERENCES `acesso` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_dados` FOREIGN KEY (`id_dados`) REFERENCES `dados_pessoais` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Constraints for table `aluno`
--
ALTER TABLE `aluno`
  ADD CONSTRAINT `aluno___fk` FOREIGN KEY (`id_dados`) REFERENCES `dados_pessoais` (`id`),
  ADD CONSTRAINT `aluno_id_acesso_foreign` FOREIGN KEY (`id_acesso`) REFERENCES `acesso` (`id`);

--
-- Constraints for table `dados_pessoais`
--
ALTER TABLE `dados_pessoais`
  ADD CONSTRAINT `dados_pessoais_id_pais_foreign` FOREIGN KEY (`id_Pais`) REFERENCES `pais` (`id`);

--
-- Constraints for table `docentes`
--
ALTER TABLE `docentes`
  ADD CONSTRAINT `docentes_id_acesso_foreign` FOREIGN KEY (`id_acesso`) REFERENCES `acesso` (`id`),
  ADD CONSTRAINT `docentes_id_dados_foreign` FOREIGN KEY (`id_dados`) REFERENCES `dados_pessoais` (`id`);

--
-- Constraints for table `empresa`
--
ALTER TABLE `empresa`
  ADD CONSTRAINT `empresa_id_acesso_foreign` FOREIGN KEY (`id_acesso`) REFERENCES `acesso` (`id`),
  ADD CONSTRAINT `empresa_id_dados_foreign` FOREIGN KEY (`id_dados`) REFERENCES `dados_pessoais` (`id`);

--
-- Constraints for table `projetos`
--
ALTER TABLE `projetos`
  ADD CONSTRAINT `projeto_id_docente_foreign` FOREIGN KEY (`id_docente`) REFERENCES `docentes` (`id`);

--
-- Constraints for table `propostas`
--
ALTER TABLE `propostas`
  ADD CONSTRAINT `propostas_estado_proposta_id_foreign` FOREIGN KEY (`id_estado`) REFERENCES `estado_proposta` (`id`),
  ADD CONSTRAINT `propostas_id_empresa_foreign` FOREIGN KEY (`id_empresa`) REFERENCES `empresa` (`id`);

--
-- Constraints for table `rel_docente_proposta`
--
ALTER TABLE `rel_docente_proposta`
  ADD CONSTRAINT `rel_docente_proposta_id_docente_foreign` FOREIGN KEY (`id_docente`) REFERENCES `docentes` (`id`),
  ADD CONSTRAINT `rel_docente_proposta_id_proposta_foreign` FOREIGN KEY (`id_proposta`) REFERENCES `propostas` (`id`);

--
-- Constraints for table `rel_feedback_projeto`
--
ALTER TABLE `rel_feedback_projeto`
  ADD CONSTRAINT `rel_feedback_id_rel_projeto` FOREIGN KEY (`id_rel_projeto`) REFERENCES `rel_proposta_alunos` (`id`);

--
-- Constraints for table `rel_projeto_aluno`
--
ALTER TABLE `rel_projeto_aluno`
  ADD CONSTRAINT `rel_projeto_aluno_id_aluno_foreign` FOREIGN KEY (`id_aluno`) REFERENCES `aluno` (`n_mecanografico`),
  ADD CONSTRAINT `rel_projeto_aluno_id_projeto_foreign` FOREIGN KEY (`id_projeto`) REFERENCES `projetos` (`id`);

--
-- Constraints for table `rel_projeto_docente`
--
ALTER TABLE `rel_projeto_docente`
  ADD CONSTRAINT `rel_projeto_docente_id_docente_foreign` FOREIGN KEY (`id_docente`) REFERENCES `docentes` (`id`),
  ADD CONSTRAINT `rel_projeto_docente_id_projeto_foreign` FOREIGN KEY (`id_projeto`) REFERENCES `projetos` (`id`);

--
-- Constraints for table `rel_proposta_alunos`
--
ALTER TABLE `rel_proposta_alunos`
  ADD CONSTRAINT `rel_proposta_alunos_aluno_id` FOREIGN KEY (`id_Aluno`) REFERENCES `aluno` (`n_mecanografico`),
  ADD CONSTRAINT `rel_proposta_alunos_estado_id_foreign` FOREIGN KEY (`id_estado`) REFERENCES `estado_propostas_aluno` (`id`),
  ADD CONSTRAINT `rel_proposta_alunos_id_proposta_foreign` FOREIGN KEY (`id_proposta`) REFERENCES `propostas` (`id`);

--
-- Constraints for table `rel_supervisores_propostas`
--
ALTER TABLE `rel_supervisores_propostas`
  ADD CONSTRAINT `rel_supervisores_propostas_id_proposta_foreign` FOREIGN KEY (`id_proposta`) REFERENCES `propostas` (`id`),
  ADD CONSTRAINT `rel_supervisores_propostas_id_supervisor_foreign` FOREIGN KEY (`id_supervisor`) REFERENCES `supervisores` (`id`);

--
-- Constraints for table `supervisores`
--
ALTER TABLE `supervisores`
  ADD CONSTRAINT `supervisores_id_dados_foreign` FOREIGN KEY (`id_dados`) REFERENCES `dados_pessoais` (`id`),
  ADD CONSTRAINT `supervisores_id_empresa_foreign` FOREIGN KEY (`id_empresa`) REFERENCES `empresa` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
