package eapli.aisafe.weather.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AutoDetectWeatherDataImporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldUseSimpleCsvImporterForAisafeHeader() throws Exception {
        Path file = tempDir.resolve("aisafe.csv");

        Files.writeString(file, String.join("\n",
                "AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE,PROVIDER",
                "LPPC,2026-06-22 05:00,2026-06-22 08:15,18.5,12.0,90,65,1013,AISAFE"
        ));

        AutoDetectWeatherDataImporter importer = new AutoDetectWeatherDataImporter(
                new CsvWeatherDataImporter(),
                new CwWindWeatherDataImporter()
        );

        List<WeatherImportRecord> records = importer.parse(file);

        assertEquals(1, records.size());
        assertEquals("AISAFE", records.get(0).provider);
    }

    @Test
    void shouldUseCwWindImporterForCwHeader() throws Exception {
        Path file = tempDir.resolve("cw.csv");

        Files.writeString(file, String.join("\n",
                "ACA,Lat.,Long.,Lat.,Long.,Alt_inf (ft),Alt_Sup (ft),Direction (degree),Value (knot),Day,Star,End",
                "121,43.840454,-9.795711,40.225,-7.9501,0,1000,90,28.75,6/22/2026,5:00,8:15"
        ));

        AutoDetectWeatherDataImporter importer = new AutoDetectWeatherDataImporter(
                new CsvWeatherDataImporter(),
                new CwWindWeatherDataImporter()
        );

        List<WeatherImportRecord> records = importer.parse(file);

        assertEquals(1, records.size());
        assertEquals("CW-WIND", records.get(0).provider);
        assertEquals("LPPC", records.get(0).areaCode);
    }

    @Test
    void shouldRejectUnsupportedHeader() throws Exception {
        Path file = tempDir.resolve("unsupported.csv");

        Files.writeString(file, String.join("\n",
                "X,Y,Z",
                "1,2,3"
        ));

        AutoDetectWeatherDataImporter importer = new AutoDetectWeatherDataImporter(
                new CsvWeatherDataImporter(),
                new CwWindWeatherDataImporter()
        );

        assertThrows(IllegalArgumentException.class, () -> importer.parse(file));
    }

    @Test
    void shouldRejectEmptyFile() throws Exception {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "");

        AutoDetectWeatherDataImporter importer = new AutoDetectWeatherDataImporter(
                new CsvWeatherDataImporter(),
                new CwWindWeatherDataImporter()
        );

        assertThrows(IllegalArgumentException.class, () -> importer.parse(file));
    }
}