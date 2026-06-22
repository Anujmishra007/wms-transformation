# WMS Transformation

**Fulfillment by Maersk (FbM) Warehouse Management System - Microservices Architecture**

Event-driven warehouse management system with 12 microservices, migrating from legacy SQL Server stored procedures to modern cloud-native architecture.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                      WMS TRANSFORMATION ARCHITECTURE                                      │
├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                          │
│   ┌─────────────────────────────────┐         ┌─────────────────────────────────┐                       │
│   │       INBOUND DOMAIN            │         │       OUTBOUND DOMAIN           │                       │
│   │                                 │         │                                 │                       │
│   │  ┌───────────────────────────┐  │         │  ┌───────────────────────────┐  │                       │
│   │  │    putaway-service        │  │         │  │    order-service          │  │                       │
│   │  │  • Strategies             │  │         │  │  • Order                  │  │                       │
│   │  │  • Location Allocation    │  │         │  │  • Wave                   │  │                       │
│   │  │  • Algorithm              │  │         │  │                           │  │                       │
│   │  │  • Crossdocking          │◄─┼─────────┼──┤                           │  │                       │
│   │  └───────────────────────────┘  │   API   │  └───────────────────────────┘  │                       │
│   │             │                   │         │             │                   │                       │
│   │             │ Event             │         │             │ Event             │                       │
│   │             ▼                   │         │             ▼                   │                       │
│   │  ┌───────────────────────────┐  │         │  ┌───────────────────────────┐  │                       │
│   │  │    inbound-service        │  │         │  │ inventory-allocation-svc  │  │                       │
│   │  │  • Purchase Order         │  │         │  │  • Strategies             │  │                       │
│   │  │  • ASN                    │  │         │  │  • Order Allocation       │  │                       │
│   │  │  • OSDs                   │  │         │  │  • Algorithm              │  │                       │
│   │  │  • GRN                    │  │         │  │  • Unallocate / Release   │  │                       │
│   │  └───────────────────────────┘  │         │  └───────────────────────────┘  │                       │
│   │             │                   │         │             │                   │                       │
│   │             │ Event             │         │             │ Event             │                       │
│   │             ▼                   │         │             ▼                   │                       │
│   │  ┌───────────────────────────┐  │         │  ┌───────────────────────────┐  │                       │
│   │  │ inbound-operations-svc    │  │         │  │ picking-operations-svc    │  │                       │
│   │  │  • Receiving              │  │         │  │  • Pick Detail Progress   │  │                       │
│   │  │  • Returns Receipt        │  │         │  │  • Shorts Handling        │  │                       │
│   │  │  • Putaway to Location    │  │         │  │  • Cancel Handling        │  │                       │
│   │  │  • Crossdocking          │  │         │  │  • List Handling          │  │                       │
│   │  └───────────┬───────────────┘  │         │  └───────────┬───────────────┘  │                       │
│   └──────────────┼──────────────────┘         └──────────────┼──────────────────┘                       │
│                  │                                           │                                          │
│                  │         Event                             │                                          │
│                  ▼                                           ▼                                          │
│   ┌──────────────────────────────────────────────────────────────────────────────────────────┐         │
│   │                                                                                           │         │
│   │  ┌─────────────────────┐    ╔═══════════════════════════════════╗    ┌─────────────────┐ │         │
│   │  │  packing-ops-svc    │    ║      inventory-service            ║    │ inventory-ops   │ │         │
│   │  │  • Sorting          │    ║  ═══════════════════════════════  ║    │    -service     │ │         │
│   │  │  • Packing          │◄───║  HEART OF WMS (Event-Driven)      ║───►│  • Counting     │ │         │
│   │  │                     │    ║                                   ║    │  • Adjustment   │ │         │
│   │  └─────────────────────┘    ║  • Inventory (Create/Change/Del)  ║    │  • Movements    │ │         │
│   │         │                   ║  • Finalization                   ║    │  • Holds        │ │         │
│   │         │ API & Events      ║  • Nesting                        ║    └────────┬────────┘ │         │
│   │         │                   ║  • Searching                      ║             │          │         │
│   │         │                   ║  • Count Types                    ║             │ API &    │         │
│   │         ▼                   ║                                   ║             │ Events   │         │
│   │                             ║  Tables: LOTxLOCxID, ID, SKUxLOC  ║             │          │         │
│   │                             ╚═══════════════════════════════════╝             │          │         │
│   │                                           │                                   │          │         │
│   │                    CORE DOMAIN            │                                   │          │         │
│   └───────────────────────────────────────────┼───────────────────────────────────┼──────────┘         │
│                                               │                                   │                     │
│                                               │ Event                             │                     │
│                                               ▼                                   ▼                     │
│   ┌─────────────────────────────────────────────────────────────────────────────────────────────┐      │
│   │                                    COMMON SERVICES                                            │      │
│   │                                                                                               │      │
│   │  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐      │      │
│   │  │  master-data-service    │  │   printing-service      │  │ task-management-service │      │      │
│   │  │  • SKU (Dims, Lottable) │  │   • Label Format        │  │  • Task Lifecycle       │      │      │
│   │  │  • Warehouse Layout     │  │   • Generation          │  │  • Task Grouping        │      │      │
│   │  │  • Storers              │  │   • Printer Configs     │  │  • Task Orchestration   │      │      │
│   │  │  • Docks, Equipment     │  │                         │  │  • Task Prioritization  │      │      │
│   │  │  • Users                │  │                         │  │                         │      │      │
│   │  └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘      │      │
│   └─────────────────────────────────────────────────────────────────────────────────────────────┘      │
│                                                                                                          │
└──────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Microservices

### Core Domain (Heart of WMS)

| Service | Port | Description |
|---------|------|-------------|
| **inventory-service** | 8081 | Central inventory management - Create, Change, Remove, Finalize, Nest, Search |
| **inventory-operations-service** | 8082 | Counting, Adjustments, Movements, Holds |
| **packing-operations-service** | 8083 | Sorting and Packing operations |

### Inbound Domain

| Service | Port | Description |
|---------|------|-------------|
| **inbound-service** | 8084 | Purchase Order, ASN, OSDs, GRN |
| **inbound-operations-service** | 8085 | Receiving, Returns Receipt, Putaway execution |
| **putaway-service** | 8086 | Putaway strategies, Location allocation, Crossdocking |

### Outbound Domain

| Service | Port | Description |
|---------|------|-------------|
| **order-service** | 8087 | Order and Wave management |
| **inventory-allocation-service** | 8088 | Allocation strategies, Order allocation, Release |
| **picking-operations-service** | 8089 | Pick operations (FN839), Shorts handling |

### Common Services

| Service | Port | Description |
|---------|------|-------------|
| **master-data-service** | 8090 | SKU, Locations, Storers, Users, Equipment |
| **printing-service** | 8091 | Label generation, Printer management |
| **task-management-service** | 8092 | Task lifecycle, Grouping, Orchestration |

---

## Project Structure

```
wms-Transformation/
├── pom.xml                              # Parent POM (Maven multi-module)
├── README.md                            # This file
│
├── wms-common/                          # Shared utilities & frameworks
│   └── src/main/java/com/maersk/wms/common/
│       ├── config/                      # Common configurations
│       ├── domain/                      # BaseEntity, TenantContext
│       ├── event/                       # DomainEvent, EventPublisher
│       ├── exception/                   # WmsException
│       ├── plugin/                      # Plugin framework
│       ├── security/                    # JWT, tenant routing
│       ├── util/                        # Utilities
│       └── validation/                  # Custom validators
│
├── wms-event-contracts/                 # Shared event DTOs
│   └── src/main/java/com/maersk/wms/events/
│       ├── inventory/                   # InventoryCreated, InventoryAdjusted
│       ├── inbound/                     # ReceiptCompleted, PutawayCompleted
│       ├── outbound/                    # PickConfirmed, OrderCreated
│       └── common/                      # MasterDataUpdated
│
├── inventory-service/                   # CORE - Heart of WMS
├── inventory-operations-service/        # CORE - Counting, Adjustments
├── packing-operations-service/          # CORE - Sorting, Packing
│
├── inbound-service/                     # INBOUND - PO, ASN, GRN
├── inbound-operations-service/          # INBOUND - Receiving, Returns
├── putaway-service/                     # INBOUND - Putaway strategies
│
├── order-service/                       # OUTBOUND - Orders, Waves
├── inventory-allocation-service/        # OUTBOUND - Allocation
├── picking-operations-service/          # OUTBOUND - Pick operations
│
├── master-data-service/                 # COMMON - Master data
├── printing-service/                    # COMMON - Labels
└── task-management-service/             # COMMON - Task management
```

---

## Event-Driven Architecture

### Kafka Topics

| Topic | Producer | Consumers | Events |
|-------|----------|-----------|--------|
| `inventory.events` | inventory-service | allocation, picking, order | InventoryCreated, InventoryAdjusted, InventoryMoved |
| `picking.events` | picking-operations | inventory, order, packing | PickConfirmed, PickShorted, TaskCompleted |
| `inbound.events` | inbound-ops | inventory, putaway | ReceiptCompleted, ReturnsProcessed |
| `outbound.events` | order, allocation | inventory, picking | OrderCreated, WaveReleased, AllocationCompleted |
| `common.events` | master-data, task-mgmt | All services | MasterDataUpdated, TaskAssigned |

### Event Flow Example: Pick Confirmation

```
┌──────────────────────┐
│ picking-operations   │
│ (FN839 Piece Pick)   │
└──────────┬───────────┘
           │
           │ PickConfirmedEvent
           ▼
    ┌──────────────┐
    │    Kafka     │
    │ picking.events│
    └──────┬───────┘
           │
    ┌──────┴──────┬──────────────┬───────────────┐
    ▼             ▼              ▼               ▼
┌─────────┐ ┌──────────┐  ┌───────────┐  ┌────────────┐
│inventory│ │ order    │  │ packing   │  │ task-mgmt  │
│-service │ │-service  │  │-ops-svc   │  │ -service   │
└─────────┘ └──────────┘  └───────────┘  └────────────┘
    │            │              │               │
    │ Reduce     │ Update       │ Track         │ Complete
    │ inventory  │ order        │ items for     │ task
    │            │ progress     │ packing       │
    ▼            ▼              ▼               ▼
```

---

## Plugin Framework

Replaces legacy extension SPs with configurable Java plugins.

### Plugin Types (from FN839 Analysis)

| Plugin Type | Replaces | Variants | Purpose |
|-------------|----------|----------|---------|
| GetTaskPlugin | rdt_839GetTaskSP01-17 | 14 | Task retrieval logic |
| DecodePlugin | rdt_839DecodeSP01-09 | 7 | Barcode decoding |
| ExtendedValidationPlugin | rdt_839ExtValidSP01-16 | 16 | SKU/Lot/Serial validation |
| ConfirmPlugin | rdt_839Confirm01-15 | 14 | Pick confirmation |
| ExtendedUpdatePlugin | rdt_839ExtUpd01-10 | 11 | Post-confirm processing |
| ExtendedInfoPlugin | rdt_839ExtInfo01-12 | 12 | Display info |

### Plugin Configuration (YAML)

```yaml
# config/clients/nike.yaml
client: NIKE
region: AMERICAS

plugins:
  getTask:
    type: StandardGetTaskPlugin
    config:
      sortByLogicalLocation: true
      zoneFiltering: enabled

  decode:
    type: GS1128DecodePlugin
    config:
      extractQuantity: true
      extractLot: true

  confirm:
    type: LabelGenConfirmPlugin
    config:
      generateShippingLabel: true
```

---

## Technology Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.5.3 |
| **Database** | SQL Server (existing WMS DB) |
| **Messaging** | Apache Kafka |
| **Workflow** | Temporal.io |
| **Build** | Maven |
| **Containerization** | Docker, Jib |
| **Observability** | Micrometer, OpenTelemetry |
| **API Docs** | SpringDoc OpenAPI |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Kafka (or use Docker Compose)

### Build

```bash
# Build all modules
mvn clean install

# Build specific service
mvn clean install -pl inventory-service -am
```

### Run

```bash
# Start infrastructure (Kafka, SQL Server)
docker-compose up -d

# Run a service
cd inventory-service
mvn spring-boot:run
```

---

## Migration from Legacy

This project modernizes the legacy WMS stored procedures:

| Legacy | Modern |
|--------|--------|
| 12,139 Stored Procedures | 12 Microservices |
| rdtfnc_* (orchestrators) | REST APIs + Workflows |
| rdt_839* (extension SPs) | Plugin Framework |
| nsp* (Non-RDT SPs) | Domain Services |
| RDTMOBREC (session state) | Stateless APIs + JWT |
| Direct SQL | Event-Driven |

---

## Documentation

| Document | Location |
|----------|----------|
| FN839 API Modernization | `/docs/FN839_API_Modernization.md` |
| FN839 Domain R&D | `/docs/FN839_Domain_RnD.md` |
| Legacy to API Mapping | `/docs/FN839_Legacy_to_API_Mapping.xlsx` |
| Architecture Strategy | `AGENTIC_MODERNIZATION_STRATEGY.md` |
| Phase 2 Details | `PHASE2_MODERNIZE_DETAILS.md` |

---

## License

Proprietary - Maersk Internal Use Only
