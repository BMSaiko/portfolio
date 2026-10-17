package com.example.production.Domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a workstation in the production process.
 */
public class Workstation {
    private static final Logger logger = LogManager.getLogger(Workstation.class);

    private final String workstationId;
    private final String operationName;
    private final int time;
    private final AtomicInteger totalUsageTime;

    /**
     * Constructs a Workstation with the specified ID, operation name, and processing time.
     *
     * @param workstationId  The unique identifier of the workstation.
     * @param operationName  The name of the operation performed by the workstation.
     * @param time           The time required to complete the operation.
     */
    public Workstation(String workstationId, String operationName, int time) {
        this.workstationId = workstationId;
        this.operationName = operationName;
        this.time = time;
        this.totalUsageTime = new AtomicInteger(0);
        logger.debug("Workstation {} created for operation '{}', processing time: {} seconds.", workstationId, operationName, time);
    }

    // Getters
    public String getWorkstationId() {
        return workstationId;
    }

    public String getOperationName() {
        return operationName;
    }

    public int getTime() {
        return time;
    }

    public int getTotalUsageTime() {
        return totalUsageTime.get();
    }

    /**
     * Adds to the total usage time of the workstation.
     *
     * @param time The time to be added to the total usage.
     */
    public void addUsageTime(int time) {
        int newTotal = totalUsageTime.addAndGet(time);
        logger.debug("Workstation {} increased usage time by {} seconds. Total usage time: {} seconds.", workstationId, time, newTotal);
    }
}
