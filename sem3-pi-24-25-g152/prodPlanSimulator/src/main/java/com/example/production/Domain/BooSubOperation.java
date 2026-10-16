package com.example.production.Domain;

/**
 * Represents a sub-operation within a BooEntry.
 * This class defines the structure for a basic operation unit that is part of a Bill of Operations (BOO).
 */
public class BooSubOperation {
    /**
     * The unique identifier for the operation
     */
    private final String opId;

    /**
     * The quantity required for this operation
     */
    private final int opQuantity;

    /**
     * Constructs a new BooSubOperation with specified operation ID and quantity
     * @param opId the unique identifier for the operation
     * @param opQuantity the quantity required for this operation
     */
    public BooSubOperation(String opId, int opQuantity) {
        this.opId = opId;
        this.opQuantity = opQuantity;
    }

    /**
     * Gets the operation identifier
     * @return the operation ID
     */
    public String getOpId() {
        return opId;
    }

    /**
     * Gets the operation quantity
     * @return the quantity required for this operation
     */
    public int getOpQuantity() {
        return opQuantity;
    }
}
