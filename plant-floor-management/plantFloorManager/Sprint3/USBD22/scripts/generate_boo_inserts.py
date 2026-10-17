import xml.etree.ElementTree as ET
import os

def generate_boo_inserts():
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_boo_V02.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    
    # Set output path
    output_path = os.path.join(sql_dir, '04_boo_inserts.sql')
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    with open(output_path, 'w') as f:
        f.write("BEGIN\n\n")
        
        # Insert BOO
        f.write("    -- Insert BOO\n")
        for boo in root.findall('boo'):
            part_id = boo.get('id').strip('"')  # This will be both the PartID and BOOID
            
            f.write(f"""    INSERT INTO BOO (BOOID, PartID)
    VALUES ('{part_id}', '{part_id}');\n""")
        
        f.write("\n    COMMIT;\nEXCEPTION\n    WHEN OTHERS THEN\n        ROLLBACK;\n        RAISE;\nEND;\n/")

if __name__ == "__main__":
    generate_boo_inserts() 