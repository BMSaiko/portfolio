import xml.etree.ElementTree as ET
import os

def generate_basic_config_inserts():
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_operations_V01.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    
    # Set output path
    output_path = os.path.join(sql_dir, '02_basic_config_inserts.sql')
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    with open(output_path, 'w') as f:
        f.write("BEGIN\n\n")
        
        # Insert ProductFamily
        f.write("    -- Insert ProductFamily\n")
        families = [
            (130, 'Family 130'),
            (125, 'Family 125'),
            (145, 'Family 145'),
            (132, 'Family 132'),
            (146, 'Family 146')
        ]
        
        for pfid, name in families:
            f.write(f"    INSERT INTO ProductFamily (PFID, Name) VALUES ({pfid}, '{name}');\n")
        
        f.write("\n    -- Insert WorkstationTypes\n")
        # Define all workstation types from the data
        ws_types = {
            'A4578', 'A4588', 'A4598',  # Press types
            'C5637',  # Trimming
            'S3271',  # Rivet
            'T3452',  # Assembly
            'K3675', 'K3676',  # Packaging
            'D9123',  # Welding
            'Q3547', 'Q5478',  # Polishing and Painting
            'Q3548', 'Q3549'   # Additional types
        }
        
        for ws_type in sorted(ws_types):
            f.write(f"""    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('{ws_type}', 'Workstation {ws_type}', 60, 10);\n""")
        
        # Insert Workstations from spreadsheet data
        f.write("\n    -- Insert Workstations\n")
        workstations = [
            (9875, 'A4578', 'Press 01', '220-630t cold forging press'),
            (9886, 'A4578', 'Press 02', '220-630t cold forging press'),
            (9847, 'A4588', 'Press 03', '220-630t precision cold forging press'),
            (9855, 'A4588', 'Press 04', '160-1000t precision cold forging press'),
            (8541, 'S3271', 'Rivet 02', 'Rivet station'),
            (8543, 'S3271', 'Rivet 03', 'Rivet station'),
            (6814, 'K3675', 'Packaging 01', 'Packaging station'),
            (6815, 'K3675', 'Packaging 02', 'Packaging station'),
            (6816, 'K3675', 'Packaging 03', 'Packaging station'),
            (6821, 'K3675', 'Packaging 04', 'Packaging station'),
            (6822, 'K3676', 'Packaging 05', 'Packaging station'),
            (8167, 'D9123', 'Welding 01', 'Spot welding staion'),
            (8170, 'D9123', 'Welding 02', 'Spot welding staion'),
            (8171, 'D9123', 'Welding 03', 'Spot welding staion'),
            (7235, 'T3452', 'Assembly 01', 'Product assembly station'),
            (7236, 'T3452', 'Assembly 02', 'Product assembly station'),
            (7238, 'T3452', 'Assembly 03', 'Product assembly station'),
            (5124, 'C5637', 'Trimming 01', 'Metal trimming station'),
            (4123, 'Q3547', 'Polishing 01', 'Metal polishing station'),
            (4124, 'Q3547', 'Polishing 02', 'Metal polishing station'),
            (4125, 'Q3547', 'Polishing 03', 'Metal polishing station')
        ]
        
        for wsid, wtid, name, description in workstations:
            f.write(f"""    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES ({wsid}, '{name}', '{description}', '{wtid}');\n""")
        
        f.write("\n    COMMIT;\nEXCEPTION\n    WHEN OTHERS THEN\n        ROLLBACK;\n        RAISE;\nEND;\n/")

if __name__ == "__main__":
    generate_basic_config_inserts()