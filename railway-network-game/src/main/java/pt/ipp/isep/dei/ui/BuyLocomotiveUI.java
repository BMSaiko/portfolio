package pt.ipp.isep.dei.ui;

import pt.ipp.isep.dei.controller.BuyLocomotiveController;
import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.dto.AvailableLocomotiveDTO;
import pt.ipp.isep.dei.dto.LocomotivePurchaseRequestDTO;
import pt.ipp.isep.dei.dto.PlayerDTO;
import pt.ipp.isep.dei.dto.PurchaseResultDTO;

import java.util.List;

public class BuyLocomotiveUI {
    public void displayAvailableLocomotives(List<AvailableLocomotiveDTO> locomotives) {
    }

    public String selectLocomotive() {
        return null;
    }

    public LocomotivePurchaseRequestDTO getPurchaseRequest() {
        return null;
    }

    public void displayPurchaseResult(PurchaseResultDTO result) {
    }

    public void displayPlayerInfo(PlayerDTO player) {
    }

    public void displayLocomotiveDetails(AvailableLocomotiveDTO locomotive) {
    }

    public boolean confirmPurchase(AvailableLocomotiveDTO locomotive) {
        return false;
    }
}
