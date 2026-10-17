import xml.etree.ElementTree as ET
import logging
import re

# Configurar logging
logging.basicConfig(filename='script_log.log', level=logging.INFO,
                    format='%(asctime)s %(levelname)s:%(message)s')

def escape_sql(value):
    """Escapa aspas simples para uso em comandos SQL."""
    if value is None:
        return 'NULL'
    return "'" + value.replace("'", "''") + "'"

def strip_quotes(value):
    """Remove aspas duplas ou simples do início e fim da string."""
    if value is None:
        return value
    value = value.strip()
    if (value.startswith('"') and value.endswith('"')) or (value.startswith("'") and value.endswith("'")):
        return value[1:-1]
    return value

def extract_unit(description):
    """
    Extrai a unidade da descrição da peça utilizando expressões regulares.
    Suporta unidades comuns como mm, cm, l, ml, unit, etc.
    Retorna a unidade encontrada ou 'unit' como padrão.
    """
    # Definir um padrão de regex para unidades
    # Pode ser ajustado conforme as necessidades específicas dos dados
    pattern = r'\b(mm|cm|l|ml|unit)\b'
    match = re.search(pattern, description, re.IGNORECASE)
    if match:
        return match.group(1).lower()
    else:
        return 'unit'  # Padrão caso nenhuma unidade seja encontrada

def process_parts(xml_file):
    """Processa o arquivo parts XML e retorna os dados estruturados."""
    try:
        with open(xml_file, 'r', encoding='utf-8-sig') as f:
            tree = ET.parse(f)
    except ET.ParseError as e:
        logging.error(f"Erro ao analisar o ficheiro {xml_file}: {e}")
        return None
    root = tree.getroot()

    product_families = {}
    parts = []
    components = []
    products = []
    intermediate_products = []
    raw_materials = []
    part_units = {}  # Dicionário para armazenar unidades por PartID

    for part in root.findall('part'):
        part_number_elem = part.find('part_number')
        description_elem = part.find('description')
        part_type_elem = part.find('part_type')

        if part_number_elem is None or description_elem is None or part_type_elem is None:
            logging.warning(f"Parte incompleta encontrada: {ET.tostring(part, encoding='unicode')}")
            continue

        part_number = strip_quotes(part_number_elem.text)
        description = strip_quotes(description_elem.text)
        part_type = strip_quotes(part_type_elem.text)

        # Extrair a unidade da descrição
        unit = extract_unit(description)
        part_units[part_number] = unit  # Associar a unidade ao PartID

        parts.append((part_number, description))

        if part_type == "Product":
            name_elem = part.find('name')
            family_elem = part.find('family')
            if name_elem is None or family_elem is None:
                logging.warning(f"Produto incompleto encontrado: {ET.tostring(part, encoding='unicode')}")
                continue
            name = strip_quotes(name_elem.text)
            family = strip_quotes(family_elem.text)
            family_id = family if family else None
            products.append((name, family_id, part_number))
            if family:
                product_families[family] = f"Family {family}"  # Placeholder, ajustar conforme necessário
        elif part_type == "Component":
            components.append(part_number)
        elif part_type == "Intermediate Product":
            intermediate_products.append(part_number)
        elif part_type == "Raw Material":
            raw_materials.append(part_number)
        else:
            logging.warning(f"Tipo de parte desconhecido: {part_type} para PartID: {part_number}")

    return {
        "product_families": product_families,
        "parts": parts,
        "components": components,
        "products": products,
        "intermediate_products": intermediate_products,
        "raw_materials": raw_materials,
        "part_units": part_units  # Incluir unidades extraídas
    }

def process_operations(xml_file):
    """Processa o arquivo operations XML e retorna os dados estruturados."""
    try:
        with open(xml_file, 'r', encoding='utf-8-sig') as f:
            tree = ET.parse(f)
    except ET.ParseError as e:
        logging.error(f"Erro ao analisar o ficheiro {xml_file}: {e}")
        return None
    root = tree.getroot()

    workstation_types = {}
    operation_types = {}
    wstypes_operationtypes = []

    for operation_type in root.findall('operation_type'):
        otid = strip_quotes(operation_type.get('id'))
        operation_desc_elem = operation_type.find('operation_desc')
        if operation_desc_elem is None:
            logging.warning(f"OperationType sem descrição encontrada: {ET.tostring(operation_type, encoding='unicode')}")
            continue
        operation_desc = strip_quotes(operation_desc_elem.text)

        operation_types[otid] = operation_desc

        wt_list = operation_type.find('workstation_type_list')
        if wt_list is not None:
            for wt in wt_list.findall('workstation_type'):
                wtid = strip_quotes(wt.text)
                if wtid:
                    # Adiciona WorkstationType com placeholder de nome
                    if wtid not in workstation_types:
                        workstation_types[wtid] = f"Workstation {wtid}"  # Placeholder
                    # Adiciona relação na tabela de associação
                    wstypes_operationtypes.append((wtid, otid))
        else:
            logging.warning(f"OperationType {otid} não possui workstation_type_list.")

    return {
        "workstation_types": workstation_types,
        "operation_types": operation_types,
        "wstypes_operationtypes": wstypes_operationtypes
    }

def process_boo(xml_file):
    """Processa o arquivo boo XML e retorna os dados estruturados."""
    try:
        with open(xml_file, 'r', encoding='utf-8-sig') as f:
            tree = ET.parse(f)
    except ET.ParseError as e:
        logging.error(f"Erro ao analisar o ficheiro {xml_file}: {e}")
        return None
    root = tree.getroot()

    boos = []
    operations = []
    boo_operations = []
    part_ins = []
    part_outs = []

    for boo in root.findall('boo'):
        booid = strip_quotes(boo.get('id'))
        if not booid:
            logging.warning(f"BOO sem ID encontrado: {ET.tostring(boo, encoding='unicode')}")
            continue
        boos.append(booid)

        operation_list = boo.find('operation_list')
        if operation_list is None:
            logging.warning(f"BOO {booid} não possui operation_list.")
            continue

        for operation in operation_list.findall('operation'):
            opid = strip_quotes(operation.get('id'))
            optype_id_elem = operation.find('optype_id')
            if optype_id_elem is None:
                logging.warning(f"Operação {opid} em BOO {booid} não possui optype_id.")
                continue
            optype_id = strip_quotes(optype_id_elem.text)

            next_op_text = strip_quotes(operation.find('next_op').text) if operation.find('next_op') is not None else None
            next_op = next_op_text if next_op_text and next_op_text.lower() != "none" else None

            operations.append({
                "opid": opid,
                "optype_id": optype_id,
                "next_op": next_op
            })

            boo_operations.append((opid, booid))

            # Process inputs
            input_list = operation.find('input_list')
            if input_list is not None:
                for inp in input_list.findall('input'):
                    part_elem = inp.find('part')
                    quantity_elem = inp.find('quantity')
                    # unit_elem = inp.find('unit')  # Remover esta linha, pois usaremos a unidade da Part
                    if part_elem is None or quantity_elem is None:
                        logging.warning(f"Entrada incompleta na operação {opid} em BOO {booid}.")
                        continue
                    part = strip_quotes(part_elem.text)
                    quantity = strip_quotes(quantity_elem.text)
                    # unit = strip_quotes(unit_elem.text)  # Remover esta linha
                    part_ins.append({
                        "part": part,
                        "opid": opid,
                        "quantity": quantity,
                        # "unit": unit  # Remover esta linha
                    })

            # Process output
            output = operation.find('output')
            if output is not None:
                out_part_elem = output.find('part')
                out_quantity_elem = output.find('quantity')
                # out_unit_elem = output.find('unit')  # Remover esta linha
                if out_part_elem is None or out_quantity_elem is None:
                    logging.warning(f"Saída incompleta na operação {opid} em BOO {booid}.")
                    continue
                out_part = strip_quotes(out_part_elem.text)
                out_quantity = strip_quotes(out_quantity_elem.text)
                # out_unit = strip_quotes(out_unit_elem.text)  # Remover esta linha
                part_outs.append({
                    "part": out_part,
                    "opid": opid,
                    "quantity": out_quantity,
                    # "unit": out_unit  # Remover esta linha
                })

    return {
        "boos": boos,
        "operations": operations,
        "boo_operations": boo_operations,
        "part_ins": part_ins,
        "part_outs": part_outs
    }

def generate_sql(data_parts, data_operations, data_boo):
    """Gera comandos SQL `INSERT` agrupados por tabela e ordenados conforme dependências dentro de uma transação PL/SQL."""
    sql_commands = []
    
    # Iniciar o bloco PL/SQL
    sql_commands.append("BEGIN")
    
    # 1. Inserir ProductFamily
    if data_parts["product_families"]:
        sql_commands.append("    -- Inserir ProductFamily")
        for pfid, name in data_parts["product_families"].items():
            insert_pf = f"    INSERT INTO ProductFamily (PFID, Name) VALUES ({pfid}, {escape_sql(name)});"
            sql_commands.append(insert_pf)
        sql_commands.append("")  # Linha em branco
    
    # 2. Inserir WorkstationTypes
    if data_operations["workstation_types"]:
        sql_commands.append("    -- Inserir WorkstationTypes")
        for wtid, name in data_operations["workstation_types"].items():
            insert_wt = f"    INSERT INTO WorkstationTypes (WTID, Name) VALUES ({escape_sql(wtid)}, {escape_sql(name)});"
            sql_commands.append(insert_wt)
        sql_commands.append("")
    
    # 3. Inserir OperationType
    if data_operations["operation_types"]:
        sql_commands.append("    -- Inserir OperationType")
        for otid, desc in data_operations["operation_types"].items():
            insert_ot = f"    INSERT INTO OperationType (OTID, Description) VALUES ({otid}, {escape_sql(desc)});"
            sql_commands.append(insert_ot)
        sql_commands.append("")
    
    # 4. Inserir WSTypes_OperationTypes
    if data_operations["wstypes_operationtypes"]:
        sql_commands.append("    -- Inserir WSTypes_OperationTypes")
        for wtid, otid in data_operations["wstypes_operationtypes"]:
            insert_wst_ot = f"    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ({escape_sql(wtid)}, {otid});"
            sql_commands.append(insert_wst_ot)
        sql_commands.append("")
    
    # 5. Inserir Part
    if data_parts["parts"]:
        sql_commands.append("    -- Inserir Part")
        for partid, desc in data_parts["parts"]:
            insert_part = f"    INSERT INTO Part (PartID, Description) VALUES ({escape_sql(partid)}, {escape_sql(desc)});"
            sql_commands.append(insert_part)
        sql_commands.append("")
    
    # 6. Inserir Component
    if data_parts["components"]:
        sql_commands.append("    -- Inserir Component")
        for partid in data_parts["components"]:
            insert_component = f"    INSERT INTO Component (PartID) VALUES ({escape_sql(partid)});"
            sql_commands.append(insert_component)
        sql_commands.append("")
    
    # 7. Inserir Product
    if data_parts["products"]:
        sql_commands.append("    -- Inserir Product")
        for name, pfid, partid in data_parts["products"]:
            pfid_sql = pfid if pfid else 'NULL'
            insert_product = f"    INSERT INTO Product (Name, PFID, PartID) VALUES ({escape_sql(name)}, {pfid_sql}, {escape_sql(partid)});"
            sql_commands.append(insert_product)
        sql_commands.append("")
    
    # 8. Inserir IntermediateProduct
    if data_parts["intermediate_products"]:
        sql_commands.append("    -- Inserir IntermediateProduct")
        for partid in data_parts["intermediate_products"]:
            insert_intermediate = f"    INSERT INTO IntermediateProduct (PartID) VALUES ({escape_sql(partid)});"
            sql_commands.append(insert_intermediate)
        sql_commands.append("")
    
    # 9. Inserir RawMaterial
    if data_parts["raw_materials"]:
        sql_commands.append("    -- Inserir RawMaterial")
        for partid in data_parts["raw_materials"]:
            insert_raw = f"    INSERT INTO RawMaterial (PartID) VALUES ({escape_sql(partid)});"
            sql_commands.append(insert_raw)
        sql_commands.append("")
    
    # 10. Inserir BOO
    if data_boo["boos"]:
        sql_commands.append("    -- Inserir BOO")
        for booid in data_boo["boos"]:
            # Assumindo que BOO.PartID é o mesmo que BOOID
            insert_boo = f"    INSERT INTO BOO (BOOID, PartID) VALUES ({escape_sql(booid)}, {escape_sql(booid)});"
            sql_commands.append(insert_boo)
        sql_commands.append("")
    
    # 11. Inserir Operation (sem NextOPID)
    if data_boo["operations"]:
        sql_commands.append("    -- Inserir Operation (sem NextOPID)")
        for op in data_boo["operations"]:
            opid = op["opid"]
            optype_id = op["optype_id"]
            insert_op = f"    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES ({opid}, (SELECT Description FROM OperationType WHERE OTID = {optype_id}), {optype_id}, NULL);"
            sql_commands.append(insert_op)
        sql_commands.append("")
    
    # 12. Inserir BOO_Operation
    if data_boo["boo_operations"]:
        sql_commands.append("    -- Inserir BOO_Operation")
        for opid, booid in data_boo["boo_operations"]:
            insert_boo_op = f"    INSERT INTO BOO_Operation (OPID, BOOID) VALUES ({opid}, {escape_sql(booid)});"
            sql_commands.append(insert_boo_op)
        sql_commands.append("")
    
    # 13. Atualizar NextOPID nas Operações
    if data_boo["operations"]:
        sql_commands.append("    -- Atualizar NextOPID nas Operações")
        for op in data_boo["operations"]:
            opid = op["opid"]
            next_op = op["next_op"]
            if next_op:
                update_op = f"    UPDATE Operation SET NextOPID = {next_op} WHERE OPID = {opid};"
                sql_commands.append(update_op)
        sql_commands.append("")
    
    # 14. Inserir PartIN
    if data_boo["part_ins"]:
        sql_commands.append("    -- Inserir PartIN")
        for pin in data_boo["part_ins"]:
            part = pin["part"]
            opid = pin["opid"]
            quantity = pin["quantity"]
            # Obter a unidade a partir do PartID
            unit = data_parts["part_units"].get(part, 'unit')  # Default para 'unit' se não encontrar
            insert_part_in = f"    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ({escape_sql(part)}, {opid}, {quantity}, {escape_sql(unit)});"
            sql_commands.append(insert_part_in)
        sql_commands.append("")
    
    # 15. Inserir PartOut
    if data_boo["part_outs"]:
        sql_commands.append("    -- Inserir PartOut")
        for pout in data_boo["part_outs"]:
            part = pout["part"]
            opid = pout["opid"]
            quantity = pout["quantity"]
            # Obter a unidade a partir do PartID
            unit = data_parts["part_units"].get(part, 'unit')  # Default para 'unit' se não encontrar
            insert_part_out = f"    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ({escape_sql(part)}, {opid}, {quantity}, {escape_sql(unit)});"
            sql_commands.append(insert_part_out)
        sql_commands.append("")
    
    # Adicionar COMMIT ao final do bloco
    sql_commands.append("    COMMIT;")
    
    # Adicionar tratamento de exceções
    sql_commands.append("EXCEPTION")
    sql_commands.append("    WHEN OTHERS THEN")
    sql_commands.append("        ROLLBACK;")
    sql_commands.append("        RAISE;")
    sql_commands.append("END;")
    sql_commands.append("/")
    
    return sql_commands

def main():
    parts_xml = 'Dataset_S2_parts_V03a.xml'
    operations_xml = 'Dataset_S2_operations_V01.xml'
    boo_xml = 'Dataset_S2_boo_V03.xml'
    inserts_sql = 'inserts.sql'

    # Processar os arquivos XML
    logging.info("Iniciando o processamento dos arquivos XML.")
    data_parts = process_parts(parts_xml)
    data_operations = process_operations(operations_xml)
    data_boo = process_boo(boo_xml)

    if not data_parts or not data_operations or not data_boo:
        logging.error("Erro no processamento dos arquivos XML. Verifique as mensagens acima.")
        return

    # Gerar os comandos SQL
    sql_commands = generate_sql(data_parts, data_operations, data_boo)

    # Escrever no arquivo SQL
    try:
        with open(inserts_sql, 'w', encoding='utf-8') as sql_file:
            sql_file.write("-- Inserções geradas automaticamente pelo script Python\n\n")
            for command in sql_commands:
                sql_file.write(command + "\n")
        logging.info(f"Script SQL gerado com sucesso em '{inserts_sql}'.")
        print(f"Script SQL gerado com sucesso em '{inserts_sql}'.")
    except Exception as e:
        logging.error(f"Erro ao escrever no arquivo SQL: {e}")
        print(f"Erro ao escrever no arquivo SQL: {e}")

if __name__ == "__main__":
    main()
