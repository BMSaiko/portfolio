package eapli.aisafe.aircontrolmanagement.application;

import eapli.aisafe.aircontrolmanagement.domain.*;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

public class RegisterAirControlAreaController {

    private final AuthService authService;
    private final AirControlAreaRepository repository;

    public RegisterAirControlAreaController(AuthService authService,
                                            AirControlAreaRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    public AirControlArea registerAirControlArea(
            final String code,
            final String name,
            final double minimumFuel,
            final double northLat,
            final double southLat,
            final double eastLon,
            final double westLon) {

        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        final AirControlAreaCode areaCode = AirControlAreaCode.of(code);

        if (repository.findByCode(areaCode).isPresent())
            throw new IllegalStateException(
                    "An Air Control Area with code '" + code + "' already exists.");

        final GeoBoundary newBoundary =
                new GeoBoundary(northLat, southLat, eastLon, westLon);

        for (AirControlArea existing : repository.findAll()) {
            if (existing.geoBoundary().overlapsWith(newBoundary))
                throw new IllegalStateException(
                        "The boundary overlaps with existing area: "
                                + existing.identity());
        }

        final AirControlArea area = new AirControlArea(
                areaCode, name, minimumFuel, newBoundary);

        repository.save(area);
        return area;
    }
}