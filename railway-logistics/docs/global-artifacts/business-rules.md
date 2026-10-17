# Business Rules and Constraints

| ID        | Description                                                                                                      | Category                 | Source / User Story |
|-----------|------------------------------------------------------------------------------------------------------------------|--------------------------|---------------------|
| **BR01**  | Each railway line has exactly one owner responsible for all its segments.                                        | Railway Infrastructure   | USBD01–USBD03       |
| **BR02**  | A line segment belongs to only one line and must specify gauge, max weight, and speed limit.                     | Railway Infrastructure   | USBD02              |
| **BR03**  | Locomotives can only operate on segments compatible with their power type (diesel/electric) and gauge.           | Rolling Stock            | 2.2.2               |
| **BR04**  | Wagons cannot exceed their maximum payload or volume capacity.                                                   | Rolling Stock            | 2.2.2               |
| **BR05**  | Freight trains must only use connected stations through existing line segments.                                  | Operations               | 2.2.4               |
| **BR06**  | Line segments classified as “electrified” count toward the total km of electrified network.                      | Planning                 | USBD14              |
| **BR07**  | Each station must connect to at least one line segment.                                                          | Infrastructure Integrity | USBD15              |
| **BR08**  | Every data import (CSV) must reject records with invalid SKUs, wagons, quantities, or missing timestamps.        | Data Validation          | USEI01              |
| **BR09**  | Each `boxId` in the warehouse must be unique.                                                                    | Data Validation          | USEI01              |
| **BR10**  | Perishable goods follow FEFO; non-perishables follow FIFO.                                                       | Warehouse Operations     | USEI01              |
| **BR11**  | Dispatch operations must always consume stock from the “front” of the bay list (FEFO/FIFO order).                | Warehouse Operations     | USEI01              |
| **BR12**  | Empty bays remain registered in the system; only boxes are deleted.                                              | Warehouse Management     | USEI01              |
| **BR13**  | Order processing follows the sequence: priority → dueDate → orderId → lineNo.                                    | Order Allocation         | USEI02              |
| **BR14**  | In strict mode, only fully allocated order lines are eligible; in partial mode, partial allocations are allowed. | Order Allocation         | USEI02              |
| **BR15**  | Stock quantities cannot become negative during allocation.                                                       | Inventory Integrity      | USEI02              |
| **BR16**  | Trolleys used in picking must never exceed their maximum weight capacity.                                        | Picking Operations       | USEI03              |
| **BR17**  | Each picking heuristic (FF, FFD, BFD) must output the number of trolleys used and utilization percentage.        | Picking Operations       | USEI03              |
| **BR18**  | Picking paths must minimize total walking distance using Strategy A (Ascending) or B (Nearest-Neighbour).        | Picking Optimization     | USEI04              |
| **BR19**  | Returned goods must first enter quarantine before being restocked or discarded.                                  | Returns Management       | USEI05              |
| **BR20**  | Returns are inspected in reverse arrival order (latest first, ties by returnId ASC).                             | Returns Management       | USEI05              |
| **BR21**  | Damaged or expired returns must be discarded and not re-enter inventory.                                         | Returns Management       | USEI05              |
| **BR22**  | Restocked items from returns must create new box records with ID prefix “RET-”.                                  | Returns Management       | USEI05              |
| **BR23**  | All inspection results must be logged with timestamp, returnId, SKU, action, and quantities.                     | Auditing                 | USEI05              |
| **BR24**  | The database must enforce integrity constraints (foreign keys, unique keys, non-null values).                    | Data Integrity           | USBD03–USBD04       |
| **BR25**  | All system operations must validate business rules before persisting or updating data.                           | System Integrity         | Non-functional      |
| **BR26**  | All entities (lines, locomotives, wagons, operators) must maintain traceability and referential integrity.       | Data Governance          | Non-functional      |
| **BR27**  | The system must keep logs of all operational actions for auditing purposes.                                      | Logging                  | Non-functional      |

---
