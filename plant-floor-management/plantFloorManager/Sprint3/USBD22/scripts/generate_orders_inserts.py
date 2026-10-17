import os
import xml.etree.ElementTree as ET

def generate_orders_inserts():
    # Get the directory containing the script
    script_dir = os.path.dirname(os.path.abspath(__file__))
    clients_xml_path = os.path.join(script_dir, '..', 'datasets', 'Dataset_S3_clients_V01.xml')
    sql_dir = os.path.join(script_dir, '..', 'sql')
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    
    # Set output path
    output_path = os.path.join(sql_dir, '07_orders_inserts.sql')
    
    clients_tree = ET.parse(clients_xml_path)
    clients_root = clients_tree.getroot()
    
    # Build Client ID to NIF mapping from XML
    client_nifs = {}
    for client in clients_root.findall('.//client'):
        client_id = client.get('ID')
        nif = client.find('vatin').text.strip('"')
        client_nifs[client_id] = nif
    
    # Unique orders with their dates and clients
    orders = [
        {'OID': 1, 'ClientID': '785', 'DateOrder': '2024-09-15', 'DateDelivery': '2024-09-23'},
        {'OID': 2, 'ClientID': '657', 'DateOrder': '2024-09-15', 'DateDelivery': '2024-09-26'},
        {'OID': 3, 'ClientID': '348', 'DateOrder': '2024-09-15', 'DateDelivery': '2024-09-25'},
        {'OID': 4, 'ClientID': '785', 'DateOrder': '2024-09-18', 'DateDelivery': '2024-09-25'},
        {'OID': 5, 'ClientID': '657', 'DateOrder': '2024-09-18', 'DateDelivery': '2024-09-25'},
        {'OID': 6, 'ClientID': '348', 'DateOrder': '2024-09-18', 'DateDelivery': '2024-09-26'},
        {'OID': 7, 'ClientID': '456', 'DateOrder': '2024-09-21', 'DateDelivery': '2024-09-26'}
    ]
    
    # Order products with quantities
    order_products = [
        {'OID': 1, 'ProductPartID': 'AS12945S22', 'Quantity': 5},
        {'OID': 1, 'ProductPartID': 'AS12945S20', 'Quantity': 15},
        {'OID': 2, 'ProductPartID': 'AS12945S22', 'Quantity': 10},
        {'OID': 2, 'ProductPartID': 'AS12945P17', 'Quantity': 20},
        {'OID': 3, 'ProductPartID': 'AS12945S22', 'Quantity': 10},
        {'OID': 3, 'ProductPartID': 'AS12945S20', 'Quantity': 10},
        {'OID': 4, 'ProductPartID': 'AS12945S20', 'Quantity': 24},
        {'OID': 4, 'ProductPartID': 'AS12945S22', 'Quantity': 16},
        {'OID': 4, 'ProductPartID': 'AS12945S17', 'Quantity': 8},
        {'OID': 5, 'ProductPartID': 'AS12945S22', 'Quantity': 12},
        {'OID': 6, 'ProductPartID': 'AS12945S17', 'Quantity': 8},
        {'OID': 6, 'ProductPartID': 'AS12945P17', 'Quantity': 16},
        {'OID': 7, 'ProductPartID': 'AS12945S22', 'Quantity': 8}
    ]
    
    # Generate production order IDs for each order product
    production_orders = []
    pr_count = 1
    for op in order_products:
        pror_id = f'PO{str(pr_count).zfill(3)}'
        production_orders.append({
            'PrOrID': pror_id,
            'OID': op['OID'],
            'ProductPartID': op['ProductPartID']
        })
        pr_count += 1
    
    with open(output_path, 'w') as f:
        f.write("BEGIN\n\n")
        
        # Insert Orders
        f.write("    -- Insert Orders\n")
        for order in orders:
            client_nif = client_nifs[order['ClientID']]  # Get NIF from mapping
            f.write(f"""    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES ({order['OID']}, TO_DATE('{order['DateOrder']}', 'YYYY-MM-DD'), TO_DATE('{order['DateDelivery']}', 'YYYY-MM-DD'), '{client_nif}');\n""")
        
        # Insert OrderProducts
        f.write("\n    -- Insert OrderProducts\n")
        for op in order_products:
            f.write(f"""    INSERT INTO OrderProduct (OID, ProductPartID, Quantity, allMatReserved)
    VALUES ({op['OID']}, '{op['ProductPartID']}', {op['Quantity']}, 0);\n""")
        
        # Insert ProductionOrders
        f.write("\n    -- Insert ProductionOrders\n")
        for po in production_orders:
            f.write(f"""    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('{po['PrOrID']}', {po['OID']}, '{po['ProductPartID']}');\n""")
        
        f.write("\n    COMMIT;\nEXCEPTION\n    WHEN OTHERS THEN\n        ROLLBACK;\n        RAISE;\nEND;\n/")

if __name__ == "__main__":
    generate_orders_inserts() 