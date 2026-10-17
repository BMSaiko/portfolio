package com.example.production.Domain;

/**
 * Represents an operation within the production process.
 * This class encapsulates the basic information of a production operation,
 * including its unique identifier and name.
 */
public class Operation {
    /** The unique identifier of the operation */
    private final String opId;
    /** The name of the operation */
    private final String opName;

    /**
     * Constructs an Operation with the specified ID and name.
     *
     * @param opId   The unique identifier of the operation
     * @param opName The name of the operation
     */
    public Operation(String opId, String opName) {
        this.opId = opId;
        this.opName = opName;
    }

    /**
     * Gets the operation's unique identifier.
     *
     * @return The operation ID
     */
    public String getOpId() {
        return opId;
    }

    /**
     * Gets the operation's name.
     *
     * @return The operation name
     */
    public String getOpName() {
        return opName;
    }

    /**
     * Returns a string representation of the Operation.
     *
     * @return A string containing the operation's ID and name
     */
    @Override
    public String toString() {
        return "Operation{" +
                "opId='" + opId + '\'' +
                ", opName='" + opName + '\'' +
                '}';
    }
}