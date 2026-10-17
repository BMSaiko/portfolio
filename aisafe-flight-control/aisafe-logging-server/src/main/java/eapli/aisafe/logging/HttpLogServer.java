package eapli.aisafe.logging;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import eapli.aisafe.logging.model.ActiveSession;
import eapli.aisafe.logging.model.LogEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class HttpLogServer {

    private static final Logger LOGGER = Logger.getLogger(HttpLogServer.class.getName());

    private final int port;
    private final LogStore logStore;
    private final ActiveSessionTracker sessionTracker;
    private HttpServer server;

    public HttpLogServer(int port, LogStore logStore, ActiveSessionTracker sessionTracker) {
        this.port = port;
        this.logStore = logStore;
        this.sessionTracker = sessionTracker;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleRoot);
        server.createContext("/api/logs", this::handleApiLogs);
        server.createContext("/api/active", this::handleApiActive);
        server.createContext("/active", this::handleActivePage);
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));
        server.start();
        LOGGER.info("[HTTP Server] Started on port " + port);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        serveResource(exchange, "index.html");
    }

    private void handleActivePage(HttpExchange exchange) throws IOException {
        serveResource(exchange, "active.html");
    }

    private void handleApiLogs(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }
        List<LogEntry> entries = logStore.getEntries(100);
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) json.append(",");
            json.append(entries.get(i).toJson());
        }
        json.append("]");
        sendResponse(exchange, 200, json.toString(), "application/json");
    }

    private void handleApiActive(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            return;
        }
        Collection<ActiveSession> sessions = sessionTracker.getActiveSessions();
        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (ActiveSession s : sessions) {
            if (i++ > 0) json.append(",");
            json.append(s.toJson());
        }
        json.append("]");
        sendResponse(exchange, 200, json.toString(), "application/json");
    }

    private void serveResource(HttpExchange exchange, String filename) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                sendResponse(exchange, 404, "Not found", "text/plain");
                return;
            }
            byte[] bytes = is.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }


    private String buildPage(String title, String apiEndpoint, String tbodyId,
                             String heading, String[] columns) {
        StringBuilder colHeaders = new StringBuilder();
        for (String col : columns) {
            colHeaders.append("<th>").append(col).append("</th>");
        }
        boolean isLogPage = apiEndpoint.contains("logs");
        String otherLink = isLogPage
                ? "<p><a href='/active'>View Active Sessions</a></p>"
                : "<p><a href='/'>View All Logs</a></p>";
        return "<!DOCTYPE html><html><head><title>" + title + "</title>"
                + "<meta charset='UTF-8'>"
                + "<style>body{font-family:monospace;margin:20px;background:#1a1a2e;color:#eee}"
                + "h1{color:#e94560}table{border-collapse:collapse;width:100%}"
                + "th,td{border:1px solid #333;padding:6px 10px;text-align:left}"
                + "th{background:#16213e;color:#e94560}tr:nth-child(even){background:#16213e}"
                + "a{color:#4ecca3;text-decoration:none}</style></head>"
                + "<body><h1>" + heading + "</h1>"
                + otherLink
                + "<table><tr>" + colHeaders + "</tr><tbody id='" + tbodyId + "'></table>"
                + "<script>"
                + "function fetchData(){"
                + "fetch('/" + apiEndpoint + "').then(r=>r.json()).then(data=>{"
                + "document.getElementById('" + tbodyId + "').innerHTML="
                + "data.reverse().map(e=>'<tr><td>'+"
                + buildColumnAccessors(columns)
                + "+'</td></tr>').join('');});}"
                + "fetchData(); setInterval(fetchData,3000);</script></body></html>";
    }

    private String buildColumnAccessors(String[] columns) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) sb.append("+'</td><td>'+");
            String col = columns[i].toLowerCase().replace(" ", "");
            if (col.contains("timestamp") || col.contains("logintime")) sb.append("(e.timestamp||e.loginTime)");
            else if (col.contains("username")) sb.append("e.username");
            else if (col.contains("clientip") || col.contains("ip")) sb.append("e.clientIp");
            else if (col.contains("port")) sb.append("e.clientPort");
            else if (col.contains("service")) sb.append("e.service");
            else if (col.contains("event")) sb.append("e.event");
            else sb.append("'" + columns[i] + "'");
        }
        return sb.toString();
    }
}
