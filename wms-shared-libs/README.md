# WMS Shared Libraries

Central shared libraries used across all WMS microservices. This module follows the DDD **Shared Kernel** pattern to eliminate code duplication and ensure consistency.

## Module Structure

```
wms-shared-libs/
├── wms-shared-kernel/      # Core domain primitives (identifiers, value objects, exceptions)
├── wms-common/             # Common utilities (tenant context, base exceptions)
├── wms-domain-events/      # Event publishing infrastructure
├── wms-event-contracts/    # Event definitions for inter-service communication
├── wms-infrastructure/     # Infrastructure utilities (multi-tenant datasource)
└── wms-test-support/       # Test utilities (testcontainers, karate support)
```

## Modules

### wms-shared-kernel

**Purpose**: Core domain primitives shared across all microservices.

**Contains**:
- **Identifiers** (26 types): Strongly-typed entity identifiers
  - `SkuKey`, `LocationKey`, `LpnKey`, `StorerKey`, `UserKey`
  - `OrderKey`, `WarehouseKey`, `WaveKey`, `LotKey`, `ReceiptKey`
  - `ZoneKey`, `DeviceKey`, `TaskKey`, `AllocationKey`, `ShipmentKey`
  - `PoKey`, `AsnKey`, `InventoryKey`, `TransactionKey`, `CartonKey`
  - `PickKey`, `AisleKey`, `DockKey`, `EquipmentKey`, `HoldKey`, `WorkQueueKey`

- **Value Objects** (5 types): Immutable domain concepts
  - `Quantity` - Amount with UOM, arithmetic operations
  - `Money` - Monetary value with currency
  - `Weight` - Weight with unit conversion
  - `Dimensions` - L×W×H with volume calculation
  - `DateRange` - Time periods with factory methods

- **Exceptions** (7 types): Domain-specific exceptions
  - `WmsException` - Base exception
  - `EntityNotFoundException` - Entity lookup failures
  - `ValidationException` - Validation errors with field details
  - `ConcurrencyException` - Optimistic locking conflicts
  - `InsufficientQuantityException` - Quantity shortages
  - `InvalidOperationException` - Business rule violations
  - `UnauthorizedException` - Security violations

**Usage**:
```xml
<dependency>
    <groupId>com.maersk.wms</groupId>
    <artifactId>wms-shared-kernel</artifactId>
    <version>${project.version}</version>
</dependency>
```

### wms-event-contracts

**Purpose**: Defines all domain events for inter-service communication via Kafka.

**Contains**:
- `BaseDomainEvent` - Base class with metadata (eventId, tenant, correlation)
- Domain-specific events organized by bounded context:
  - `inbound/ReceiptEvents` - ReceiptCreated, ReceiptLineReceived, PutawayCompleted
  - `outbound/OrderEvents` - OrderCreated, OrderReleased, OrderShipped
  - `inventory/InventoryEvents` - InventoryCreated, InventoryAllocated, InventoryTransferred
  - `picking/PickingEvents` - PickTaskCreated, PickConfirmed, ShortPickReported
  - `packing/PackingEvents` - CartonCreated, ItemPacked, PackingCompleted
  - `task/TaskEvents` - TaskCreated, TaskAssigned, TaskCompleted
  - `masterdata/MasterDataEvents` - SkuChanged, LocationChanged

**Usage**:
```xml
<dependency>
    <groupId>com.maersk.wms</groupId>
    <artifactId>wms-event-contracts</artifactId>
    <version>${project.version}</version>
</dependency>
```

### wms-domain-events

**Purpose**: Event publishing infrastructure (Kafka integration).

**Contains**:
- `EventPublisher` - Interface for publishing events
- `KafkaEventPublisher` - Kafka implementation
- `EventTopicResolver` - Topic routing strategy

### wms-common

**Purpose**: Common utilities for multi-tenant operations.

**Contains**:
- `TenantContext` - Thread-local tenant information
- `TenantInfo` - Tenant data holder
- `BusinessException` - Legacy exception (use WmsException for new code)

### wms-infrastructure

**Purpose**: Infrastructure components for data access.

**Contains**:
- `MultiTenantDataSource` - Tenant-aware datasource routing

### wms-test-support

**Purpose**: Testing utilities and fixtures.

**Contains**:
- Testcontainers setup for SQL Server
- Karate integration for API testing
- Common test fixtures

## Migration Guide

### Migrating from Service-Specific Identifiers

Each microservice had its own copies of identifiers (e.g., `inbound.shared.kernel.identifiers.SkuKey`). These should be migrated to use the central shared kernel.

**Before** (service-specific):
```java
import com.maersk.wms.inventory.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inventory.shared.kernel.identifiers.LocationKey;
```

**After** (central shared kernel):
```java
import com.maersk.wms.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.shared.kernel.identifiers.LocationKey;
```

### Migration Steps

1. **Update pom.xml** - Add dependency on wms-shared-kernel
2. **Update imports** - Change from service-specific to shared kernel package
3. **Remove duplicate files** - Delete service-specific identifier classes
4. **Test** - Verify compilation and run tests

### Identifier Availability

| Identifier | In wms-shared-kernel | Service-Specific |
|------------|---------------------|------------------|
| SkuKey | Yes | Migrate |
| LocationKey | Yes | Migrate |
| LpnKey | Yes | Migrate |
| InventoryKey | Yes | Migrate |
| OrderKey | Yes | Migrate |
| TaskKey | Yes | Migrate |
| PickTaskKey | No | Keep in picking-shared-kernel |
| AdjustmentKey | No | Keep in inventory-shared-kernel |

**Rule**: Cross-cutting identifiers go in `wms-shared-kernel`. Domain-specific identifiers stay in service modules.

## Best Practices

### 1. Use Strongly-Typed Identifiers
```java
// Good - type-safe
public Inventory findByKey(InventoryKey key);

// Bad - primitive obsession
public Inventory findByKey(String key);
```

### 2. Use Value Objects for Domain Concepts
```java
// Good - rich domain model
public void adjust(Quantity quantity, AdjustmentReason reason);

// Bad - primitive types
public void adjust(BigDecimal qty, String uom, String reason);
```

### 3. Use Shared Events for Inter-Service Communication
```java
// Publish using wms-event-contracts
InventoryEvents.InventoryAllocated event = InventoryEvents.InventoryAllocated.builder()
    .allocationKey(allocationKey)
    .orderKey(orderKey)
    .build();
eventPublisher.publish(event);
```

### 4. Handle Exceptions Consistently
```java
// Throw domain-specific exceptions
if (inventory == null) {
    throw new EntityNotFoundException("Inventory", inventoryKey.value());
}

if (available.isLessThan(requested)) {
    throw new InsufficientQuantityException(skuKey, requested, available);
}
```

## Dependency Graph

```
wms-shared-kernel (no dependencies)
       ↓
wms-event-contracts (depends on shared-kernel)
       ↓
wms-domain-events (depends on event-contracts)
       ↓
wms-common (depends on shared-kernel)
       ↓
wms-infrastructure (depends on common)
       ↓
wms-test-support (depends on shared-kernel, event-contracts)
```

## Adding New Shared Types

1. **Evaluate**: Is this type used by 2+ microservices?
2. **Location**: Cross-cutting → shared-kernel, Domain-specific → service module
3. **Design**: Follow existing patterns (records for identifiers, classes for value objects)
4. **Document**: Update this README with the new type
5. **Migrate**: Update dependent services to use the shared type
