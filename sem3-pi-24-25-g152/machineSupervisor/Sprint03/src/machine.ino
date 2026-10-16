#include <DHT.h>

#define DHTPIN 0
#define DHTTYPE DHT11
#define N_PINS 5
#define CMD_SIZE 50

int LED_PIN[N_PINS] = {17, 18, 19, 20, 21};

DHT dht(DHTPIN, DHTTYPE);

float hum = 0.0;
float temp = 0.0;

void setup()
{
    Serial.begin(9600);

    while (!Serial)
        ;

    while (Serial.available() > 0)
    {
        Serial.read();
    }

    dht.begin();

    for (int i = 0; i < N_PINS; i++)
    {
        pinMode(LED_PIN[i], OUTPUT);
        digitalWrite(LED_PIN[i], LOW);
    }

    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, LOW);
}

void loop()
{

    char *cmd = read_from_serial();

    read_temp_and_hum(cmd);

    turn_on_leds(cmd);
}

void turn_on_leds(char *cmd)
{
    char cmd_copy[50];
    strcpy(cmd_copy, cmd);

    char *token = strtok(cmd_copy + 3, ",");
    int i = 0;

    while (token != NULL && i < N_PINS)
    {
        int state = atoi(token);
        digitalWrite(LED_PIN[i], state ? HIGH : LOW);
        token = strtok(NULL, ",");
        i++;
    }

    if (strncmp(cmd, "OP", 2) == 0)
    {
        for (i = 0; i < 4; i++)
        {
            digitalWrite(LED_BUILTIN, HIGH);
            delay(500);
            digitalWrite(LED_BUILTIN, LOW);
            delay(500);
        }
    }
    else if (strncmp(cmd, "ON", 2) == 0)
    {
        digitalWrite(LED_BUILTIN, HIGH);
        delay(2000);
    }
    else if (strncmp(cmd, "OFF", 3) == 0)
    {
        digitalWrite(LED_BUILTIN, LOW);
        delay(2000);
    }
}

void read_temp_and_hum(char *cmd)
{
    char str[100];

    if (strncmp(cmd, "OFF", 3) == 0)
    {
        temp = -1;
        hum = -1;
        sprintf(str, "Machine is OFF");
        Serial.println(str);
    }
    else if (strncmp(cmd, "ON", 2) == 0 || strncmp(cmd, "OP", 2) == 0)
    {
        temp = read_temp_from_sensor();
        hum = read_hum_from_sensor();
        sprintf(str, "TEMP&unit:celsius&value:%d#HUM&unit:percentage&value:%d", (int)temp, (int)hum);
        Serial.println(str);
    }
}

int read_temp_from_sensor()
{
    float t = dht.readTemperature();
    if (isnan(t))
    {
        Serial.println("Failed to read temperature from DHT sensor!");
        return -1;
    }
    return (int)t;
}

int read_hum_from_sensor()
{
    float h = dht.readHumidity();
    if (isnan(h))
    {
        Serial.println("Failed to read humidity from DHT sensor!");
        return -1;
    }
    return (int)h;
}

char *read_from_serial()
{
    static char command[CMD_SIZE];

    while (Serial.available() != 0)
    {
        String input = Serial.readStringUntil('\n');
        input.toCharArray(command, sizeof(command));

        Serial.println(command);
        delay(500);
    }

    return command;
}
