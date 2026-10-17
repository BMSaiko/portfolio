# Understanding the Temperature and Humidity Monitoring Code

This document provides an in-depth explanation of the provided code snippets, which monitor temperature and humidity using a DHT11 sensor and control LEDs based on specific thresholds. The code is available Arduino IDE formats.

## Arduino IDE Implementation

The Arduino sketch performs similar functionality using the DHT library to interface with the DHT11 sensor and control LEDs based on temperature and humidity thresholds.

### Code Breakdown

```python
#include <DHT.h>

// Pin definitions
#define DHTPIN 0        // Pin where the DHT11 is connected
#define DHTTYPE DHT11   // DHT sensor type

#define LED_TEMP_PIN 9   // Temperature LED on digital pin 9
#define LED_HUM_PIN 13   // Humidity LED on digital pin 13

DHT dht(DHTPIN, DHTTYPE);

// Variables to store initial readings
float temp_initial = 0.0;
float hum_initial = 0.0;

void setup() {
  // Initialize serial communication
  Serial.begin(9600);
  Serial.println("Initializing DHT11 sensor and LEDs...");

  // Initialize the DHT11 sensor
  dht.begin();

  // Set LED pins as outputs
  pinMode(LED_TEMP_PIN, OUTPUT);
  pinMode(LED_HUM_PIN, OUTPUT);

  // Ensure LEDs are off at the start
  digitalWrite(LED_TEMP_PIN, LOW);
  digitalWrite(LED_HUM_PIN, LOW);

  // Capture initial readings
  delay(2000); // Wait 2 seconds for sensor stabilization
  temp_initial = dht.readTemperature();
  hum_initial = dht.readHumidity();

  if (isnan(temp_initial) || isnan(hum_initial)) {
    Serial.println("Failed to read from DHT11 sensor.");
  } else {
    Serial.print("Initial Reading - Temperature: ");
    Serial.print(temp_initial);
    Serial.print(" °C | Humidity: ");
    Serial.print(hum_initial);
    Serial.println(" %");
  }
}

void loop() {
  // Attempt to read from DHT11 sensor
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();

  // Check if reading failed
  if (isnan(temp) || isnan(hum)) {
    Serial.println("Failed to read from DHT11 sensor.");
  } else {
    // Check if temperature exceeds initial by 5°C
    if (temp >= (temp_initial + 5.0)) {
      digitalWrite(LED_TEMP_PIN, HIGH); // Turn on temperature LED
    } else {
      digitalWrite(LED_TEMP_PIN, LOW);  // Turn off temperature LED
    }

    // Check if humidity exceeds initial by 5%
    if (hum >= (hum_initial + 5.0)) {
      digitalWrite(LED_HUM_PIN, HIGH);  // Turn on humidity LED
    } else {
      digitalWrite(LED_HUM_PIN, LOW);   // Turn off humidity LED
    }

    // Display readings on Serial Monitor
    Serial.print("Temperature: ");
    Serial.print(temp);
    Serial.print(" °C | Humidity: ");
    Serial.print(hum);
    Serial.println(" %");
  }

  delay(1000); // Wait 1 second before next reading
}
```

## Explanation

1. Library Inclusion:

- The `DHT.h` library is included to facilitate communication with the DHT11 sensor.

2. Pin Definitions:

- Defines the pins for the DHT11 sensor and the LEDs.

3. Sensor and LED Initialization:

- Initializes the DHT11 sensor and sets the LED pins as outputs.
- Ensures LEDs are off at the start.

4. Initial Readings:

- Captures initial temperature and humidity readings after a 2-second delay to allow sensor stabilization

5. Loop:

- Continuously reads temperature and humidity data from the sensor.
- Turns on the temperature LED if the temperature is 5°C higher than the initial reading.
- Turns on the humidity LED if the humidity is 5% higher than the initial reading.
- Outputs the readings to the Serial Monitor.
