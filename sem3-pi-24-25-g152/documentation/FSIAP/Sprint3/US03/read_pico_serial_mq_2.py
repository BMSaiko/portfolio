import serial
import serial.tools.list_ports
from datetime import datetime
import re

BAUD_RATE = 9600
OUTPUT_FILE = 'output.txt'

def find_pico_port():
    # Procura pela porta serial conectado ao Pico
    ports = serial.tools.list_ports.comports()
    for port in ports:
        # Procura por portas com VID 0x2E8A (Pico)
        if port.vid == 0x2E8A:
            print(f"Raspberry Pi encontrado na porta {port.device}")
            return port.device
    return None

# Variáveis globais para armazenar leituras iniciais
initial_temp = None
initial_hum = None
initial_gas = None
initialized = False

# Regex para extrair dados do formato:
# "Temp: X °C | LED_TEMP: ON/OFF | Hum: Y % | LED_HUM: ON/OFF | Gas: Z"
pattern = re.compile(r'Temp:\s+([0-9.]+)\s+°C\s+\|\s+LED_TEMP:\s+(ON|OFF)\s+\|\s+Hum:\s+([0-9.]+)\s+%\s+\|\s+LED_HUM:\s+(ON|OFF)\s+\|\s+Gas:\s+([0-9.]+)')

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
                line = ser.readline().decode('utf-8').strip()
                now = datetime.now()
                current_time = now.strftime("[%d-%m-%Y %H:%M:%S] ")

                if line:
                    # Escreve linha bruta
                    print(current_time + line)
                    file.write(current_time + line + '\n')
                    file.flush()

                    # Detetar linha inicial
                    # Exemplo da linha inicial no Arduino: 
                    # "Leitura Inicial - Temp: X °C | Hum: Y % | Gas: Z"
                    if "Leitura Inicial" in line:
                        # Extrair valores iniciais
                        # Supondo que a linha é algo como:
                        # "Leitura Inicial - Temp: 24.0 °C | Humidade: 50.0 % | Gas: 300"
                        init_pattern = re.compile(r'Temp:\s+([0-9.]+)\s+°C\s+\|\s+Humidade:\s+([0-9.]+)\s+%\s+\|\s+Gas:\s+([0-9.]+)')
                        match_init = init_pattern.search(line)
                        if match_init:
                            initial_temp = float(match_init.group(1))
                            initial_hum = float(match_init.group(2))
                            initial_gas = float(match_init.group(3))
                            initialized = True
                            print(current_time + f"Valores Iniciais guardados: Temp={initial_temp}°C, Hum={initial_hum}%, Gas={initial_gas}\n")
                            file.write(current_time + f"Valores Iniciais: Temp={initial_temp}°C, Hum={initial_hum}%, Gas={initial_gas}\n")

                    # Detetar dados de sensor
                    match = pattern.search(line)
                    if match and initialized:
                        temp_val = float(match.group(1))
                        led_temp_state = match.group(2)
                        hum_val = float(match.group(3))
                        led_hum_state = match.group(4)
                        gas_val = float(match.group(5))

                        # Verificação das condições
                        # Temperatura > 5°C
                        if temp_val >= initial_temp + 5:
                            print(current_time + "Evento: Temperatura excedeu o limite. Sequência de exaustão->ventilação deve ter sido ativada.\n")
                            file.write(current_time + "Evento: Temperatura >5°C. Ventoinhas acionadas.\n")

                        # Humidade > 5%
                        if hum_val >= initial_hum + 5:
                            print(current_time + "Evento: Humidade excedeu o limite. Sequência de ventilação->exaustão deve ter sido ativada.\n")
                            file.write(current_time + "Evento: Humidade >5%. Ventoinhas acionadas.\n")

                        # Gás > 2%
                        if gas_val >= initial_gas * 1.02:
                            print(current_time + "Evento: Gás excedeu o limite. Ambas as ventoinhas devem ter sido ativadas.\n")
                            file.write(current_time + "Evento: Gás >2%. Ambas ventoinhas acionadas.\n")

                    # Caso o Arduino imprima eventos específicos
                    if "EVENTO:" in line:
                        print(current_time + f"Notificação de Evento detectada do Arduino: {line}\n")
                        file.write(current_time + f"Evento Arduino: {line}\n")

except KeyboardInterrupt:
    print("Operação interrompida pelo utilizador.")

except Exception as e:
    print(f"Erro: {e}")
