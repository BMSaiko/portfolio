package pt.ipp.isep.dei.repository;

public class Repositories {
    public static Repositories getInstance() { return null; }

    private static Repositories instance;
    private SimulationRepository simulationRepository;
    private RailwayLineRepository railwayLineRepository;
    private LocomotiveRepository locomotiveRepository;
    private TrainRepository trainRepository;
    private CargoDeliveryRepository cargoDeliveryRepository;
    private SaveGameRepository saveGameRepository;
    private IMapRepository mapRepository;
    private IStationRepository stationRepository;
    private IBuildingUpgradeRepository buildingUpgradeRepository;
    private IRailwayNetworkRepository railwayNetworkRepository;



    private Repositories() {
        this.simulationRepository = new SimulationRepository();
    //  this.railwayLineRepository = new RailwayLineRepository();
        this.locomotiveRepository = new LocomotiveRepository();
        this.trainRepository = new TrainRepository();
        this.cargoDeliveryRepository = new CargoDeliveryRepository();
        this.mapRepository = new MapRepository();
        this.stationRepository = new StationRepository();
    }



    public SimulationRepository getSimulationRepository() {
        return simulationRepository;
    }

    public RailwayLineRepository getRailwayLineRepository() {
        return railwayLineRepository;
    }

    public LocomotiveRepository getLocomotiveRepository() {
        return locomotiveRepository;
    }

    public TrainRepository getTrainRepository() {
        return trainRepository;
    }

    public CargoDeliveryRepository getCargoDeliveryRepository() {
        return cargoDeliveryRepository;
    }

    public IMapRepository getMapRepository() {
        return mapRepository;
    }

    public IStationRepository getStationRepository() {
        return stationRepository;
    }

    public IBuildingUpgradeRepository getBuildingUpgradeRepository() {
        return buildingUpgradeRepository;
    }

    public IRailwayNetworkRepository getRailwayNetworkRepository() {
        return null;
    }



}
