package eapli.aisafe.airportmanagement.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

public class RegisterAirportController {

    private final AuthService authService;
    private final AirportRepository airportRepository;
    private final AirControlAreaRepository areaRepository;

    public RegisterAirportController(final AuthService authService,
                                     final AirportRepository airportRepository,
                                     final AirControlAreaRepository areaRepository) {
        this.authService = authService;
        this.airportRepository = airportRepository;
        this.areaRepository = areaRepository;
    }

    public Airport registerAirport(final String iata,
                                   final String icao,
                                   final String name,
                                   final String town,
                                   final String country,
                                   final double latitude,
                                   final double longitude,
                                   final double altitude,
                                   final String areaCode) {

        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        final IataCode iataCode = IataCode.of(iata);
        final IcaoCode icaoCode = IcaoCode.of(icao);

        if (airportRepository.findByIataCode(iataCode).isPresent())
            throw new IllegalStateException(
                    "An airport with IATA code '" + iata + "' already exists.");

        if (airportRepository.findByIcaoCode(icaoCode).isPresent())
            throw new IllegalStateException(
                    "An airport with ICAO code '" + icao + "' already exists.");

        final AirControlArea area = areaRepository
                .findByCode(AirControlAreaCode.of(areaCode))
                .orElseThrow(() -> new IllegalStateException(
                        "Air Control Area '" + areaCode + "' does not exist."));

        if (!area.geoBoundary().contains(latitude, longitude))
            throw new IllegalStateException(
                    "Coordinates (" + latitude + ", " + longitude + ") are outside "
                            + "the boundary of Air Control Area '" + areaCode + "'.");

        final Airport airport = new Airport(
                iataCode, icaoCode, name, town, country,
                new Coordinates(latitude, longitude, altitude),
                area);

        return airportRepository.save(airport);
    }
}
