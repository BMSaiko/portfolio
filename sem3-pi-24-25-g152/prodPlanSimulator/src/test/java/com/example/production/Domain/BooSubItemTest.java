package com.example.production.Domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BooSubItemTest {
    @Test
    public void testBooSubItemConstructorAndGetters() {
        // Dados de teste
        String itemId = "ITEM001";
        int itemQuantity = 10;

        // Criar a instância de BooSubItem
        BooSubItem subItem = new BooSubItem(itemId, itemQuantity);

        // Verificar os valores retornados pelos getters
        assertEquals(itemId, subItem.getItemId());
        assertEquals(itemQuantity, subItem.getItemQuantity());
    }

    @Test
    public void testBooSubItemWithZeroQuantity() {
        // Dados de teste
        String itemId = "ITEM002";
        int itemQuantity = 0;

        // Criar a instância de BooSubItem
        BooSubItem subItem = new BooSubItem(itemId, itemQuantity);

        // Verificar os valores retornados pelos getters
        assertEquals(itemId, subItem.getItemId());
        assertEquals(itemQuantity, subItem.getItemQuantity());
    }

    @Test
    public void testBooSubItemWithNegativeQuantity() {
        // Dados de teste
        String itemId = "ITEM003";
        int itemQuantity = -5;

        // Criar a instância de BooSubItem
        BooSubItem subItem = new BooSubItem(itemId, itemQuantity);

        // Verificar os valores retornados pelos getters
        assertEquals(itemId, subItem.getItemId());
        assertEquals(itemQuantity, subItem.getItemQuantity());
    }

    @Test
    public void testBooSubItemWithNullItemId() {
        // Criar a instância de BooSubItem com itemId nulo
        BooSubItem subItem = new BooSubItem(null, 10);

        // Verificar os valores retornados pelos getters
        assertNull(subItem.getItemId());
        assertEquals(10, subItem.getItemQuantity());
    }
}
