# US078 - Air Transport Company Collaborator Remote Access

> **📋 Cloud deployment:** See [`CLOUD_TESTING.md`](../global-artifacts/CLOUD_TESTING.md) for cloud testing and [`CLOUD_VPN_ACCESS.md`](../global-artifacts/CLOUD_VPN_ACCESS.md) for VPN access from home.

## 1. Context

Sprint 3 requirement for RCOMP. The goal is to provide ATCCs with remote access through a dedicated TCP-based client application (Air Transport Company App). All ATCC operations (US070-US077) must be available remotely with no direct database access from the client.

Depends on: US030 (auth), US070-US077 (operations), US090 (UDP logging), US091 (visualization).
**All dependencies satisfied:** US030 provides AuthService, US070-US072 + US073-US074 have controllers, US090 is fully integrated.

### 1.1 List of Issues

**Analysis:** TCP architecture, operation scope, DTOs, auth over TCP.
**Design:** TCP server/client structure, protocol, DTOs, controller integration.
**Implement:** TCP server, Company App client, DTOs, US090 logging.
**Test:** TCP communication, authorization, operations over TCP, concurrent clients.

---

## 2. Requirements

**US078** As an Air Transport Company Collaborator, I want to remotely access the system using the Air Transport Company App.

### Acceptance Criteria

- **US078.1:** TCP-based network client application required.
- **US078.2:** Client interaction limited to TCP connection; no direct DB access.
- **US078.3:** All ATCC user stories remotely available.
- **US078.4:** Authentication and authorization enforced.
- **US078.5:** Server supports multiple concurrent ATCC connections.
- **US078.6:** Remote access events logged via UDP (US090).

### Dependencies

- **US030** (Authentication), **US070-US077** (operations)
- **US090** (UDP logging), **US091** (visualization)
- **US044/US086** (same TCP architecture pattern)

### ATCC Operations Remotely Available

| Operation | US | Description |
|---|---|---|
| Add Aircraft | US070 | Register new aircraft in fleet |
| Decommission Aircraft | US071 | Retire aircraft |
| List Fleet | US072 | List company fleet |
| List Fleet by Model | US072a | Filter by model |
| List Fleet by Maker | US072b | Filter by manufacturer |
| List Fleet by Capacity | US072c | Filter by capacity |
| List Fleet by Age | US072d | Filter by age |
| Create Flight Route | US073 | Register new route |
| Delete Flight Route | US074 | Deactivate route |
| Add Pilot | US075 | Register new pilot |
| List Pilot Roster | US076 | List pilots |
| Remove Pilot | US077 | Deactivate pilot |

---

## 3. Analysis

### 3.1 Understanding

The Air Transport Company App is a TCP client connecting to an embedded TCP server. No direct DB access — requests are routed through EAPLI controllers, responses returned as DTOs. Architecture mirrors US044 and US086, sharing AuthService and UDP logging (US090).

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| TCP over HTTP | Project mandates TCP-based client |
| DTOs for transfer | Avoids exposing domain entities |
| Shared auth | Reuses existing AuthService |
| Dedicated port per service | Isolates connections |
| UDP logging | Fire-and-forget to US090 |
| Thread per client | Supports concurrency |

### 3.3 Communication Flow

```
Air Transport Company App (TCP Client)
  1. Connect -> 2. Auth -> 3. Send requests -> 4. Receive DTOs -> 5. Disconnect
              | TCP connection |
              v
AISafe TCP Server (aisafe.app.server)
  Accept -> Auth via AuthService -> Route to EAPLI controller -> Return DTO
              | UDP datagram |
              v
UdpLogClient -> Remote Accesses Logging Server (US090)
```

### 3.4 Protocol

**Auth:** `AUTH <email> <password>` -> `AUTH_OK` / `AUTH_FAIL`
**Operation:** `<OPERATION> <params>` -> `OK <DTO_JSON>` / `ERROR <msg>`
**Logout:** `LOGOUT` -> `BYE`

---

## 4. Design

### 4.1 Server

```
aisafe.app.server/
+-- AtccTcpServer.java           # Accept, spawn handler
+-- AtccConnectionHandler.java   # Per-client thread
+-- AtccProtocol.java            # Parser/serializer
```

### 4.2 Client

```
aisafe.app.company.console/
+-- AirTransportCompanyApp.java  # Entry point
+-- presentation/ (fleet/, routes/, pilots/ UIs)
+-- DTO/ (AircraftDTO, FleetAircraftDTO, FlightRouteDTO, PilotDTO, ResultDTO)
```

### 4.3 TCP Ports

| Service | Port |
|---|---|
| US044 (Weather) | 2001 |
| US078 (ATCC) | 2002 |
| US086 (Pilot) | 2003 |

### 4.4 Authorization

Server enforces `AIR_TRANSPORT_COMPANY_COLLABORATOR` role at authentication.

### 4.5 UDP Logging (US090)

```java
remoteLoggingService.logEvent(username, clientIp, clientPort,
    ServiceType.US78, EventType.LOGIN_SUCCESS);
```

---

## 5. Demonstration

### Start Server
```bash
mvn clean install
mvn -pl aisafe.app.server exec:java
```

### Start Client
```bash
mvn -pl aisafe.app.company.console exec:java
```

### Auth
```
Air Transport Company App | Port: 2002
Email: atcc@airline.com | Password: ********
[AUTH_OK] Welcome, Airline Collaborator
```

### Example: Add Aircraft
```
> Fleet Management > Add Aircraft
Registration: CS-TUA | Model: B738 | Seats: 180 | Crew: 6
[OK] Aircraft CS-TUA added to fleet.
```

### UDP Log Events
```
2026-05-25T14:30:22|atcc@airline.com|127.0.0.1|54321|US78|LOGIN_SUCCESS
2026-05-25T14:45:00|atcc@airline.com|127.0.0.1|54321|US78|LOGOUT
```

---

## 6. Tests

| Test | Expected Result |
|---|---|
| TCP connection to port 2002 | Connection accepted |
| Valid ATCC credentials | AUTH_OK, menu displayed |
| Invalid credentials | AUTH_FAIL, connection closed |
| Non-ATCC user role | AUTH_FAIL, no access |
| Add Aircraft over TCP | Aircraft persisted |
| List Fleet over TCP | DTO list returned |
| No direct DB in client | No JDBC/JPA in client code |
| UDP logging on login | LOGIN_SUCCESS at US090 |
| UDP logging on logout | LOGOUT at US090 |
| Client disconnect | Server logs DISCONNECT |
| Concurrent clients | Independent sessions |

---

## 7. Observations

- US030 provides auth; US070-US077 define operations; US090 logs; US091 visualizes
- Server separated from DB layer -- all access through EAPLI controllers
- DTOs prevent domain entity serialization over network
- Future: JSON protocol, TLS encryption, heartbeat, per-request auth

