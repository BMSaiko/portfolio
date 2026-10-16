package eapli.aisafe.weather.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CwWindWeatherDataImporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldParseCwWindProviderCsv() throws Exception {
        Path file = tempDir.resolve("cw_wind.csv");

        Files.writeString(file, String.join("\n",
                "ACA,Lat.,Long.,Lat.,Long.,Alt_inf (ft),Alt_Sup (ft),Direction (degree),Value (knot),Day,Star,End",
                "121,43.840454,-9.795711,40.225,-7.9501,0,1000,90,28.75,6/22/2026,5:00,8:15"
        ));

        List<WeatherImportRecord> records = new CwWindWeatherDataImporter().parse(file);

        assertEquals(1, records.size());

        WeatherImportRecord record = records.get(0);
        assertEquals("LPPC", record.areaCode);
        assertEquals(LocalDateTime.of(2026, 6, 22, 5, 0), record.start);
        assertEquals(LocalDateTime.of(2026, 6, 22, 8, 15), record.end);
        assertEquals(15.0, record.temperature);
        assertEquals(28.75 * 0.514444, record.windSpeed, 0.001);
        assertEquals(90.0, record.windDirection);
        assertEquals(50.0, record.humidity);
        assertEquals(1013.0, record.pressure);
        assertEquals("CW-WIND", record.provider);
    }

    @Test
    void shouldMapUnknownAcaIdToGeneratedAreaCode() throws Exception {
        Path file = tempDir.resolve("cw_unknown_aca.csv");

        Files.writeString(file, String.join("\n",
                "ACA,Lat.,Long.,Lat.,Long.,Alt_inf (ft),Alt_Sup (ft),Direction (degree),Value (knot),Day,Star,End",
                "999,43.840454,-9.795711,40.225,-7.9501,0,1000,90,10,6/22/2026,5:00,8:15"
        ));

        List<WeatherImportRecord> records = new CwWindWeatherDataImporter().parse(file);

        assertEquals("ACA_999", records.get(0).areaCode);
    }

    @Test
    void shouldMoveEndToNextDayWhenEndTimeIsBeforeStartTime() throws Exception {
        Path file = tempDir.resolve("cw_overnight.csv");

        Files.writeString(file, String.join("\n",
                "ACA,Lat.,Long.,Lat.,Long.,Alt_inf (ft),Alt_Sup (ft),Direction (degree),Value (knot),Day,Star,End",
                "121,43.840454,-9.795711,40.225,-7.9501,0,1000,90,10,6/22/2026,23:00,1:00"
        ));

        List<WeatherImportRecord> records = new CwWindWeatherDataImporter().parse(file);

        assertEquals(LocalDateTime.of(2026, 6, 22, 23, 0), records.get(0).start);
        assertEquals(LocalDateTime.of(2026, 6, 23, 1, 0), records.get(0).end);
    }

    @Test
    void shouldRejectLineWithTooFewColumns() throws Exception {
        Path file = tempDir.resolve("cw_invalid.csv");

        Files.writeString(file, String.join("\n",
                "ACA,Lat.,Long.,Lat.,Long.,Alt_inf (ft),Alt_Sup (ft),Direction (degree),Value (knot),Day,Star,End",
                "121,43.840454"
        ));

        assertThrows(IllegalArgumentException.class, () ->
                new CwWindWeatherDataImporter().parse(file));
    }
}