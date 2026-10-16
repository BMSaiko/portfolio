package com.example.production.Domain;

import java.util.List;

/**
 * Represents an entry from the boo.csv file.
 * This class contains information about a production operation including its ID,
 * associated item, quantity, sub-operations and sub-items.
 */
public class BooEntry {
    /** Operation ID */
    private final String opId;
    /** Item ID associated with this operation */
    private final String itemId;
    /** Quantity of items to be produced */
    private final int itemQuantity;
    /** List of sub-operations that compose this operation */
    private final List<BooSubOperation> subOperations;
    /** List of sub-items required for this operation */
    private final List<BooSubItem> subItems;

    /**
     * Constructs a new BooEntry with the specified parameters.
     * 
     * @param opId The operation identifier
     * @param itemId The item identifier
     * @param itemQuantity The quantity of items
     * @param subOperations List of sub-operations
     * @param subItems List of sub-items
     */
    public BooEntry(String opId, String itemId, int itemQuantity, List<BooSubOperation> subOperations, List<BooSubItem> subItems) {
        this.opId = opId;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.subOperations = subOperations;
        this.subItems = subItems;
    }

    /**
     * Gets the operation ID.
     * 
     * @return The operation ID
     */
    public String getOpId() {
        return opId;
    }

    /**
     * Gets the item ID.
     * 
     * @return The item ID
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Gets the item quantity.
     * 
     * @return The item quantity
     */
    public int getItemQuantity() {
        return itemQuantity;
    }

    /**
     * Gets the list of sub-operations.
     * 
     * @return List of sub-operations
     */
    public List<BooSubOperation> getSubOperations() {
        return subOperations;
    }

    /**
     * Gets the list of sub-items.
     * 
     * @return List of sub-items
     */
    public List<BooSubItem> getSubItems() {
        return subItems;
    }
}