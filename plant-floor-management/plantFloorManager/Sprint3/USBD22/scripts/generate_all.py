import os
import sys
import importlib.util

def import_script(script_name):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    script_path = os.path.join(script_dir, script_name)
    print(f"Importing script from: {script_path}")
    
    spec = importlib.util.spec_from_file_location(script_name, script_path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    
    return module

def main():
    print("Starting SQL generation...")
    
    # Get the absolute path to USBD22/sql directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    sql_dir = os.path.join(script_dir, '..', 'sql')
    print(f"SQL directory will be: {sql_dir}")
    
    # Create sql directory if it doesn't exist
    os.makedirs(sql_dir, exist_ok=True)
    print(f"Created/verified SQL directory at: {sql_dir}")
    
    print("\nGenerating client inserts...")
    client_script = import_script("generate_client_inserts.py")
    client_script.generate_client_inserts()
    
    print("\nGenerating basic config inserts...")
    config_script = import_script("generate_basic_config_inserts.py")
    config_script.generate_basic_config_inserts()
    
    print("\nGenerating parts inserts...")
    parts_script = import_script("generate_parts_inserts.py")
    parts_script.generate_parts_inserts()
    
    print("\nGenerating BOO inserts...")
    boo_script = import_script("generate_boo_inserts.py")
    boo_script.generate_boo_inserts()
    
    print("\nGenerating operations inserts...")
    operations_script = import_script("generate_operations_inserts.py")
    operations_script.generate_operations_inserts()
    
    print("\nGenerating procurement inserts...")
    procurement_script = import_script("generate_procurement_inserts.py")
    procurement_script.generate_procurement_inserts()
    
    print("\nGenerating orders inserts...")
    orders_script = import_script("generate_orders_inserts.py")
    orders_script.generate_orders_inserts()
    
    print(f"\nAll SQL files have been generated in: {sql_dir}")

if __name__ == "__main__":
    print("Script is running...")
    main() 