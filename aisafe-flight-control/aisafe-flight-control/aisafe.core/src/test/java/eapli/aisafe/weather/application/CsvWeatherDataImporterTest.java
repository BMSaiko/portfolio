package eapli.aisafe.weather.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvWeatherDataImporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldParseCompleteAisafeCsvWithHeader() throws Exception {
        Path file = tempDir.resolve("weather.csv");

        Files.writeString(file, String.join("\n",
                "AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE,PROVIDER",
                "LPPC,2026-06-22 05:00,2026-06-22 08:15,18.5,12.0,90,65,1013,AISAFE",
                "LPPO,2026-06-22 08:15,2026-06-22 12:00,22.0,8.5,180,55,1010,AISAFE"
        ));

        List<WeatherImportRecord> records = new CsvWeatherDataImporter().parse(file);

        assertEquals(2, records.size());

        WeatherImportRecord first = records.get(0);
        assertEquals("LPPC", first.areaCode);
        assertEquals(LocalDateTime.of(2026, 6, 22, 5, 0), first.start);
        assertEquals(LocalDateTime.of(2026, 6, 22, 8, 15), first.end);
        assertEquals(18.5, first.temperature);
        assertEquals(12.0, first.windSpeed);
        assertEquals(90.0, first.windDirection);
        assertEquals(65.0, first.humidity);
        assertEquals(1013.0, first.pressure);
        assertEquals("AISAFE", first.provider);
    }

    @Test
    void shouldParseCompleteAisafeCsvWithoutHeader() throws Exception {
        Path file = tempDir.resolve("weather_no_header.csv");

        Files.writeString(file,
                "LPPC,2026-06-22 05:00,2026-06-22 08:15,18.5,12.0,90,65,1013,AISAFE");

        List<WeatherImportRecord> records = new CsvWeatherDataImporter().parse(file);

        assertEquals(1, records.size());
        assertEquals("LPPC", records.get(0).areaCode);
    }

    @Test
    void shouldUseUnknownProviderWhenProviderColumnIsMissing() throws Exception {
        Path file = tempDir.resolve("weather_missing_provider.csv");

        Files.writeString(file, String.join("\n",
                "AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE",
                "LPPC,2026-06-22 05:00,2026-06-22 08:15,18.5,12.0,90,65,1013"
        ));

        List<WeatherImportRecord> records = new CsvWeatherDataImporter().parse(file);

        assertEquals(1, records.size());
        assertEquals("UNKNOWN", records.get(0).provider);
    }

    @Test
    void shouldRejectLineWithTooFewColumns() throws Exception {
        Path file = tempDir.resolve("invalid.csv");

        Files.writeString(file, String.join("\n",
                "AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE,PROVIDER",
                "LPPC,2026-06-22 05:00"
        ));

        assertThrows(IllegalArgumentException.class, () ->
                new CsvWeatherDataImporter().parse(file));
    }

    @Test
    void shouldRejectInvalidDateFormat() throws Exception {
        Path file = tempDir.resolve("invalid_date.csv");

        Files.writeString(file, String.join("\n",
                "AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE,PROVIDER",
                "LPPC,22-06-2026 05:00,2026-06-22 08:15,18.5,12.0,90,65,1013,AISAFE"
        ));

        assertThrows(Exception.class, () ->
                new CsvWeatherDataImporter().parse(file));
    }
}