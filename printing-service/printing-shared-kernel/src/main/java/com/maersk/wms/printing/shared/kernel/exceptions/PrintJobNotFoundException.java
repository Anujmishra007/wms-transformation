package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Exception thrown when a print job is not found.
 */
public class PrintJobNotFoundException extends PrintingException {

    public PrintJobNotFoundException(String printJobKey) {
        super("PRINT_JOB_NOT_FOUND", "Print job not found: " + printJobKey);
    }
}
