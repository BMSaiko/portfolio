package pt.ipp.isep.dei.controller;

/**
 * Represents a response from a controller to the UI.
 * @param <T> the type of data contained in the response
 */
public class Response<T> {
    private final boolean success;
    private final String message;
    private final T data;

    /**
     * Private constructor for Response.
     * 
     * @param success whether the request was successful
     * @param message a message describing the result
     * @param data the data returned
     */
    private Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a successful response with data.
     * 
     * @param <T> the type of data
     * @param data the data to include in the response
     * @return a successful response
     */
    public static <T> Response<T> ok(T data) {
        return new Response<>(true, "Success", data);
    }

    /**
     * Creates a successful response with a message.
     * 
     * @param message the success message
     * @return a successful response
     */
    public static <T> Response<T> ok(String message) {
        return new Response<>(true, message, null);
    }

    /**
     * Creates an error response with a message.
     * 
     * @param <T> the type of data
     * @param message the error message
     * @return an error response
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(false, message, null);
    }

    /**
     * Checks if the response was successful.
     * 
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the message from the response.
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the data from the response.
     * 
     * @return the data
     */
    public T getData() {
        return data;
    }
}
