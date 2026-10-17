package com.example.production.Domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorkstationTest {

    @Test
    void testWorkstationCreation() {
        Workstation ws = new Workstation("ws1", "CUT", 10);
        assertEquals("ws1", ws.getWorkstationId());
        assertEquals("CUT", ws.getOperationName());
        assertEquals(10, ws.getTime());
        assertEquals(0, ws.getTotalUsageTime());
    }

    @Test
    void testAddUsageTime() {
        Workstation ws = new Workstation("ws1", "CUT", 10);
        ws.addUsageTime(5);
        assertEquals(5, ws.getTotalUsageTime());
        ws.addUsageTime(7);
        assertEquals(12, ws.getTotalUsageTime());
    }
}
