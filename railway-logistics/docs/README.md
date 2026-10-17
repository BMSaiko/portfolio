# Warehouse Management System - User Stories Documentation

This folder contains the complete documentation for the warehouse and fright management system.

## User Stories

### USEI01 - Warehouse Management - Box Allocation and FEFO/FIFO Dispatch
**Description**: Box allocation to warehouse bays with FEFO (First-Expired-First-Out) and FIFO (First-In-First-Out) dispatch rules.

**Key Components**:
- Warehouse, Bay, Box, Item domain classes
- WarehouseAllocator service for box allocation
- CsvImportService for data loading
- FEFO/FIFO ordering implemented in Box.compareTo()

**Documentation**: [USEI01](ESINF/USEI01/USEI01-README.md)

---

### USEI02 - Order Allocation with STRICT and PARTIAL Modes
**Description**: Process customer orders by allocating warehouse inventory to order lines with support for STRICT (all-or-nothing) and PARTIAL (partial fulfillment) modes.

**Key Components**:
- Order, OrderLine, AllocationFragment, LineEligibility domain classes
- OrderAllocationService for allocation logic
- AllocationResult containing eligibilities and allocations
- Inventory snapshot mechanism for allocation tracking

**Documentation**: [USEI02](ESINF/USEI02/USEI02-README.md)

---

### USEI03 - Trolley Packing with Multiple Heuristics
**Description**: Pack allocation fragments into trolleys using bin packing heuristics (First Fit, First Fit Decreasing, Best Fit Decreasing) for efficient warehouse picking operations.

**Key Components**:
- Trolley, PickingPlan, WeightedAllocation domain classes
- TrolleyPackingService implementing three bin packing algorithms
- PackingHeuristic enum (FF, FFD, BFD)
- SKU weight caching for performance optimization

**Documentation**: [USEI03](ESINF/USEI03/USEI03-README.md)

---

## Global Artifacts

- [Domain Model](./global-artifacts/domain-model.puml)
- [Global Class Diagram](./global-artifacts/global-class-diagram.puml)
- [Glossary.md](./global-artifacts/glossary.md)
- [Business Rules.md](./global-artifacts/business-rules.md)

---

## Documentation Structure

Each user story follows this structure:

```shell
USEIXX/
├── USEINN-README.md
├── 01.requirements-engineering/
│   └── USEIXX-requirements.md
├── 02.analysis/
│   └── USEIXX-analysis.md
├── 03.design/
│   ├── USEIXX-design.md
│   └── puml/
│       ├── USEIXX-sequence-diagram.puml
│       └── USEIXX-class-diagram.puml
└── 04.tests-and-implementation/
    └── USEIXX-tests-and-implementation.md
```

---

## Implementation Summary

### USEI01 Implementation
- **Main Classes**: Warehouse, Bay, Box, WarehouseAllocator
- **Algorithm**: FEFO/FIFO sorting via Box.compareTo()
- **Tests**: WarehouseAllocatorTest.java (6 test methods)

### USEI02 Implementation
- **Main Classes**: OrderAllocationService, AllocationResult, LineEligibility
- **Algorithm**: Order sorting + inventory snapshot + STRICT/PARTIAL modes
- **Tests**: OrderAllocationServiceTest.java (9 test methods)

### USEI03 Implementation
- **Main Classes**: TrolleyPackingService, PickingPlan, Trolley, WeightedAllocation
- **Algorithms**: First Fit (FF), First Fit Decreasing (FFD), Best Fit Decreasing (BFD)
- **Tests**: TrolleyPackingServiceTest.java (30+ test methods)

---

