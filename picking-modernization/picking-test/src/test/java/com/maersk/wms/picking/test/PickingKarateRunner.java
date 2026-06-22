package com.maersk.wms.picking.test;

import com.intuit.karate.junit5.Karate;

/**
 * Karate test runner for FN839 Picking E2E tests.
 */
public class PickingKarateRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run()
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testGetTask() {
        return Karate.run("features/gettask")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testDecode() {
        return Karate.run("features/decode")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testConfirm() {
        return Karate.run("features/confirm")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testE2EFlow() {
        return Karate.run("features/e2e")
                .relativeTo(getClass());
    }
}
