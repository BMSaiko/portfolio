import xml.etree.ElementTree as ET
import os

def generate_client_inserts():
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_clients_V01.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    
    # Set output path
    output_path = os.path.join(sql_dir, '01_client_inserts.sql')
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    with open(output_path, 'w') as f:
        f.write("BEGIN\n\n")
        
        # Insert ClientType
        f.write("    -- Insert ClientType\n")
        f.write("    INSERT INTO ClientType (CID, Type) VALUES ('C1', 'Individual');\n")
        f.write("    INSERT INTO ClientType (CID, Type) VALUES ('C2', 'Company');\n\n")
        
        # Insert Country
        f.write("    -- Insert Country\n")
        # Get unique countries from the XML
        countries = set()
        for client in root.findall('client'):
            country = client.find('country').text.strip('"')
            countries.add(country)
            
        # Create country code mapping
        country_codes = {
            'Portugal': 'PT',
            'Czechia': 'CZ'
        }
        
        for country in countries:
            code = country_codes[country]
            f.write(f"    INSERT INTO Country (CountryCode, Name) VALUES ('{code}', '{country}');\n")
        
        # Insert Clients
        f.write("\n    -- Insert Clients\n")
        for client in root.findall('client'):
            client_id = client.get('ID')
            name = client.find('name').text.strip('"')
            vatin = client.find('vatin').text.strip('"')
            email = client.find('email').text.strip('"')
            phone = client.find('phone').text
            
            f.write(f"""    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES ({client_id}, '{name}', '{vatin}', 'C2', '{email}', {phone}, 1);\n""")
        
        f.write("\n    -- Insert Addresses\n")
        for client in root.findall('client'):
            vatin = client.find('vatin').text.strip('"')
            address = client.find('adress').text.strip('"')
            zip_code = client.find('zip').text.strip('"')
            town = client.find('town').text.strip('"')
            country = client.find('country').text.strip('"')
            country_code = country_codes[country]  # Use the mapping instead of slicing
            
            f.write(f"""    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('{address}', '{zip_code}', '{town}', '{country_code}', '{vatin}');\n""")
        
        f.write("\n    COMMIT;\nEXCEPTION\n    WHEN OTHERS THEN\n        ROLLBACK;\n        RAISE;\nEND;\n/")

if __name__ == "__main__":
    generate_client_inserts() 