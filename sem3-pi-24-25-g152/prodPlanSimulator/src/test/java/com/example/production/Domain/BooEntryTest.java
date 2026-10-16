package com.example.production.Domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BooEntryTest {
    @Test
    public void testBooEntryConstructorAndGetters() {

        String opId = "OP123";
        String itemId = "ITEM456";
        int itemQuantity = 10;

        BooSubOperation subOp1 = new BooSubOperation("SUBOP1", 5);
        BooSubOperation subOp2 = new BooSubOperation("SUBOP2", 3);
        List<BooSubOperation> subOperations = Arrays.asList(subOp1, subOp2);

        BooSubItem subItem1 = new BooSubItem("SUBITEM1", 5);
        BooSubItem subItem2 = new BooSubItem("SUBITEM2", 3);
        List<BooSubItem> subItems = Arrays.asList(subItem1, subItem2);

        BooEntry booEntry = new BooEntry(opId, itemId, itemQuantity, subOperations, subItems);

        assertEquals(opId, booEntry.getOpId());
        assertEquals(itemId, booEntry.getItemId());
        assertEquals(itemQuantity, booEntry.getItemQuantity());
        assertEquals(subOperations, booEntry.getSubOperations());
        assertEquals(subItems, booEntry.getSubItems());
    }

    @Test
    public void testBooEntryWithEmptyLists() {

        String opId = "OP789";
        String itemId = "ITEM101";
        int itemQuantity = 0;

        List<BooSubOperation> subOperations = Arrays.asList();
        List<BooSubItem> subItems = Arrays.asList();

        BooEntry booEntry = new BooEntry(opId, itemId, itemQuantity, subOperations, subItems);

        assertEquals(opId, booEntry.getOpId());
        assertEquals(itemId, booEntry.getItemId());
        assertEquals(itemQuantity, booEntry.getItemQuantity());
        assertTrue(booEntry.getSubOperations().isEmpty());
        assertTrue(booEntry.getSubItems().isEmpty());
    }

    @Test
    public void testBooEntryWithNullValues() {

        BooEntry booEntry = new BooEntry(null, null, 0, null, null);

        assertNull(booEntry.getOpId());
        assertNull(booEntry.getItemId());
        assertEquals(0, booEntry.getItemQuantity());
        assertNull(booEntry.getSubOperations());
        assertNull(booEntry.getSubItems());
    }

    @Test
    public void testBooEntryWithSingleSubOperation() {

        String opId = "OP999";
        String itemId = "ITEM777";
        int itemQuantity = 15;

        BooSubOperation subOp1 = new BooSubOperation("SUBOP1", 8);
        List<BooSubOperation> subOperations = Arrays.asList(subOp1);

        BooSubItem subItem1 = new BooSubItem("SUBITEM1", 4);
        List<BooSubItem> subItems = Arrays.asList(subItem1);

        BooEntry booEntry = new BooEntry(opId, itemId, itemQuantity, subOperations, subItems);

        assertEquals(opId, booEntry.getOpId());
        assertEquals(itemId, booEntry.getItemId());
        assertEquals(itemQuantity, booEntry.getItemQuantity());
        assertEquals(1, booEntry.getSubOperations().size());
        assertEquals(subOp1, booEntry.getSubOperations().get(0));
        assertEquals(1, booEntry.getSubItems().size());
        assertEquals(subItem1, booEntry.getSubItems().get(0));
    }
}

