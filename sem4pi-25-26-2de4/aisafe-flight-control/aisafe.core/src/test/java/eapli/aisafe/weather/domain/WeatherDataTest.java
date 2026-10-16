package eapli.aisafe.weather.domain;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.generic.domain.TimePeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDataTest {

    private AirControlArea area;
    private LocalDateTime validStart;
    private LocalDateTime validEnd;
    private final double validTemp = 20.0;
    private final double validWindSpeed = 10.0;
    private final double validWindDir = 180.0;
    private final double validHumidity = 65.0;
    private final double validPressure = 1013.0;

    @BeforeEach
    void setUp() {
        area = new AirControlArea(AirControlAreaCode.of("TEST_AREA"), "Test Area", 5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0));
        validStart = LocalDateTime.now().minusHours(2);
        validEnd = LocalDateTime.now().minusHours(1);
    }

    private WeatherData build() {
        return new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure));
    }

    @Test
    void constructorShouldCreateValidWeatherData() {
        WeatherData data = build();
        assertNotNull(data);
        assertEquals(area, data.area());
        assertEquals(validStart, data.period().start());
        assertEquals(validEnd, data.period().end());
        assertEquals(validTemp, data.temperature().value());
        assertEquals(validWindSpeed, data.windSpeed().value());
        assertEquals(validWindDir, data.windDirection().value());
        assertEquals(validHumidity, data.humidity().value());
        assertEquals(validPressure, data.pressure().value());
    }

    @Test
    void constructorShouldThrowWhenAreaIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(null, TimePeriod.of(validStart, validEnd),
                        Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                        WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldThrowWhenPeriodIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, null,
                        Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                        WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldThrowWhenPeriodEndIsBeforeStart() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, TimePeriod.of(validEnd, validStart),
                        Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                        WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldAllowFuturePeriodStartForForecastData() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);

        WeatherData data = new WeatherData(area, TimePeriod.of(future, future.plusHours(1)),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure));

        assertEquals(future, data.period().start());
        assertEquals(future.plusHours(1), data.period().end());
    }

    @Test
    void constructorShouldThrowWhenTemperatureIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, TimePeriod.of(validStart, validEnd),
                        null, WindSpeed.of(validWindSpeed),
                        WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldThrowWhenWindSpeedIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, TimePeriod.of(validStart, validEnd),
                        Temperature.of(validTemp), null,
                        WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldThrowWhenWindDirectionIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, TimePeriod.of(validStart, validEnd),
                        Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                        null, Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldThrowWhenHumidityIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, TimePeriod.of(validStart, validEnd),
                        Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                        WindDirection.of(validWindDir), null, Pressure.of(validPressure)));
    }

    @Test
    void constructorShouldThrowWhenPressureIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(area, TimePeriod.of(validStart, validEnd),
                        Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                        WindDirection.of(validWindDir), Humidity.of(validHumidity), null));
    }

    @Test
    void idShouldReturnNullBeforePersistence() {
        WeatherData data = build();
        assertNull(data.id());
    }

    @Test void constructorShouldAcceptMinTemp() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(-50.0), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAcceptMaxTemp() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(60.0), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAcceptZeroWindSpeed() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(0.0),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAcceptZeroWindDir() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(0.0), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAccept360WindDir() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(360.0), Humidity.of(validHumidity), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAcceptZeroHumidity() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(0.0), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAccept100Humidity() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(100.0), Pressure.of(validPressure)));
    }

    @Test void constructorShouldAcceptMinPressure() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(800.0)));
    }

    @Test void constructorShouldAcceptMaxPressure() {
        assertDoesNotThrow(() -> new WeatherData(area, TimePeriod.of(validStart, validEnd),
                Temperature.of(validTemp), WindSpeed.of(validWindSpeed),
                WindDirection.of(validWindDir), Humidity.of(validHumidity), Pressure.of(1100.0)));
    }
}