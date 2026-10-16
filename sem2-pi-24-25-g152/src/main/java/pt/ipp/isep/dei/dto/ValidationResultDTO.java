package pt.ipp.isep.dei.dto;

public class ValidationResultDTO {
    private boolean isValid;
    private String errorMessage;
    private String suggestedName;

    public ValidationResultDTO(boolean isValid, String errorMessage, String suggestedName) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.suggestedName = suggestedName;
    }

    public boolean isValid() { return isValid; }

    // Getters and setters
    public String getErrorMessage() { return errorMessage; }
    public String getSuggestedName() { return suggestedName; }
    public void setValid(boolean valid) { isValid = valid; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setSuggestedName(String suggestedName) { this.suggestedName = suggestedName; }
}

