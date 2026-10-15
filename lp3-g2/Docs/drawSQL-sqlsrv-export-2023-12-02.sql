CREATE TABLE "Pais"(
    "id" INT NOT NULL,
    "Pais" VARCHAR NOT NULL,
    "taxa" FLOAT NOT NULL,
    "Tipo_moeda" VARCHAR NOT NULL
);
ALTER TABLE
    "Pais" ADD CONSTRAINT "pais_id_primary" PRIMARY KEY("id");
CREATE TABLE "Funcionarios"(
    "id" INT NOT NULL,
    "id_permissao" INT NOT NULL,
    "id_dados" INT NOT NULL,
    "id_acesso" INT NOT NULL,
    "ativo" NCHAR(255) NOT NULL
);
ALTER TABLE
    "Funcionarios" ADD CONSTRAINT "funcionarios_id_primary" PRIMARY KEY("id");
CREATE TABLE "permissao"(
    "id" INT NOT NULL,
    "Tipo" VARCHAR NOT NULL
);
ALTER TABLE
    "permissao" ADD CONSTRAINT "permissao_id_primary" PRIMARY KEY("id");
CREATE TABLE "UOM"(
    "id" INT NOT NULL,
    "Tipo" VARCHAR NOT NULL
);
ALTER TABLE
    "UOM" ADD CONSTRAINT "uom_id_primary" PRIMARY KEY("id");
CREATE TABLE "acesso"(
    "id" INT NOT NULL,
    "username" VARCHAR NOT NULL,
    "password" VARCHAR NOT NULL
);
ALTER TABLE
    "acesso" ADD CONSTRAINT "acesso_id_primary" PRIMARY KEY("id");
CREATE TABLE "rel_Informacao_UOM_Stock"(
    "id" INT NOT NULL,
    "id_stock" INT NOT NULL,
    "id_UOM" INT NOT NULL,
    "valor" FLOAT NOT NULL,
    "id_informacao" INT NOT NULL
);
ALTER TABLE
    "rel_Informacao_UOM_Stock" ADD CONSTRAINT "rel_informacao_uom_stock_id_primary" PRIMARY KEY("id");
CREATE TABLE "Encomenda"(
    "id" INT NOT NULL,
    "id_funcionario" INT NOT NULL,
    "data" DATE NOT NULL,
    "id_pais" INT NOT NULL
);
ALTER TABLE
    "Encomenda" ADD CONSTRAINT "encomenda_id_primary" PRIMARY KEY("id");
CREATE TABLE "Agencia"(
    "id" INT NOT NULL,
    "Tipo" VARCHAR NOT NULL,
    "PartNumber" VARCHAR NOT NULL,
    "id_Produto" INT NOT NULL
);
ALTER TABLE
    "Agencia" ADD CONSTRAINT "agencia_id_primary" PRIMARY KEY("id");
CREATE TABLE "Dados_pessoais"(
    "id" INT NOT NULL,
    "Nome" VARCHAR NOT NULL,
    "Endereco" VARCHAR NOT NULL,
    "Endereco2" VARCHAR NOT NULL,
    "Cidade" VARCHAR NOT NULL,
    "id_pais" INT NOT NULL,
    "Telefone" INT NOT NULL,
    "Email" VARCHAR NOT NULL,
    "cod_postal" INT NOT NULL,
    "Data_nascimento" DATE NOT NULL
);
ALTER TABLE
    "Dados_pessoais" ADD CONSTRAINT "dados_pessoais_id_primary" PRIMARY KEY("id");
CREATE TABLE "rel_UOM_stock"(
    "id" INT NOT NULL,
    "id_stock" INT NOT NULL,
    "id_UOM" INT NOT NULL
);
ALTER TABLE
    "rel_UOM_stock" ADD CONSTRAINT "rel_uom_stock_id_primary" PRIMARY KEY("id");
CREATE TABLE "Informacao_adicional"(
    "id" INT NOT NULL,
    "Tipo" VARCHAR NOT NULL
);
ALTER TABLE
    "Informacao_adicional" ADD CONSTRAINT "informacao_adicional_id_primary" PRIMARY KEY("id");
CREATE TABLE "Stock"(
    "id" INT NOT NULL,
    "id_produto" INT NOT NULL,
    "quantidade" INT NOT NULL,
    "rejeitado" NVARCHAR(255) NOT NULL,
    "Total" INT NOT NULL
);
ALTER TABLE
    "Stock" ADD CONSTRAINT "stock_id_primary" PRIMARY KEY("id");
CREATE TABLE "Produto"(
    "id" INT NOT NULL,
    "Nome" VARCHAR NOT NULL,
    "Preco_uni" FLOAT NOT NULL,
    "id_Encomenda" INT NOT NULL
);
ALTER TABLE
    "Produto" ADD CONSTRAINT "produto_id_primary" PRIMARY KEY("id");
CREATE TABLE "Clientes"(
    "id" INT NOT NULL,
    "id_dados" INT NOT NULL,
    "ativo" NCHAR(255) NOT NULL
);
ALTER TABLE
    "Clientes" ADD CONSTRAINT "clientes_id_primary" PRIMARY KEY("id");
ALTER TABLE
    "rel_Informacao_UOM_Stock" ADD CONSTRAINT "rel_informacao_uom_stock_id_stock_foreign" FOREIGN KEY("id_stock") REFERENCES "Stock"("id");
ALTER TABLE
    "rel_UOM_stock" ADD CONSTRAINT "rel_uom_stock_id_uom_foreign" FOREIGN KEY("id_UOM") REFERENCES "UOM"("id");
ALTER TABLE
    "rel_Informacao_UOM_Stock" ADD CONSTRAINT "rel_informacao_uom_stock_id_informacao_foreign" FOREIGN KEY("id_informacao") REFERENCES "Informacao_adicional"("id");
ALTER TABLE
    "Clientes" ADD CONSTRAINT "clientes_id_dados_foreign" FOREIGN KEY("id_dados") REFERENCES "Dados_pessoais"("id");
ALTER TABLE
    "Funcionarios" ADD CONSTRAINT "funcionarios_id_dados_foreign" FOREIGN KEY("id_dados") REFERENCES "Dados_pessoais"("id");
ALTER TABLE
    "rel_Informacao_UOM_Stock" ADD CONSTRAINT "rel_informacao_uom_stock_id_uom_foreign" FOREIGN KEY("id_UOM") REFERENCES "UOM"("id");
ALTER TABLE
    "Dados_pessoais" ADD CONSTRAINT "dados_pessoais_id_pais_foreign" FOREIGN KEY("id_pais") REFERENCES "Pais"("id");
ALTER TABLE
    "Funcionarios" ADD CONSTRAINT "funcionarios_id_acesso_foreign" FOREIGN KEY("id_acesso") REFERENCES "acesso"("id");
ALTER TABLE
    "Funcionarios" ADD CONSTRAINT "funcionarios_id_permissao_foreign" FOREIGN KEY("id_permissao") REFERENCES "permissao"("id");
ALTER TABLE
    "Agencia" ADD CONSTRAINT "agencia_id_produto_foreign" FOREIGN KEY("id_Produto") REFERENCES "Produto"("id");
ALTER TABLE
    "Stock" ADD CONSTRAINT "stock_id_produto_foreign" FOREIGN KEY("id_produto") REFERENCES "Produto"("id");
ALTER TABLE
    "Encomenda" ADD CONSTRAINT "encomenda_id_pais_foreign" FOREIGN KEY("id_pais") REFERENCES "Pais"("id");
ALTER TABLE
    "Encomenda" ADD CONSTRAINT "encomenda_id_funcionario_foreign" FOREIGN KEY("id_funcionario") REFERENCES "Funcionarios"("id");
ALTER TABLE
    "rel_UOM_stock" ADD CONSTRAINT "rel_uom_stock_id_stock_foreign" FOREIGN KEY("id_stock") REFERENCES "Stock"("id");