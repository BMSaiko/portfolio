# US091 - Remote Accesses Logging Visualization

**Author:** Diogo Azevedo (1240895)
**Sprint:** 3
**Course Unit:** RCOMP

---

## 1. Context

This user story belongs to Sprint 3 and is mandatory for RCOMP. It extends the Remote Accesses Logging Server built in US090 by adding an HTTP server that allows an Administrator to visualize remote access logs through a standard web browser, with automatic page updates using AJAX.

**Dependencies:**
- **US090** — provides the UDP log receiver, LogStore, and ActiveSessionTracker that this US reads from. US091 is implemented as an extension of the same standalone application (`aisafe-logging-server`).
- **US044, US078, US086** — the remote access services that generate the log events. Required for end-to-end testing with real data; manual UDP injection can be used for isolated testing.

### List of Issues


Analysis:  Requirement summary, dependencies on US090/US044/US078, HTTP endpoint design, AJAX update flow 

Design: HttpLogServer architecture, JSON API format, static HTML resource loading, shared state with US090 

Implementation: HttpLogServer.java, index.html (recent events page), active.html (active sessions page) 

Test: Local testing with PowerShell UDP injection, browser verification, AJAX auto-refresh validation 

---

## 2. Requirements

**US091** As Administrator, I want to view the logs stored at the Remote Accesses Logging Server.

**Acceptance Criteria:**

- **US091.1** The Remote Accesses Logging Server application contains an HTTP server to provide status pages to clients running a standard web browser.
- **US091.2** At least two pages should be available: one presenting the list of the last recorded events, and another presenting a list of the remote users currently active.
- **US091.3** Both pages must be kept updated to represent the current standing.
- **US091.4** AJAX must be used to update the presented data without the need of reloading the page.

---

## 3. Analysis

The HTTP visualization server is embedded in the same process as the UDP logging server (US090), sharing the same `LogStore` and `ActiveSessionTracker` instances. This avoids any synchronization or inter-process communication overhead.

The Administrator opens a browser and navigates to the HTTP server. The pages use AJAX polling (every 3 seconds) to fetch updated data from JSON API endpoints, updating the DOM without a full page reload.

### 3.1 Architecture

```
+-----------------------------+         +---------------------------+
|  Remote Accesses Logging    |         |  Administrator Browser    |
|  Server (Cloud VS)          |         |                           |
|                             |  HTTP   |  http://<VS-IP>:8080/     |
|  +----------+  +---------+  | <-----> |  (Recent Events)          |
|  | LogStore |  | Session |  |         |                           |
|  |          |  | Tracker |  |  HTTP   |  http://<VS-IP>:8080/     |
|  +----+-----+  +----+----+  | <-----> |  active                   |
|       |             |       |         |  (Active Sessions)        |
|  +----v-------------v----+  |         +---------------------------+
|  |   HttpLogServer       |  |
|  |   (port 8080)         |  |
|  +-----------------------+  |
+-----------------------------+
```

### 3.2 HTTP Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/` | GET | HTML page — list of last recorded events |
| `/active` | GET | HTML page — list of currently active sessions |
| `/api/logs` | GET | JSON array of last 100 log events (used by AJAX) |
| `/api/active` | GET | JSON array of active sessions (used by AJAX) |

### 3.3 AJAX Update Flow

```
Browser                        HttpLogServer
   |                                 |
   |--- GET /                ------> |
   |<-- index.html           ------- |
   |                                 |
   | (every 3 seconds)               |
   |--- GET /api/logs        ------> |
   |<-- JSON array           ------- |
   | update DOM (no reload)          |
```

---

## 4. Design

### 4.1 Class Overview

US091 is implemented across two files added to the `aisafe-logging-server` module:

| File | Location | Purpose |
|---|---|---|
| `HttpLogServer.java` | `eapli.aisafe.logging` | HTTP server — handles all routes, serves HTML pages and JSON API |
| `index.html` | `src/main/resources/` | Browser page — recent log events, AJAX polling |
| `active.html` | `src/main/resources/` | Browser page — active sessions, AJAX polling |

The `HttpLogServer` is instantiated and started by `RemoteAccessesLoggingServer` (the main entry point, part of US090), receiving shared references to `LogStore` and `ActiveSessionTracker`.

### 4.2 HttpLogServer Design

```
HttpLogServer
├── start()                  — creates HttpServer on configured port, registers handlers
├── stop()                   — gracefully shuts down the HTTP server
├── handleRoot()             — serves index.html from classpath resources
├── handleActivePage()       — serves active.html from classpath resources
├── handleApiLogs()          — serializes LogStore entries to JSON
├── handleApiActive()        — serializes ActiveSessionTracker sessions to JSON
├── serveResource()          — loads HTML files from classpath (src/main/resources/)
└── sendResponse()           — writes HTTP response with correct headers
```

### 4.3 JSON Formats

**GET /api/logs** — array of log event objects:
```json
[
  {
    "timestamp": "2026-06-05T14:25:00",
    "username": "joao.silva",
    "clientIp": "192.168.1.10",
    "clientPort": 54321,
    "service": "US078",
    "event": "LOGIN_SUCCESS"
  }
]
```

**GET /api/active** — array of active session objects:
```json
[
  {
    "username": "maria.costa",
    "clientIp": "192.168.1.11",
    "clientPort": 54322,
    "service": "US044",
    "loginTime": "2026-06-05T14:26:00"
  }
]
```

### 4.4 Key Design Decisions

- **Embedded HTTP server:** `com.sun.net.httpserver.HttpServer` (included in the JDK) — no external dependencies required.
- **Static HTML files in resources:** HTML pages are stored in `src/main/resources/` and loaded via the classpath, keeping presentation separate from Java logic.
- **AJAX polling:** `setInterval(fetch, 3000)` — simple, reliable, no WebSocket complexity needed.
- **Shared in-memory state:** `HttpLogServer` reads directly from the same `LogStore` and `ActiveSessionTracker` instances used by the UDP receiver — no additional storage or IPC needed.
- **CORS header:** `Access-Control-Allow-Origin: *` set on all API responses to allow browser requests.

---

## 5. Implementation

### 5.1 Files

**New files in `aisafe-logging-server`:**

| # | File | Purpose |
|---|---|---|
| 1 | `eapli/aisafe/logging/HttpLogServer.java` | HTTP server implementation |
| 2 | `src/main/resources/index.html` | Recent events page (HTML + AJAX) |
| 3 | `src/main/resources/active.html` | Active sessions page (HTML + AJAX) |

**No modifications** to existing files were required. `RemoteAccessesLoggingServer.java` already instantiates and starts `HttpLogServer` as part of US090's Phase 4.

### 5.2 How the Pages Work

Both pages follow the same pattern:

1. Page loads from `/` or `/active`
2. JavaScript immediately calls `fetch('/api/logs')` or `fetch('/api/active')`
3. JSON response is parsed and rendered as an HTML table
4. `setInterval` repeats the fetch every 3 seconds
5. Table is replaced in-place — no page reload

Event badges on the logs page are colour-coded:

| Event | Colour |
|---|---|
| LOGIN_SUCCESS | Green |
| LOGIN_FAILURE | Red |
| LOGOUT | Blue |
| DISCONNECT | Yellow |

---

## 6. Testing

### 6.1 How to Run Locally

**Step 1 — Start the Logging Server:**

Open `RemoteAccessesLoggingServer.java` in IntelliJ and click the green triangle next to `main`. Expected output:
```
INFO: Logging Server ready. UDP:9999 HTTP:8080
```

**Step 2 — Inject test events (second terminal, Windows PowerShell):**

```powershell
$udp = New-Object System.Net.Sockets.UdpClient
$udp.Connect("localhost", 9999)

$msgs = @(
    "2026-06-05T14:25:00|joao.silva|192.168.1.10|54321|US078|LOGIN_SUCCESS",
    "2026-06-05T14:26:00|maria.costa|192.168.1.11|54322|US044|LOGIN_SUCCESS",
    "2026-06-05T14:27:00|pedro.neves|192.168.1.12|54323|US086|LOGIN_FAILURE",
    "2026-06-05T14:28:00|joao.silva|192.168.1.10|54321|US078|LOGOUT"
)

foreach ($msg in $msgs) {
    $bytes = [Text.Encoding]::UTF8.GetBytes($msg)
    $udp.Send($bytes, $bytes.Length)
    Start-Sleep -Milliseconds 500
}
$udp.Close()
```

**Step 3 — Open the browser:**

| URL | Expected result |
|---|---|
| `http://localhost:8080/` | Table with 4 events, colour-coded badges |
| `http://localhost:8080/active` | Only `maria.costa` listed as active |

**Step 4 — Verify AJAX:** Leave the browser open and inject a new event. The table must update within 3 seconds without a page reload.

### 6.2 Test Scenarios

| # | Scenario | Expected Result |
|---|---|---|
| T1 | LOGIN_SUCCESS event injected | Appears in logs page; session appears in active page |
| T2 | LOGIN_FAILURE event injected | Appears in logs page with red badge; no session created |
| T3 | LOGOUT event injected | Appears in logs page; session removed from active page |
| T4 | DISCONNECT event injected | Appears in logs page; session removed from active page |
| T5 | Page left open for 10 seconds | Table updates automatically without browser reload |
| T6 | Navigate between pages | Links between `/` and `/active` work correctly |

---

## 7. Observations

- The HTTP server uses a fixed thread pool of 4 threads (`Executors.newFixedThreadPool(4)`), which is sufficient for the expected load of a single administrator.
- For the cloud deployment, the HTTP server runs on port 8080 of the Cloud VS. Access requires either being on the ISEP network or connected via VPN (`deinet.dei.isep.ipp.pt:443`). See [`CLOUD_VPN_ACCESS.md`](../global-artifacts/CLOUD_VPN_ACCESS.md).
- End-to-end testing with real TCP clients (US044, US078, US086) does not require any changes to this US — the server accepts UDP datagrams from any source on port 9999.