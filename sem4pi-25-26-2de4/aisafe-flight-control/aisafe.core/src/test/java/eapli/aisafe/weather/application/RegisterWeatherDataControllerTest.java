package eapli.aisafe.weather.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterWeatherDataControllerTest {

    private AuthService authService;
    private AirControlAreaRepository areaRepo;
    private WeatherDataRepository weatherRepo;
    private RegisterWeatherDataController controller;
    private AirControlArea mockArea;
    private final String areaCode = "LIS_AREA";
    private final LocalDateTime start = LocalDateTime.now().minusHours(2);
    private final LocalDateTime end = LocalDateTime.now().minusHours(1);
    private final double temp = 20.0;
    private final double windSpeed = 10.0;
    private final double windDir = 180.0;
    private final double humidity = 65.0;
    private final double pressure = 1013.0;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        areaRepo = mock(AirControlAreaRepository.class);
        weatherRepo = mock(WeatherDataRepository.class);
        controller = new RegisterWeatherDataController(authService, areaRepo, weatherRepo);
        mockArea = new AirControlArea(AirControlAreaCode.of(areaCode), "Lisbon Area", 5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0));
    }

    @Test
    void registerShouldSucceedWhenUserHasRequiredRoleAndAreaExists() {
        when(areaRepo.findByCode(AirControlAreaCode.of(areaCode))).thenReturn(Optional.of(mockArea));

        controller.register(areaCode, start, end, temp, windSpeed, windDir, humidity, pressure);

        verify(authService).ensureHasAnyRole(Role.WEATHER_PERSON, Role.ADMIN);
        verify(areaRepo).findByCode(AirControlAreaCode.of(areaCode));
        verify(weatherRepo).save(any(WeatherData.class));
    }

    @Test
    void registerShouldThrowWhenAreaNotFound() {
        when(areaRepo.findByCode(AirControlAreaCode.of(areaCode))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                controller.register(areaCode, start, end, temp, windSpeed, windDir, humidity, pressure));

        verify(weatherRepo, never()).save(any());
    }

    @Test
    void registerShouldThrowWhenAuthFails() {
        doThrow(new SecurityException("Unauthorized")).when(authService)
                .ensureHasAnyRole(Role.WEATHER_PERSON, Role.ADMIN);

        assertThrows(SecurityException.class, () ->
                controller.register(areaCode, start, end, temp, windSpeed, windDir, humidity, pressure));

        verify(areaRepo, never()).findByCode(any());
        verify(weatherRepo, never()).save(any());
    }

    @Test
    void registerShouldPropagateValidationExceptionFromVOs() {
        when(areaRepo.findByCode(AirControlAreaCode.of(areaCode))).thenReturn(Optional.of(mockArea));

        assertThrows(IllegalArgumentException.class, () ->
                controller.register(areaCode, start, end, -51.0, windSpeed, windDir, humidity, pressure));

        verify(weatherRepo, never()).save(any());
    }

    @Test
    void registerShouldThrowWhenEndBeforeStart() {
        when(areaRepo.findByCode(AirControlAreaCode.of(areaCode))).thenReturn(Optional.of(mockArea));

        assertThrows(IllegalArgumentException.class, () ->
                controller.register(areaCode, end, start, temp, windSpeed, windDir, humidity, pressure));

        verify(weatherRepo, never()).save(any());
    }

    @Test
    void registerShouldAllowFutureWeatherForecastData() {
        when(areaRepo.findByCode(AirControlAreaCode.of(areaCode))).thenReturn(Optional.of(mockArea));

        LocalDateTime future = LocalDateTime.now().plusDays(1);

        assertDoesNotThrow(() ->
                controller.register(areaCode, future, future.plusHours(1), temp, windSpeed, windDir, humidity, pressure));

        verify(weatherRepo).save(any(WeatherData.class));
    }

    @Test
    void registerShouldPassCorrectParametersToWeatherData() {
        when(areaRepo.findByCode(AirControlAreaCode.of(areaCode))).thenReturn(Optional.of(mockArea));

        controller.register(areaCode, start, end, temp, windSpeed, windDir, humidity, pressure);

        verify(weatherRepo).save(argThat(data ->
                data.area().equals(mockArea) &&
                        data.period().start().equals(start) &&
                        data.period().end().equals(end) &&
                        data.temperature().value() == temp &&
                        data.windSpeed().value() == windSpeed &&
                        data.windDirection().value() == windDir &&
                        data.humidity().value() == humidity &&
                        data.pressure().value() == pressure
        ));
    }
}