package eapli.aisafe.app.company.console.connection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Simple TCP client used by the air transport company console application.
 *
 * <p>
 * This client manages the socket connection to the AISafe server and provides a
 * small command-based communication API. Commands are sent as UTF-8 text lines,
 * and each command expects a single-line response from the server.
 * </p>
 *
 * <p>
 * The protocol message format itself is built by protocol helper classes such
 * as {@code AtccProtocol}; this class is only responsible for transport-level
 * communication.
 * </p>
 */
public class TcpClient {

    /**
     * Socket connection to the server.
     */
    private Socket socket;

    /**
     * Reader used to receive UTF-8 responses from the server.
     */
    private BufferedReader reader;

    /**
     * Writer used to send UTF-8 commands to the server.
     */
    private BufferedWriter writer;

    /**
     * Opens a TCP connection to the server.
     *
     * @param host server hostname or IP address
     * @param port server TCP port
     * @throws IOException if the connection cannot be established
     */
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    /**
     * Sends one command to the server and waits for a single-line response.
     *
     * <p>
     * The command is written as one UTF-8 line and flushed immediately. The
     * method then blocks until the server sends one response line.
     * </p>
     *
     * @param command protocol command to send
     * @return server response line
     * @throws IOException if the client is not connected or if communication
     *                     fails
     */
    public String sendCommand(String command) throws IOException {
        if (socket == null || socket.isClosed()) throw new IOException("Not connected.");
        writer.write(command);
        writer.newLine();
        writer.flush();
        return reader.readLine();
    }

    /**
     * Closes the TCP client resources.
     *
     * <p>
     * The method closes the reader, writer and socket if they were opened.
     * Individual close failures are ignored because the client is already being
     * shut down.
     * </p>
     */
    public void disconnect() {
        try { if (reader != null) reader.close(); } catch (IOException ignored) { }
        try { if (writer != null) writer.close(); } catch (IOException ignored) { }
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) { }
    }
}