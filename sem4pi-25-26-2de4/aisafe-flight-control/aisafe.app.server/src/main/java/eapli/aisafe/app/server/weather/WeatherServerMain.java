package eapli.aisafe.app.server.weather;

import eapli.aisafe.bootstrap.MasterUsersBootstrapper;
import eapli.aisafe.persistence.impl.jpa.PersistenceContext;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.weather.application.ConsultWeatherDataController;

public class WeatherServerMain {

    public static void main(String[] args) {

        final var repositories = PersistenceContext.repositories();

        new MasterUsersBootstrapper(
                repositories.users(),
                repositories.airTransportCompanies(),
                repositories.customerCollaborators(),
                repositories.airControlAreas()
        ).bootstrap();

        final AuthService authService = new AuthService(repositories.users());

        final ConsultWeatherDataController controller =
                new ConsultWeatherDataController(
                        authService,
                        repositories.airControlAreas(),
                        repositories.weatherData()
                );

        final WeatherTcpServer server = new WeatherTcpServer(5050, authService, controller);

        try {

            server.start();

        } catch (final Exception e) {

            System.err.println("Error starting Weather TCP Server: "
                    + e.getMessage());
        }
    }
}