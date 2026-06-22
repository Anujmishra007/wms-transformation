package com.maersk.wms.inventory.test;

import com.intuit.karate.junit5.Karate;

public class InventoryKarateRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }

    @Karate.Test
    Karate testAdjustment() {
        return Karate.run("features/adjustment").relativeTo(getClass());
    }

    @Karate.Test
    Karate testTransfer() {
        return Karate.run("features/transfer").relativeTo(getClass());
    }

    @Karate.Test
    Karate testHold() {
        return Karate.run("features/hold").relativeTo(getClass());
    }
}
