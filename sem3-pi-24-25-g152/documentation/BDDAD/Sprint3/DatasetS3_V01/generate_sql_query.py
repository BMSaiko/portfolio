import xml.etree.ElementTree as ET
import os
from datetime import datetime

def escape_quotes(text):
    """Escape single quotes in text for SQL."""
    if text is None:
        return 'NULL'
    return f"'{text.replace('\'', '\'\'')}'"

def format_date(date_str):
    """Convert date from 'YYYY/MM/DD' or 'None' to 'YYYY-MM-DD' or NULL."""
    if date_str is None or date_str in ['NULL', 'None']:
        return 'NULL'
    try:
        return f"'{datetime.strptime(date_str, '%Y/%m/%d').strftime('%Y-%m-%d')}'"
    except ValueError:
        return 'NULL'

def parse_client_list(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    
    inserts = {}
    
    for client in root.findall('client'):
        id_ = client.get('ID')
        name = client.find('name').text.strip('"') if client.find('name') is not None else None
        vatin = client.find('vatin').text.strip('"') if client.find('vatin') is not None else None
        address = client.find('adress').text.strip('"') if client.find('adress') is not None else None
        zip_code = client.find('zip').text.strip('"') if client.find('zip') is not None else None
        town = client.find('town').text.strip('"') if client.find('town') is not None else None
        country = client.find('country').text.strip('"') if client.find('country') is not None else None
        email = client.find('email').text.strip('"') if client.find('email') is not None else None
        phone = client.find('phone').text if client.find('phone') is not None else None
        
        # Insert into COUNTRY
        # Ideally, use a proper mapping from country names to country codes
        country_code = country[:2].upper() if country else 'XX'
        country_insert = f"INSERT INTO COUNTRY (COUNTRYCODE, NAME) VALUES ('{country_code}', {escape_quotes(country)});"
        inserts.setdefault('COUNTRY', []).append(country_insert)
        
        # Insert into CLIENT
        # Ensure phone numbers fit into NUMBER(10). If they have leading zeros or are longer, consider changing the data type.
        phone_numeric = ''.join(filter(str.isdigit, phone)) if phone else 'NULL'
        phone_numeric = phone_numeric if phone_numeric != 'NULL' else 'NULL'
        if phone_numeric != 'NULL' and len(phone_numeric) > 10:
            phone_numeric = phone_numeric[:10]  # Truncate if necessary
        
        client_insert = (
            f"INSERT INTO CLIENT (NIF, NAME, EMAIL, PHONE, IDCLIENT, CID, STATUS) "
            f"VALUES ({escape_quotes(vatin)}, {escape_quotes(name)}, {escape_quotes(email)}, "
            f"{phone_numeric if phone_numeric != 'NULL' else 'NULL'}, {id_}, '01', 1);"
        )
        inserts.setdefault('CLIENT', []).append(client_insert)
        
        # Insert into ADDRESS
        address_insert = (
            f"INSERT INTO ADDRESS (ADDRESS, ZIP, TOWN, COUNTRYCODE, CLIENTNIF) "
            f"VALUES ({escape_quotes(address)}, {escape_quotes(zip_code)}, {escape_quotes(town)}, "
            f"'{country_code}', {escape_quotes(vatin)});"
        )
        inserts.setdefault('ADDRESS', []).append(address_insert)
    
    return inserts

def parse_operation_type_list(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    
    inserts = {}
    
    for op_type in root.findall('operation_type'):
        otid = op_type.get('id')
        description = op_type.find('operation_desc').text.strip('"') if op_type.find('operation_desc') is not None else None
        
        # Insert into OPERATIONTYPE
        op_type_insert = f"INSERT INTO OPERATIONTYPE (OTID, DESCRIPTION) VALUES ({otid}, {escape_quotes(description)});"
        inserts.setdefault('OPERATIONTYPE', []).append(op_type_insert)
        
        # Insert into WSTYPES_OPERATIONTYPES
        workstation_type_list = op_type.find('workstation_type_list')
        if workstation_type_list is not None:
            for ws_type in workstation_type_list.findall('workstation_type'):
                ws_id = ws_type.text.strip('"')
                wst_op_insert = f"INSERT INTO WSTYPES_OPERATIONTYPES (WTID, OPERATIONTYPEOTID) VALUES ('{ws_id}', {otid});"
                inserts.setdefault('WSTYPES_OPERATIONTYPES', []).append(wst_op_insert)
    
    return inserts

def parse_part_list(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    
    inserts = {}
    
    for part in root.findall('part'):
        part_number = part.find('part_number').text.strip('"') if part.find('part_number') is not None else None
        description = part.find('description').text.strip('"') if part.find('description') is not None else None
        part_type = part.find('part_type').text.strip('"') if part.find('part_type') is not None else None
        name = part.find('name').text.strip('"') if part.find('name') is not None else None
        family = part.find('family').text if part.find('family') is not None else None
        
        if part_type == "Component":
            # Insert into PART
            part_insert = f"INSERT INTO PART (PARTID, DESCRIPTION) VALUES ({escape_quotes(part_number)}, {escape_quotes(description)});"
            inserts.setdefault('PART', []).append(part_insert)
            
            # Insert into COMPONENT
            component_insert = f"INSERT INTO COMPONENT (PARTID) VALUES ({escape_quotes(part_number)});"
            inserts.setdefault('COMPONENT', []).append(component_insert)
        
        elif part_type == "Intermediate Product":
            # Insert into INTERMEDIATEPRODUCT
            intermediate_insert = f"INSERT INTO INTERMEDIATEPRODUCT (PARTID) VALUES ({escape_quotes(part_number)});"
            inserts.setdefault('INTERMEDIATEPRODUCT', []).append(intermediate_insert)
            
            # Insert into PART
            part_insert = f"INSERT INTO PART (PARTID, DESCRIPTION) VALUES ({escape_quotes(part_number)}, {escape_quotes(description)});"
            inserts.setdefault('PART', []).append(part_insert)
        
        elif part_type == "Internal Part":
            # Insert into INTERNALPART
            internal_insert = f"INSERT INTO INTERNALPART (PARTID) VALUES ({escape_quotes(part_number)});"
            inserts.setdefault('INTERNALPART', []).append(internal_insert)
            
            # Insert into PART
            part_insert = f"INSERT INTO PART (PARTID, DESCRIPTION) VALUES ({escape_quotes(part_number)}, {escape_quotes(description)});"
            inserts.setdefault('PART', []).append(part_insert)
        
        elif part_type == "Product":
            family_id = family if family else 'NULL'
            # Insert into PRODUCT
            product_insert = f"INSERT INTO PRODUCT (NAME, PFID, PARTID) VALUES ({escape_quotes(name)}, {family_id}, {escape_quotes(part_number)});"
            inserts.setdefault('PRODUCT', []).append(product_insert)
            
            # Insert into PART
            part_insert = f"INSERT INTO PART (PARTID, DESCRIPTION) VALUES ({escape_quotes(part_number)}, {escape_quotes(description)});"
            inserts.setdefault('PART', []).append(part_insert)
        
        elif part_type == "Raw Material":
            # Insert into RAWMATERIAL
            # Assuming UNITID is known, but not provided in XML. Setting to NULL or a default value.
            raw_material_insert = f"INSERT INTO RAWMATERIAL (PARTID, UNITID) VALUES ({escape_quotes(part_number)}, NULL);"
            inserts.setdefault('RAWMATERIAL', []).append(raw_material_insert)
            
            # Insert into PART
            part_insert = f"INSERT INTO PART (PARTID, DESCRIPTION) VALUES ({escape_quotes(part_number)}, {escape_quotes(description)});"
            inserts.setdefault('PART', []).append(part_insert)
    
    return inserts

def parse_boo_list(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    
    inserts = {}
    
    for boo in root.findall('boo'):
        booid = boo.get('id')
        
        # Insert into BOO
        # Assuming BOO needs a PARTID, which is the 'output' part of the first operation
        operation_list = boo.find('operation_list')
        if operation_list is not None:
            first_op = operation_list.find('operation')
            if first_op is not None:
                output_part_elem = first_op.find('output/part')
                output_part = output_part_elem.text.strip('"') if output_part_elem is not None else 'NULL'
            else:
                output_part = 'NULL'
        else:
            output_part = 'NULL'
        
        boo_insert = f"INSERT INTO BOO (BOOID, PARTID) VALUES ({escape_quotes(booid)}, {escape_quotes(output_part)});"
        inserts.setdefault('BOO', []).append(boo_insert)
        
        for operation in operation_list.findall('operation'):
            opid = operation.get('id')
            optype_id_elem = operation.find('optype_id')
            optype_id = optype_id_elem.text if optype_id_elem is not None else 'NULL'
            next_op_elem = operation.find('next_op')
            next_op = next_op_elem.text if next_op_elem is not None else 'NULL'
            next_op = next_op if next_op not in ['None', 'NULL'] else 'NULL'
            
            # Insert into OPERATION
            operation_insert = (
                f"INSERT INTO OPERATION (OPID, NAME, OPERATIONTYPEOTID, EXPECTEDEXECUTIONTIME) "
                f"VALUES ({opid}, 'Operation {opid}', {optype_id}, 0);"
            )
            inserts.setdefault('OPERATION', []).append(operation_insert)
            
            # Insert into BOO_OPERATION
            if next_op == 'NULL':
                next_op_value = 'NULL'
            else:
                next_op_value = next_op
            boo_op_insert = f"INSERT INTO BOO_OPERATION (OPID, BOOID, NEXTOPID) VALUES ({opid}, {escape_quotes(booid)}, {next_op_value if next_op_value == 'NULL' else next_op_value});"
            inserts.setdefault('BOO_OPERATION', []).append(boo_op_insert)
            
            # Insert into PARTIN based on inputs
            input_list = operation.find('input_list')
            if input_list is not None:
                for input_item in input_list.findall('input'):
                    part = input_item.find('part').text.strip('"') if input_item.find('part') is not None else None
                    quantity = input_item.find('quantity').text if input_item.find('quantity') is not None else 'NULL'
                    unit = input_item.find('unit').text.strip('"') if input_item.find('unit') is not None else None
                    
                    # Insert into PARTIN
                    partin_insert = (
                        f"INSERT INTO PARTIN (QUANTITYIN, OPID, BOOID, PARTID) "
                        f"VALUES ({quantity}, {opid}, {escape_quotes(booid)}, {escape_quotes(part)});"
                    )
                    inserts.setdefault('PARTIN', []).append(partin_insert)
            
            # Handle output by inserting into PARTOUT
            output = operation.find('output')
            if output is not None:
                out_part = output.find('part').text.strip('"') if output.find('part') is not None else None
                out_quantity = output.find('quantity').text if output.find('quantity') is not None else 'NULL'
                out_unit = output.find('unit').text.strip('"') if output.find('unit') is not None else None
                
                # Insert into PARTOUT
                partout_insert = (
                    f"INSERT INTO PARTOUT (QUANTITY, PARTID, OPID, BOOID) "
                    f"VALUES ({out_quantity}, {escape_quotes(out_part)}, {opid}, {escape_quotes(booid)});"
                )
                inserts.setdefault('PARTOUT', []).append(partout_insert)
    
    return inserts

def parse_procurement_list(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    
    inserts = {}
    
    for supplier in root.findall('supplier'):
        supplier_id = supplier.get('id')
        # Insert into SUPPLIER
        # The XML doesn't provide a name for suppliers. You might want to add this information if available.
        supplier_insert = f"INSERT INTO SUPPLIER (SUPPLIERID, NAME) VALUES ({supplier_id}, NULL);"
        inserts.setdefault('SUPPLIER', []).append(supplier_insert)
        
        part_list = supplier.find('part_list')
        if part_list is not None:
            for part in part_list.findall('part'):
                part_id = part.get('id')
                offer_list = part.find('offer_list')
                if offer_list is not None:
                    for offer in offer_list.findall('offer'):
                        start_date = format_date(offer.find('start_date').text)
                        end_date = format_date(offer.find('end_date').text)
                        min_quantity = offer.find('min_quantity').text if offer.find('min_quantity') is not None else 'NULL'
                        price = offer.find('price').text if offer.find('price') is not None else 'NULL'
                        
                        # Insert into PLACEDORDER
                        placed_order_insert = (
                            f"INSERT INTO PLACEDORDER (PARTID, SUPPLIERID, QUANTITY, UNITCOST, DATESTART, DATEEND, MINIMUMORDERSIZE) "
                            f"VALUES ({escape_quotes(part_id)}, {supplier_id}, {min_quantity}, {price}, {start_date}, {end_date}, {min_quantity});"
                        )
                        inserts.setdefault('PLACEDORDER', []).append(placed_order_insert)
    
    return inserts

def main():
    # Define XML file paths
    xml_files = {
        'client_list': 'Dataset_S3_clients_V01.xml',
        'operation_type_list': 'Dataset_S3_operations_V01.xml',
        'part_list': 'Dataset_S3_parts_V01.xml',
        'boo_list': 'Dataset_S3_boo_V01.xml',
        'procurement_list': 'Dataset_S3_procurement_V01.xml'
    }
    
    # Check if all files exist
    for key, file in xml_files.items():
        if not os.path.exists(file):
            print(f"Error: {file} not found.")
            return
    
    all_inserts = {}
    
    try:
        # Parse CLIENT_LIST
        client_inserts = parse_client_list(xml_files['client_list'])
        for table, stmts in client_inserts.items():
            all_inserts.setdefault(table, []).extend(stmts)
        
        # Parse OPERATION_TYPE_LIST
        operation_type_inserts = parse_operation_type_list(xml_files['operation_type_list'])
        for table, stmts in operation_type_inserts.items():
            all_inserts.setdefault(table, []).extend(stmts)
        
        # Parse PART_LIST
        part_inserts = parse_part_list(xml_files['part_list'])
        for table, stmts in part_inserts.items():
            all_inserts.setdefault(table, []).extend(stmts)
        
        # Parse BOO_LIST
        boo_inserts = parse_boo_list(xml_files['boo_list'])
        for table, stmts in boo_inserts.items():
            all_inserts.setdefault(table, []).extend(stmts)
        
        # Parse PROCUREMENT_LIST
        procurement_inserts = parse_procurement_list(xml_files['procurement_list'])
        for table, stmts in procurement_inserts.items():
            all_inserts.setdefault(table, []).extend(stmts)
        
    except ET.ParseError as e:
        print(f"XML Parse Error: {e}")
        return
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return
    
    # Define the insert order based on foreign key dependencies
    insert_order = [
        'CLIENTTYPE',          # No data handled in script; ensure it's populated before running inserts
        'COUNTRY',
        'OPERATIONTYPE',
        'PART',
        'PRODUCTFAMILY',       # No data handled in script; ensure it's populated before running inserts
        'SUPPLIER',
        'UNIT',                # No data handled in script; ensure it's populated before running inserts
        'WORKSTATIONTYPES',
        'CLIENT',
        'EXTERNALPARTS',       # Inserted via PART parsing
        'INTERNALPART',        # Inserted via PART parsing
        'OPERATION',
        'WORKSTATIONS',
        'WSTYPES_OPERATIONTYPES',
        'PRODUCT',
        'INTERMEDIATEPRODUCT',
        'BOO',
        'Order',               # Note: "Order" is a reserved keyword; ensure it's correctly handled
        'ORDERPRODUCT',
        'RAWMATERIAL',
        'ADDRESS',
        'BOO_OPERATION',
        'PRODUCTIONORDER',
        'PARTIN',
        'PARTOUT',
        'PLACEDORDER',
        'COMPONENT'
    ]
    
    # Note: CLIENTTYPE, PRODUCTFAMILY, and UNIT are not handled by the script.
    # Ensure these tables are populated before running the inserts.
    
    # Write all INSERT statements to a single SQL file in the correct order
    with open('inserts.sql', 'w', encoding='utf-8') as f:
        for table in insert_order:
            if table in all_inserts:
                for stmt in all_inserts[table]:
                    f.write(stmt + '\n')
        
        # Handle any remaining INSERTs not specified in insert_order
        remaining_tables = set(all_inserts.keys()) - set(insert_order)
        for table in remaining_tables:
            for stmt in all_inserts[table]:
                f.write(stmt + '\n')
    
    total_inserts = sum(len(stmts) for stmts in all_inserts.values())
    print(f"Generated {total_inserts} INSERT statements in 'inserts.sql'.")

if __name__ == "__main__":
    main()
