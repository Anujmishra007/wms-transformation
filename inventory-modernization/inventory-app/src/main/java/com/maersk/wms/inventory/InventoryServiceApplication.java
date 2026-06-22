package com.maersk.wms.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Inventory Service.
 * Heart of the WMS - manages LOTxLOCxID inventory tracking.
 *
 * Bounded Contexts:
 * - Inventory Core: Central inventory operations
 * - Inventory Lifecycle: Create, Change, Remove, Finalization
 * - Inventory Structure: Nesting (Pallet → Case → Inner Pack → Each)
 * - Inventory Discovery: Search and query operations
 * - Inventory Controls: Count type configuration
 */
@SpringBootApplication(scanBasePackages = "com.maersk.wms.inventory")
@EnableAsync
@EnableScheduling
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
