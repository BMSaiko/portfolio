package eapli.aisafe.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteAccessesLoggingServer {

    private static final Logger LOGGER = Logger.getLogger(RemoteAccessesLoggingServer.class.getName());

    public static final int DEFAULT_UDP_PORT = 9999;
    public static final int DEFAULT_HTTP_PORT = 8080;
    public static final String DEFAULT_LOG_FILE =
        System.getProperty("logging.file",
            System.getenv().getOrDefault("AISAFE_LOG_FILE",
                System.getProperty("os.name").toLowerCase().contains("win")
                    ? "remote_accesses.log"
                    : "/var/log/aisafe/remote_accesses.log"));

    public static void main(String[] args) {
        int udpPort = DEFAULT_UDP_PORT;
        int httpPort = DEFAULT_HTTP_PORT;
        String logFile = DEFAULT_LOG_FILE;

        if (args.length >= 1) {
            try { udpPort = Integer.parseInt(args[0]); } catch (NumberFormatException e) {
                LOGGER.warning("Invalid UDP port. Using default: " + DEFAULT_UDP_PORT);
            }
        }
        if (args.length >= 2) {
            try { httpPort = Integer.parseInt(args[1]); } catch (NumberFormatException e) {
                LOGGER.warning("Invalid HTTP port. Using default: " + DEFAULT_HTTP_PORT);
            }
        }
        if (args.length >= 3) {
            logFile = args[2];
        }

        LOGGER.info("Starting AISafe Remote Accesses Logging Server");
        LOGGER.info("UDP port: " + udpPort + ", HTTP port: " + httpPort + ", Log file: " + logFile);

        LogStore logStore = new LogStore(logFile);
        ActiveSessionTracker sessionTracker = new ActiveSessionTracker();
        UdpLogReceiver udpReceiver = new UdpLogReceiver(udpPort, logStore, sessionTracker);
        HttpLogServer httpServer = new HttpLogServer(httpPort, logStore, sessionTracker);

        Thread udpThread = new Thread(udpReceiver, "udp-receiver");
        udpThread.setDaemon(true);
        udpThread.start();

        try {
            httpServer.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start HTTP server", e);
            udpReceiver.stop();
            return;
        }

        LOGGER.info("Logging Server ready. UDP:" + udpPort + " HTTP:" + httpPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down Logging Server...");
            udpReceiver.stop();
            httpServer.stop();
        }));
    }
}
