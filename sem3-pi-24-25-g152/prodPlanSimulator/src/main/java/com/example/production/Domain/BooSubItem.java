package com.example.production.Domain;

/**
 * Represents a sub-item within a BooEntry.
 * This class encapsulates the basic information of a sub-item, including its ID and quantity.
 */
public class BooSubItem {
    /**
     * The unique identifier of the item
     */
    private final String itemId;

    /**
     * The quantity of the item
     */
    private final int itemQuantity;

    /**
     * Constructs a new BooSubItem with the specified ID and quantity
     * 
     * @param itemId       The unique identifier of the item
     * @param itemQuantity The quantity of the item
     */
    public BooSubItem(String itemId, int itemQuantity) {
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
    }

    /**
     * Returns the item's unique identifier
     * 
     * @return The item ID
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the quantity of the item
     * 
     * @return The item quantity
     */
    public int getItemQuantity() {
        return itemQuantity;
    }
}