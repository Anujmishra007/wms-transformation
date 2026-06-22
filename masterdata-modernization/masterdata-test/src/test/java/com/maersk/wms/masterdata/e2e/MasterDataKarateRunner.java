package com.maersk.wms.masterdata.e2e;

import com.intuit.karate.junit5.Karate;

/**
 * Karate test runner for master data E2E tests.
 */
class MasterDataKarateRunner {

    @Karate.Test
    Karate testItems() {
        return Karate.run("items/item-create", "items/item-update", "items/item-search")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testLocations() {
        return Karate.run("locations/location-create", "locations/location-search")
                .relativeTo(getClass());
    }
}
