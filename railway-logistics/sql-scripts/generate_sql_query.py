import pandas as pd

def escape_quotes(val):
    if pd.isna(val):
        return "NULL"
    return "'" + str(val).replace("'", "''") + "'"

def yes_no_sql(val):
    if pd.isna(val):
        return "'No'"
    s = str(val).strip().lower()
    return "'Yes'" if s in ('yes', 'true', '1', 'y', 'sim') else "'No'"

def safe_full_time_oracle(val):
    if pd.isna(val):
        return "NULL"
    try:
        formatted_date = val.strftime('%H:%M:%S')
        return formatted_date
    except Exception:
        return "NULL"

def safe_date_only_oracle(val):
    if pd.isna(val):
        return "NULL"
    try:

        formatted_date = val.strftime('%Y/%m/%d' )
        return formatted_date
    except Exception:
        return "NULL"
    
def safe_date_oracle(val,val2):
    if pd.isna(val):
        return "NULL"
    if pd.isna(val2):
        return "NULL"
    try:
        return "TO_DATE('" + safe_date_only_oracle(val) + " " + safe_full_time_oracle(val2) + "', 'YYYY/MM/DD HH24:MI:SS')"
    except Exception:
        return "NULL"

def safe_int(val):
    if pd.isna(val):
        return "NULL"
    try:
        return str(int(val))
    except Exception:
        return "NULL"

def safe_float(val):
    if pd.isna(val):
        return "NULL"
    try:
        return str(float(val))
    except Exception:
        return "NULL"


def find_col(df, names):
    """Procura por uma coluna que combine com qualquer nome da lista (case-insensitive)."""
    for col in df.columns:
        if any(col.strip().lower() == n.lower() for n in names):
            return col
    return None

def main():
    excel_file = "C:\\Users\\joaoh\\OneDrive\\Documentos\\PythonScripBddad\\Sprint2\\dataset_s2.xlsx"
    xls = pd.ExcelFile(excel_file)
    inserts = []
    # PRIORITY 1 TABLES
    
    # === Facility === P1
    if "Facility" in xls.sheet_names:
        df = pd.read_excel(xls, "Facility")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Name'])}, {escape_quotes(r['Intermodal'])});"
            )
    
    # === Siding === P1
    if "Siding" in xls.sheet_names:
        df = pd.read_excel(xls, "Siding")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Siding (siding_id, siding_position, siding_length) VALUES ({safe_int(r['Siding ID'])}, {escape_quotes(r['Siding Position'])}, {safe_int(r['Siding Lenght'])});"
            )

    # === Owner === P1
    if "Owner" in xls.sheet_names:
        df = pd.read_excel(xls, "Owner")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO LineOwner (vat_number, owner_name, short_name) VALUES ({escape_quotes(r['VATNumber'])}, {escape_quotes(r['Name'])}, {escape_quotes(r['Short name'])});"
            )

    # === Operator === P1
    if "Operator" in xls.sheet_names:
        df = pd.read_excel(xls, "Operator")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO VehicleOperator (vat_number, operator_name, short_name) VALUES ({escape_quotes(r['VATNumber'])}, {escape_quotes(r['Name'])}, {escape_quotes(r['Short name'])});"
            )

    # === Maker === P1
    if "Maker" in xls.sheet_names:
        df = pd.read_excel(xls, "Maker")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Maker (maker_id, maker_name) VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Name'])});"
            )

    # === Infrastructure === P1
    if "Infrastructure" in xls.sheet_names:
        df = pd.read_excel(xls, "Infrastructure")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Infrastructure (infrastructure_id, infrastructure_name) VALUES ({safe_int(r['id'])}, {escape_quotes(r['name'])});"
            )

    # === Path === P1
    if "Path" in xls.sheet_names:
        df = pd.read_excel(xls, "Path")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO TrainPath (path_id) VALUES ({safe_int(r['path_id'])});"
            )

    # === Gauge === P1
    if "Gauge" in xls.sheet_names:
        df = pd.read_excel(xls, "Gauge")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Gauge (gauge_id, gauge_name, gauge_width) VALUES ({safe_int(r['gauge_id'])}, {escape_quotes(r['name '])}, {safe_int(r['width'])});"
            )

    # === EngineType === P1
    if "EngineType" in xls.sheet_names:
        df = pd.read_excel(xls, "EngineType")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO EngineType (engine_type_id, engine_type_name) VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Name'])});"
            )

    # === WagonType === P1
    if "WagonType" in xls.sheet_names:
        df = pd.read_excel(xls, "WagonType")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO WagonType (wagon_type_id, wagon_type_name) VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Name'])});"
            )

    # === Bogie Type === P1
    if "BogieType" in xls.sheet_names:
        df = pd.read_excel(xls, "BogieType")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO BogieType (bogie_type_id, bogie_type_name) VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Name'])});"
            )

    # PRIORITY 2 TABLES
    
    
    # ===Freight === P2
    if "Freights" in xls.sheet_names:
        df = pd.read_excel(xls, "Freights")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES ({safe_int(r['ID'])},TO_DATE('{safe_date_only_oracle(r['Date'])}', 'YYYY/MM/DD'), {safe_int(r['Origin'])}, {safe_int(r['Destination'])});"
            )

    # === Facility Infrastructure === P2
    if "FacilityInfrastructure" in xls.sheet_names:
        df = pd.read_excel(xls, "FacilityInfrastructure")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES ({safe_int(r['facility_id'])}, {safe_int(r['infrastructure_id'])});"
            )
    
    # === Train === P2
    if "Train" in xls.sheet_names:
        df = pd.read_excel(xls, "Train")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Train (train_id, operator_id, train_departure_date_time, path_id) "
                f"VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Operator'])}, {safe_date_oracle(r['Date'], r['Time'])}, {safe_int(r['Path'])});"
            )

    # === Path Facilities === P2
    if "PathFacilities" in xls.sheet_names:
        df = pd.read_excel(xls, "PathFacilities")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES ({safe_int(r['Path'])}, {safe_int(r['Facility'])}, {safe_int(r['Order'])});"
            )

    # === Line === P2
    if "Line" in xls.sheet_names:
        df = pd.read_excel(xls, "Line")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) "
                f"VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Owner'])}, {safe_int(r['Start ID'])}, {safe_int(r['End ID'])}, {safe_int(r['Gauge'])}, {escape_quotes(r['Name'])});"
            )

    # === Engine === P2
    if "Engine" in xls.sheet_names:
        df = pd.read_excel(xls, "Engine")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Engine (engine_id, engine_type_id) "
                f"VALUES ({safe_int(r['ID'])}, {safe_int(r['Type ID'])});"
            )

    # === WagonModel  === P2
    if "WagonModel" in xls.sheet_names:
        df = pd.read_excel(xls, "WagonModel")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) "
                f"VALUES ({safe_int(r['ID'])}, {safe_int(r['Type'])}, {safe_int(r['Maker'])}, {escape_quotes(r['Model'])}, "
                f"{safe_int(r['Number Bogies'])}, {safe_int(r['Payload (t)'])}, {safe_int(r['Max Speed'])}, "
                f"{safe_int(r['Length'])}, {safe_int(r['Width'])}, {safe_int(r['Height'])}, {safe_int(r['Weight (t)'])}, {safe_int(r['Volume (m3)'])}, {safe_int(r['Bogies'])});"
            )

    # PRIORITY 3 TABLES


    # === Wagon Model Gauge === P3
    if "WagonModelGauge" in xls.sheet_names:
        df = pd.read_excel(xls, "WagonModelGauge")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES ({safe_int(r['Model'])}, {safe_int(r['Gauge'])});"
            )

    # === Train Freight === P3
    if "TrainFreight" in xls.sheet_names:
        df = pd.read_excel(xls, "TrainFreight")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO TrainFreight (train_id, freight_id) VALUES ({safe_int(r['Train'])}, {safe_int(r['Freight'])});"
            )

    # === ElectricEngine === P3
    if "ElectricEngine" in xls.sheet_names:
        df = pd.read_excel(xls, "ElectricEngine")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO ElectricEngine (engine_id, voltage, frequency) "
                f"VALUES ({safe_int(r['ID'])}, {safe_int(r['Voltage'])}, {safe_int(r['Frequency'])});"
            )

    # === DieselEngine === P3
    if "DieselEngine" in xls.sheet_names:
        df = pd.read_excel(xls, "DieselEngine")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO DieselEngine (engine_id, fuel_capacity) "
                f"VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Capacity'])});"
            )

    # === LineSegment === P3
    if "LineSegment" in xls.sheet_names:
        df = pd.read_excel(xls, "LineSegment")
        for _, r in df.iterrows():
            is_electrified = yes_no_sql(r.get('Electrified'))
            inserts.append(
                f"INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) "
                f"VALUES ({safe_int(r['ID'])}, {safe_int(r['Line'])}, {is_electrified}, "
                f"{safe_int(r['Max Weight (kg/m)'])}, {safe_int(r['Length (m)'])}, {safe_int(r['NumberTracks'])}, {safe_int(r['Order'])}, {safe_int(r['Siding ID'])});"
            )

    # === Wagon === P3
    if "Wagon" in xls.sheet_names:
        df = pd.read_excel(xls, "Wagon")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)"
                f"VALUES ({escape_quotes(r['Number'])}, {escape_quotes(r['Operator'])}, "
                f"{safe_int(r['Model'])}, "
                f"{safe_int(r['Service Year'])});"
            )
            

    #LocomotiveModel P3
    if "LocomotiveModel" in xls.sheet_names:
        df = pd.read_excel(xls, "LocomotiveModel")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) "
                f"VALUES ({safe_int(r['ID'])}, {safe_int(r['Engine ID'])}, {safe_int(r['Bogie Type ID'])}, {safe_int(r['Maker'])}, {escape_quotes(r['Name'])}, {safe_int(r['Number Bogies'])}, "
                f"{safe_float(r['Power'])}, {safe_int(r['Length'])}, {safe_int(r['Height'])}, {safe_int(r['Width'])}, {safe_int(r['Weight (t)'])}, {safe_int(r['Max Speed'])}, {safe_int(r['Operational Speed'])}, {safe_int(r['Traction (KN)'])});"
            )


    # PRIORITY 4 TABLES

    # === Locomotive Model Gauge === P4
    if "LocomotiveModelGauge" in xls.sheet_names:
        df = pd.read_excel(xls, "LocomotiveModelGauge")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES ({safe_int(r['Model ID'])}, {safe_int(r['Gauge ID'])});"
            )

    # === Freight Wagon === P4
    if "FreightWagon" in xls.sheet_names:
        df = pd.read_excel(xls, "FreightWagon")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO FreightWagon (freight_id, wagon_id) VALUES ({safe_int(r['Freight'])}, {escape_quotes(r['Wagon'])});"
            )

    # === Locomotive === P4
    if "Locomotive" in xls.sheet_names:
        df = pd.read_excel(xls, "Locomotive")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) "
                f"VALUES ({safe_int(r['ID'])}, {escape_quotes(r['Name'])}, {safe_int(r['Year'])}, {escape_quotes(r['Operator'])}, {safe_int(r['Model ID'])});"
            )

    # PRIORITY 5 TABLES


    # === Train Locomotive === P5
    if "TrainLocomotive" in xls.sheet_names:
        df = pd.read_excel(xls, "TrainLocomotive")
        for _, r in df.iterrows():
            inserts.append(
                f"INSERT INTO TrainLocomotive (train_id, locomotive_id) VALUES ({safe_int(r['Train'])}, {escape_quotes(r['Locomotive'])});"
            )

    # Escreve os INSERTs em um arquivo SQL
    with open("Sprint2\\inserts.sql", "w", encoding="utf-8") as f:
        for stmt in inserts:
            f.write(stmt + "\n")

    print(f"✓ Gerados {len(inserts)} INSERTS em inserts.sql")
    
if __name__ == "__main__":
    main()


