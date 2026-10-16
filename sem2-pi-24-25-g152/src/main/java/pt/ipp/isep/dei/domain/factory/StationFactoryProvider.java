package pt.ipp.isep.dei.domain.factory;

public class StationFactoryProvider {
    public static StationFactory getFactory(String stationType) {
        switch (stationType.toUpperCase()) {
            case "DEPOT":
                return new DepotStationFactory();
            case "PASSENGER":
                return new PassengerStationFactory();
            case "TERMINAL":
                return new TerminalStationFactory();
            default:
                throw new IllegalArgumentException("Unknown station type: " + stationType);
        }
    }
}
