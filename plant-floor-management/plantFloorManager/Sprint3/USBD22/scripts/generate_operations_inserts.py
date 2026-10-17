import xml.etree.ElementTree as ET
import os

def generate_operations_inserts():
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    ops_xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_operations_V01.xml')
    boo_xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_boo_V02.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    
    # Set output path
    output_path = os.path.join(sql_dir, '05_operations_inserts.sql')
    
    tree_ops = ET.parse(ops_xml_path)
    tree_boo = ET.parse(boo_xml_path)
    
    # Create a dictionary to store operation type EETs from BOO file
    operation_eets = {}
    for boo in tree_boo.findall('.//boo'):
        for operation in boo.findall('.//operation'):
            op_id = operation.get('id')
            op_type_id = operation.find('optype_id').text.strip('"')
            eet = operation.find('eet').text
            if op_type_id not in operation_eets:
                operation_eets[op_type_id] = eet
    
    with open(output_path, 'w') as f:
        # First transaction: OperationType and WSTypes_OperationTypes
        f.write("BEGIN\n")
        
        # Insert OperationType
        f.write("    -- Insert OperationType\n")
        for op_type in tree_ops.findall('.//operation_type'):
            op_id = op_type.get('id')
            description = op_type.find('operation_desc').text.strip('"')  # Remove quotes
            
            f.write(f"""    INSERT INTO OperationType (OTID, Description)
    VALUES ({op_id}, '{description}');\n""")
            
            # Insert WSTypes_OperationTypes relationships
            f.write("\n    -- Insert WSTypes_OperationTypes for operation type {op_id}\n")
            for ws_type in op_type.findall('.//workstation_type'):
                ws_type_id = ws_type.text.strip('"')
                f.write(f"""    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID)
    VALUES ('{ws_type_id}', {op_id});\n""")
            f.write("\n")
        
        f.write("    COMMIT;\nEND;\n/\n\n")
        
        # Second transaction: Operations
        f.write("BEGIN\n")
        f.write("    -- Insert Operations\n")
        for boo in tree_boo.findall('boo'):
            for operation in boo.findall('.//operation'):
                op_id = operation.get('id')
                op_type_id = operation.find('optype_id').text.strip('"')
                eet = operation.find('eet').text  # Get EET directly from operation
                
                f.write(f"""    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES ({op_id}, 'Operation {op_id}', {op_type_id}, {eet});\n""")
        
        f.write("    COMMIT;\nEND;\n/\n\n")
        
        # Third transaction: BOO_Operations
        f.write("BEGIN\n")
        f.write("    -- Insert BOO_Operations\n")
        
        # First insert all records with NULL for next operations
        for boo in tree_boo.findall('boo'):
            boo_id = boo.get('id').strip('"')
            for operation in boo.findall('.//operation'):
                op_id = operation.get('id')
                
                f.write(f"""    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES ({op_id}, '{boo_id}', NULL, NULL);\n""")
        
        # Then update the next operations
        f.write("\n    -- Update BOO_Operations with next operations\n")
        for boo in tree_boo.findall('boo'):
            boo_id = boo.get('id').strip('"')
            for operation in boo.findall('.//operation'):
                op_id = operation.get('id')
                next_op = operation.find('next_op')
                
                if next_op is not None and next_op.text.lower() != 'none':
                    f.write(f"""    UPDATE BOO_Operation
    SET NextOPID = {next_op.text}, NextBOOID = '{boo_id}'
    WHERE OPID = {op_id} AND BOOID = '{boo_id}';\n""")
        
        f.write("    COMMIT;\nEND;\n/\n\n")
        
        # Fourth transaction: PartIN and PartOut
        f.write("BEGIN\n")
        f.write("    -- Insert PartIN and PartOut records\n")
        for boo in tree_boo.findall('boo'):
            boo_id = boo.get('id').strip('"')
            for operation in boo.findall('.//operation'):
                op_id = operation.get('id')
                
                # Insert PartIN records
                for input_part in operation.findall('.//input'):
                    part = input_part.find('part').text.strip('"')
                    quantity = input_part.find('quantity').text
                    
                    f.write(f"""    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
    VALUES ('{part}', {op_id}, '{boo_id}', {quantity});\n""")
                
                # Insert PartOut record
                output = operation.find('output')
                if output is not None:
                    part = output.find('part').text.strip('"')
                    quantity = output.find('quantity').text
                    
                    f.write(f"""    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity)
    VALUES ('{part}', {op_id}, '{boo_id}', {quantity});\n""")
        
        f.write("    COMMIT;\nEND;\n/")

if __name__ == "__main__":
    generate_operations_inserts() 