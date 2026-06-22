package com.maersk.wms.picking.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application for FN839 Picking Microservice.
 */
@SpringBootApplication(scanBasePackages = "com.maersk.wms.picking")
public class PickingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickingApplication.class, args);
    }
}
