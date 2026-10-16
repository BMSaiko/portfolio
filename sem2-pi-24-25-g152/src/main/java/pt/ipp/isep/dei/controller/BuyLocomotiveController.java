package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.domain.locomotive.Train;
import pt.ipp.isep.dei.dto.AvailableLocomotiveDTO;
import pt.ipp.isep.dei.dto.LocomotivePurchaseRequestDTO;
import pt.ipp.isep.dei.dto.PlayerDTO;
import pt.ipp.isep.dei.dto.PurchaseResultDTO;
import pt.ipp.isep.dei.repository.ILocomotiveRepository;
import pt.ipp.isep.dei.repository.IRailwayNetworkRepository;
import pt.ipp.isep.dei.repository.ITrainRepository;

import java.util.List;

public class BuyLocomotiveController {
    private ILocomotiveRepository locomotiveRepository;
    private ITrainRepository trainRepository;
    private IRailwayNetworkRepository railwayNetworkRepository;

    public BuyLocomotiveController() {
    }

    public List<AvailableLocomotiveDTO> getAvailableLocomotives() {
        return null;
    }

    public PurchaseResultDTO buyLocomotive(LocomotivePurchaseRequestDTO request) {
        return null;
    }

    public PlayerDTO getPlayerInfo() {
        return null;
    }

    public AvailableLocomotiveDTO getLocomotiveDetails(String locomotiveId) {
        return null;
    }

    private boolean validatePurchase(Player player, Locomotive locomotive) {
        return false;
    }

    private boolean calculateAffordability(Locomotive locomotive, double playerFunds) {
        return false;
    }

    private Train createTrainFromLocomotive(Locomotive locomotive) {
        return null;
    }

    private double estimateOperatingCost(Locomotive locomotive) {
        return 0.0;
    }
}
