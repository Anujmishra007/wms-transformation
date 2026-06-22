package com.maersk.wms.printing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Printing Service Application.
 *
 * Provides label generation, print job management, and printer management capabilities.
 *
 * Bounded Contexts:
 * - Label Generation: Label creation, rendering, template management
 * - Print Job Management: Job lifecycle, queuing, execution
 * - Printer Management: Printer registration, status, queue configuration
 */
@SpringBootApplication
public class PrintingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrintingServiceApplication.class, args);
    }
}
