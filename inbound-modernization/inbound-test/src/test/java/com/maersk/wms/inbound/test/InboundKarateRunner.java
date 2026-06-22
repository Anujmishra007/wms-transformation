package com.maersk.wms.inbound.test;

import com.intuit.karate.junit5.Karate;

public class InboundKarateRunner {

    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }

    @Karate.Test
    Karate testReceiptCreate() {
        return Karate.run("features/receiving/receipt-create").relativeTo(getClass());
    }

    @Karate.Test
    Karate testReceiptReceive() {
        return Karate.run("features/receiving/receipt-receive").relativeTo(getClass());
    }
}
