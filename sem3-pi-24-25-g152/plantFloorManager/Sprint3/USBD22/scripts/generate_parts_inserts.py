import xml.etree.ElementTree as ET
import os

def generate_parts_inserts():
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_parts_V01.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    
    # Set output path
    output_path = os.path.join(sql_dir, '03_parts_inserts.sql')
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    with open(output_path, 'w') as f:
        f.write("BEGIN\n\n")
        
        # Insert Units first
        f.write("    -- Insert Units\n")
        units = [
            (1, 'kg', 'Kilogram'),
            (2, 'l', 'Liter'),
            (3, 'g', 'Gram'),
            (4, 'unit', 'Unit')
        ]
        
        for unit_id, name, description in units:
            f.write(f"""    INSERT INTO Unit (UnitID, Name)
    VALUES ({unit_id}, '{name}');\n""")
        
        f.write("\n")
        
        # Insert Parts
        f.write("    -- Insert Parts\n")
        for part in root.findall('part'):
            part_number = part.find('part_number').text.strip('"')
            description = part.find('description').text.strip('"')
            part_type = part.find('part_type').text.strip('"')
            
            f.write(f"""    INSERT INTO Part (PartID, Description)
    VALUES ('{part_number}', '{description}');\n""")
            
            # Insert into specific part type tables
            if part_type == 'Component':
                f.write(f"""    INSERT INTO ExternalPart (PartID, Stock, MinimumStock)
    VALUES ('{part_number}', 10, 3);\n""")
                f.write(f"    INSERT INTO Component (PartID) VALUES ('{part_number}');\n")
            elif part_type == 'Raw Material':
                f.write(f"""    INSERT INTO ExternalPart (PartID, Stock, MinimumStock)
    VALUES ('{part_number}', 10, 3);\n""")
                # For raw materials, assign a default unit (1 for kg)
                f.write(f"""    INSERT INTO RawMaterial (PartID, UnitID)
    VALUES ('{part_number}', 1);\n""")
            elif part_type == 'Product':
                name = part.find('name').text.strip('"')
                family = part.find('family').text.strip('"')
                f.write(f"    INSERT INTO InternalPart (PartID) VALUES ('{part_number}');\n")
                f.write(f"""    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('{part_number}', '{name}', {family});\n""")
            elif part_type == 'Intermediate Product':
                f.write(f"    INSERT INTO InternalPart (PartID) VALUES ('{part_number}');\n")
                f.write(f"    INSERT INTO IntermediateProduct (PartID) VALUES ('{part_number}');\n")
            
            f.write("\n")
        
        f.write("\n    COMMIT;\nEXCEPTION\n    WHEN OTHERS THEN\n        ROLLBACK;\n        RAISE;\nEND;\n/")

if __name__ == "__main__":
    generate_parts_inserts() 