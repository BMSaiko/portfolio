package eapli.aisafe.weather.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.generic.domain.TimePeriod;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.domain.Temperature;
import eapli.aisafe.weather.domain.WindSpeed;
import eapli.aisafe.weather.domain.WindDirection;
import eapli.aisafe.weather.domain.Humidity;
import eapli.aisafe.weather.domain.Pressure;
import eapli.aisafe.weather.repositories.WeatherDataRepository;

import java.time.LocalDateTime;

public class RegisterWeatherDataController {

    private final AuthService auth;
    private final AirControlAreaRepository areaRepo;
    private final WeatherDataRepository weatherRepo;

    public RegisterWeatherDataController(AuthService auth,
                                         AirControlAreaRepository areaRepo,
                                         WeatherDataRepository weatherRepo) {
        this.auth = auth;
        this.areaRepo = areaRepo;
        this.weatherRepo = weatherRepo;
    }

    public void register(String areaCode, LocalDateTime start, LocalDateTime end,
                         double temperature, double windSpeed, double windDirection,
                         double humidity, double pressure) {
        auth.ensureHasAnyRole(Role.WEATHER_PERSON, Role.ADMIN);

        AirControlArea area = areaRepo.findByCode(AirControlAreaCode.of(areaCode))
                .orElseThrow(() -> new IllegalArgumentException("Air control area not found"));

        WeatherData data = new WeatherData(area, TimePeriod.of(start, end),
                Temperature.of(temperature), WindSpeed.of(windSpeed),
                WindDirection.of(windDirection), Humidity.of(humidity),
                Pressure.of(pressure));
        weatherRepo.save(data);
    }
}
