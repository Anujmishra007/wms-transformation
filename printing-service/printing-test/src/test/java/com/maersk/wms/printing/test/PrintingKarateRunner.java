package com.maersk.wms.printing.test;

import com.intuit.karate.junit5.Karate;

/**
 * Karate test runner for Printing Service E2E tests.
 */
public class PrintingKarateRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run()
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testLabelPrint() {
        return Karate.run("features/label-print")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testPrintJob() {
        return Karate.run("features/print-job")
                .relativeTo(getClass());
    }
}
