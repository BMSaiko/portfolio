package eapli.aisafe.tcpprotocol.dto;

import java.io.Serializable;

/**
 * Generic result DTO for TCP protocol responses.
 * Carries either a success payload or an error message.
 */
public class ResultDTO implements Serializable {
    public final boolean success;
    public final String message;
    public final Object data;

    private ResultDTO(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static ResultDTO ok(String message) {
        return new ResultDTO(true, message, null);
    }

    public static ResultDTO ok(String message, Object data) {
        return new ResultDTO(true, message, data);
    }

    public static ResultDTO error(String message) {
        return new ResultDTO(false, message, null);
    }

    public boolean isSuccess() { return success; }
}
