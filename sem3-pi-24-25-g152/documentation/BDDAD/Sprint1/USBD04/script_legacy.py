import pandas as pd
import numpy as np
import datetime  # Import datetime module

# Function to generate SQL INSERT commands
def generate_insert_query(table_name, columns, values):
    columns_string = ", ".join(columns)
    value_list = []
    for col, value in zip(columns, values):
        if pd.isna(value):
            value_list.append('NULL')  # Handle missing values
        elif isinstance(value, pd.Timestamp) or isinstance(value, datetime.datetime) or isinstance(value, np.datetime64):
            # Convert date to string format and use TO_DATE
            date_str = pd.to_datetime(value).strftime('%Y-%m-%d %H:%M:%S')
            value_list.append(f"TO_DATE('{date_str}', 'YYYY-MM-DD HH24:MI:SS')")
        else:
            # Escape single quotes in strings to avoid SQL errors
            if isinstance(value, str):
                value = value.replace("'", "''")
            value_list.append(f"'{str(value)}'")
    values_string = ", ".join(value_list)
    return f"INSERT INTO {table_name} ({columns_string}) VALUES ({values_string});"

# Main function to read Excel file and generate SQL
def excel_to_sql(excel_file, output_file):
    # Open the Excel file
    excel_data = pd.ExcelFile(excel_file)

    # Correct order of tables
    ordered_tables = [
        "Country", "ClientType", "WorkstationTypes", "Operations", 
        "Parts", "Clients", "Adresses", "Orders", "ProductFamilies", "Product", "Order-Product", 
        "BOM", "BOM-Part", "Workstations", "Operation-WorkstationTypes", 
        "BOO", "B00-Operation", "ProductionOrder"
    ]

    # Mapping sheet names to SQL table names
    table_name_mapping = {
        "Country" : "Country",
        "Adresses": "Address",
        "ClientType" : "ClientType",
        "WorkstationTypes": "WorkstationTypes",
        "Operations" : "Operation",
        "Parts" : "Part",
        "Clients" : "Client",
        "Orders" : '"Order"',
        "ProductFamilies" : "ProductFamily",
        "Product" : "Product",
        "Order-Product": "OrderProduct",
        "BOM" : "BOM",
        "BOM-Part": "BOMPart",
        "Workstations" : "Workstations",
        "Operation-WorkstationTypes": "OperationWorkstationTypes",
        "BOO" : "BOO",
        "B00-Operation": "BOO_Operation",
        "ProductionOrder" : "ProductionOrder"
    }

    # Open a file to write the SQL queries
    with open(output_file, 'w', encoding='utf-8') as sql_file:
        # Loop through the tables in the correct order
        for table_name in ordered_tables:
            if table_name in excel_data.sheet_names:
                # Read each sheet, making sure dates are parsed
                df = pd.read_excel(excel_file, sheet_name=table_name, parse_dates=True)

                # Get the SQL table name, use sheet name if not mapped
                sql_table_name = table_name_mapping.get(table_name, table_name)

                # Loop through each row in the sheet
                for index, row in df.iterrows():
                    # Generate the INSERT query
                    query = generate_insert_query(sql_table_name, df.columns, row)
                    # Write the query to the file
                    sql_file.write(query + '\n')

    print(f"SQL queries successfully generated in {output_file}")

# Call the function to convert Excel to SQL
excel_file = 'dataset2V2 23_10-1923.xlsx'  # Put the correct path to your Excel file
output_file = 'output_sql_queries.sql'     # Define the output file name
excel_to_sql(excel_file, output_file)
