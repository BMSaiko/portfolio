USE RentACar

CREATE TABLE Cliente (
   id INT NOT NULL PRIMARY KEY,
   nome VARCHAR(100) NOT NULL,
   email VARCHAR(100) NOT NULL,
   telefone INT NOT NULL,
   NIF INT NOT NULL,
   data_cliente DATE NOT NULL,
   nascimento DATE NOT NULL,
   morada VARCHAR(20)
);

CREATE TABLE list_telefonica (
   id INT NOT NULL PRIMARY KEY,
   id_cliente INT NOT NULL FOREIGN KEY REFERENCES Cliente(id),
   telefone VARCHAR(20) NOT NULL
);


CREATE TABLE Gestor (
   id INT NOT NULL PRIMARY KEY,
   nome VARCHAR(100) NOT NULL,
   email VARCHAR(100) NOT NULL,
--   id_telefone INT NOT NULL FOREIGN KEY REFERENCES list_telefonica(id)
    telefone INT NOT NULL
);

CREATE TABLE marca (
   id INT NOT NULL PRIMARY KEY,
   marca VARCHAR(100) NOT NULL,
   modelo VARCHAR(100) NOT NULL
);

CREATE TABLE revisao (
   id INT NOT NULL PRIMARY KEY,
   prox_revisao DATE NOT NULL
);

CREATE TABLE inspecao (
   id INT NOT NULL PRIMARY KEY,
   ult_inspecao DATE NOT NULL
);

CREATE TABLE limpeza (
   id INT NOT NULL PRIMARY KEY,
   stat VARCHAR(20) NOT NULL
);

CREATE TABLE categoria (
   id INT NOT NULL PRIMARY KEY,
   categoria VARCHAR(100) NOT NULL
);

CREATE TABLE specs (
    id INT NOT NULL PRIMARY KEY,
    cilindrada INT NOT NULL,
    cavalos INT NOT NULL,
    combustivel varchar(20) not null
);

CREATE TABLE estado_aluguer(
   id INT NOT NULL PRIMARY KEY,
   estado varchar(50)
)
CREATE TABLE aluguer (
     id INT NOT NULL PRIMARY KEY,
     data_inic DATE NOT NULL,
     data_final DATE NOT NULL,
     estado INT NOT NULL FOREIGN KEY REFERENCES estado_aluguer(id),
     id_cliente INT NOT NULL FOREIGN KEY REFERENCES Cliente(id)
)

CREATE TABLE Veiculos (
   id INT NOT NULL PRIMARY KEY,
   id_cliente INT NOT NULL ,
   id_marca INT NOT NULL FOREIGN KEY REFERENCES marca(id),
   id_specs INT NOT NULL FOREIGN KEY REFERENCES specs(id),
   id_aluguer INT NOT NULL FOREIGN KEY REFERENCES aluguer(id),
   matricula VARCHAR(8) NOT NULL,
   kms INT NOT NULL,
   id_revisao INT FOREIGN KEY REFERENCES revisao(id),
   id_inspecao INT FOREIGN KEY REFERENCES inspecao(id),
   id_limpeza INT FOREIGN KEY REFERENCES limpeza(id),
   id_categoria INT NOT NULL FOREIGN KEY REFERENCES categoria(id),
   disponibilidade INT NOT NULL,
);

CREATE TABLE Veiculo_disponibilidade (
    id INT NOT NULL,
    disponibilidade VARCHAR(50)
)

CREATE TABLE Reserva (
   id_reserva INT NOT NULL PRIMARY KEY,
   data_inicio DATE NOT NULL,
   data_fim DATE NOT NULL,
   id_gestor INT NOT NULL FOREIGN KEY REFERENCES Gestor(id),
   id_cliente INT NOT NULL FOREIGN KEY REFERENCES Cliente(id),
   id_veiculo INT NOT NULL FOREIGN KEY REFERENCES Veiculos(id)
);


CREATE TABLE estado_pagamento(
                                 id INT NOT NULL PRIMARY KEY,
                                 estado VARCHAR(50)
)

create table tipo_pagamento(
                               id INT NOT NULL PRIMARY KEY ,
                               tipo varchar(20)
)

CREATE TABLE pagamento (
    id INT NOT NULL PRIMARY KEY,
    id_cliente INT NOT NULL FOREIGN KEY references Cliente(id),
    id_veiculo INT NOT NULL FOREIGN KEY REFERENCES Veiculos(id),
    id_gestor INT NOT NULL FOREIGN KEY REFERENCES Gestor(id),
    id_aluguer INT NOT NULL FOREIGN KEY REFERENCES aluguer(id),
    mes INT NOT NULL,
    ano INT NOT NULL ,
    estado INT NOT NULL,
    tipo_pagamento INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL ,
    fatura varchar(250)
);
