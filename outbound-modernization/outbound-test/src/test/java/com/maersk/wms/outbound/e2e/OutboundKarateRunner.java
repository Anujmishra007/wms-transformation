package com.maersk.wms.outbound.e2e;

import com.intuit.karate.junit5.Karate;

/**
 * Karate test runner for outbound E2E tests.
 */
class OutboundKarateRunner {

    @Karate.Test
    Karate testOrders() {
        return Karate.run("orders/order-create", "orders/order-allocate", "orders/order-release")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testWaves() {
        return Karate.run("waves/wave-create", "waves/wave-release")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testShipments() {
        return Karate.run("shipments/shipment-create", "shipments/ship-confirm")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testE2E() {
        return Karate.run("e2e/order-to-ship")
                .relativeTo(getClass());
    }
}
