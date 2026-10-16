BEGIN

    -- Insert ClientType
    INSERT INTO ClientType (CID, Type) VALUES ('C1', 'Individual');
    INSERT INTO ClientType (CID, Type) VALUES ('C2', 'Company');

    -- Insert Country
    INSERT INTO Country (CountryCode, Name) VALUES ('PT', 'Portugal');
    INSERT INTO Country (CountryCode, Name) VALUES ('CZ', 'Czechia');

    -- Insert Clients
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (456, 'Carvalho & Carvalho, Lda', 'PT501245987', 'C2', 'idont@care.com', 003518340500, 1);
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (785, 'Tudo para a casa, Lda', 'PT501245488', 'C2', 'me@neither.com', 003518340500, 1);
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (657, 'Sair de Cena', 'PT501242417', 'C2', 'some@email.com', 003518340500, 1);
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (348, 'U Fleku', 'CZ6451237810', 'C2', 'some.random@email.cz', 004201234567, 1);

    -- Insert Addresses
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('Tv. Augusto Lessa 23', '4200-047', 'Porto', 'PT', 'PT501245987');
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('R. Dr. Barros 93', '4465-219', 'São Mamede de Infesta', 'PT', 'PT501245488');
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('EDIFICIO CRISTAL lj18, R. António Correia de Carvalho 88', '4400-023', 'Vila Nova de Gaia', 'PT', 'PT501242417');
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('Křemencova 11', '110 00', 'Nové Město', 'CZ', 'CZ6451237810');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/