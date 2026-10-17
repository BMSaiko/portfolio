package com.example.production.Utils;

import com.example.production.Utils.FlowDependency;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class FlowDependencyTest {

    @Test
    void testRecordFlow() {
        FlowDependency flowDependency = new FlowDependency();
        flowDependency.recordFlow(1, "ws1");
        flowDependency.recordFlow(2, "ws1");
        flowDependency.recordFlow(1, "ws2");

        Map<String, Map<Integer, Integer>> dependencies = flowDependency.getWorkstationDependencies();

        assertEquals(2, dependencies.size());
        assertTrue(dependencies.containsKey("ws1"));
        assertTrue(dependencies.containsKey("ws2"));

        Map<Integer, Integer> ws1Articles = dependencies.get("ws1");
        assertEquals(2, ws1Articles.size());
        assertEquals(1, ws1Articles.get(1));
        assertEquals(1, ws1Articles.get(2));

        Map<Integer, Integer> ws2Articles = dependencies.get("ws2");
        assertEquals(1, ws2Articles.size());
        assertEquals(1, ws2Articles.get(1));
    }
}
