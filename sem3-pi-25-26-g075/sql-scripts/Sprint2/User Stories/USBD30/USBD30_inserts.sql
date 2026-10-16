-- ==================================================================================
-- DADOS DE TESTES PARA A USBD30
-- ==================================================================================

-- CASO 1 (POSITIVO): Instalação SÓ com Silo (Deve aparecer)
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (70, 'Agro Terminal Norte', 'Sim');
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (70, 3); -- 3 = grain silo

-- CASO 2 (POSITIVO): Instalação com Silo E Outra coisa (que não armazém) (Deve aparecer)
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (71, 'Porto Frio de Aveiro', 'Sim');
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (71, 3); -- 3 = grain silo
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (71, 2); -- 2 = refrigerated area

-- CASO 3 (NEGATIVO - CRUCIAL): Instalação com Silo E Armazém (NÃO deve aparecer)
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (60, 'Terminal de Bobadela', 'Sim');
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (60, 3); -- 3 = grain silo
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (60, 1); -- 1 = warehouse

-- CASO 4 (NEGATIVO): Instalação SÓ com Armazém (NÃO deve aparecer)
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (62, 'Armazém Central', 'Nao');
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (62, 1); -- 1 = warehouse

COMMIT;