package pt.ipp.isep.dei.dto;

public class PurchaseResultDTO {
    private boolean success;
    private String message;
    private String trainId;
    private String locomotiveId;
    private double remainingFunds;
    private TrainDTO newTrain;

    public PurchaseResultDTO(boolean success, String message, String trainId,
                             String locomotiveId, double remainingFunds, TrainDTO newTrain) {
    }

    public boolean isSuccess() {
        return false;
    }
}
