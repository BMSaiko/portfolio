import xml.etree.ElementTree as ET
import os

def generate_procurement_inserts():
    print("Starting procurement inserts generation...")
    
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_procurement_V02.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    print(f"XML path: {xml_path}")
    print(f"SQL directory: {sql_dir}")
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    print(f"Created/verified SQL directory")
    
    # Set output path
    output_path = os.path.join(sql_dir, '06_procurement_inserts.sql')
    print(f"Will write to: {output_path}")
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    print("Writing SQL file...")
    with open(output_path, 'w') as f:
        f.write("BEGIN\n\n")
        
        # Insert Suppliers
        f.write("    -- Insert Suppliers\n")
        suppliers_added = set()
        for supplier in root.findall('supplier'):
            supplier_id = supplier.get('id')
            if supplier_id not in suppliers_added:
                f.write(f"""    INSERT INTO supplier (SupplierID, Name)
    VALUES ({supplier_id}, 'Supplier {supplier_id}');\n""")
                suppliers_added.add(supplier_id)
        
        # Insert Offers
        f.write("\n    -- Insert Offers\n")
        offers = set()  # Track unique combinations of PartID, SupplierID, DateStart
        for supplier in root.findall('supplier'):
            supplier_id = supplier.get('id')
            for part in supplier.findall('.//part'):
                part_id = part.get('id').strip('"')
                for offer in part.findall('.//offer'):
                    start_date = offer.find('start_date').text
                    end_date = offer.find('end_date').text
                    min_qty = offer.find('min_quantity').text
                    price = offer.find('price').text
                    
                    # Create a unique key for this offer
                    offer_key = (part_id, supplier_id, start_date)
                    
                    if offer_key not in offers:
                        if end_date == 'NULL':
                            end_date_sql = 'NULL'
                        else:
                            end_date_sql = f"TO_DATE('{end_date}', 'YYYY/MM/DD')"
                        
                        f.write(f"""    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('{part_id}', {supplier_id}, {price}, TO_DATE('{start_date}', 'YYYY/MM/DD'), {end_date_sql}, {min_qty});\n""")
                        offers.add(offer_key)
        
        f.write("\n    COMMIT;\nEXCEPTION\n    WHEN OTHERS THEN\n        ROLLBACK;\n        RAISE;\nEND;\n/")
        print("Finished writing SQL file")

if __name__ == "__main__":
    generate_procurement_inserts() 