package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Exception thrown when a print job fails.
 */
public class PrintJobFailedException extends PrintingException {

    public PrintJobFailedException(String printJobKey, String reason) {
        super("PRINT_JOB_FAILED", "Print job failed: " + printJobKey + " - " + reason);
    }

    public PrintJobFailedException(String printJobKey, String reason, Throwable cause) {
        super("PRINT_JOB_FAILED", "Print job failed: " + printJobKey + " - " + reason, cause);
    }
}
