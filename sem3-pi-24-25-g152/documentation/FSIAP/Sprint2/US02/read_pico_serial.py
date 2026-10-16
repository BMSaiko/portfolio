import serial
import serial.tools.list_ports
from datetime import datetime

BAUD_RATE = 9600
OUTPUT_FILE = 'output.txt'

def find_pico_port():
    # Procura pela porta serial conectado ao Pico
    ports = serial.tools.list_ports.comports()
    for port in ports:
        # Procura por portas com VID 0x2E8A que representam Raspberry Pis
        if port.vid == 0x2E8A:
            print(f"Raspberry Pi encontrado na porta {port.device}")
            return port.device
    return None

try:
    pico_port = find_pico_port()
    if not pico_port:
        print("Não foi encontrado nenhum Raspberry Pi Pico!")
        exit(1)

    # Abre a porta serial
    with serial.Serial(pico_port, BAUD_RATE, timeout=1) as ser:
        # Abre o ficheiro de output
        with open(OUTPUT_FILE, 'a') as file:
            while True:
                # Lê uma linha do serial
                line = ser.readline().decode('utf-8').strip()

                now = datetime.now()

                current_time = now.strftime("[%d-%m-%Y %H:%M:%S] ")

                if line:
                    print(current_time + line + '\n')  
                    file.write(current_time + line + '\n')  
                    file.flush()

except KeyboardInterrupt:
    print("Operação interrompida pelo utilizador.")

except Exception as e:
    print(f"Erro: {e}")
