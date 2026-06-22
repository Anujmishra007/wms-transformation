package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Exception thrown when printer is offline.
 */
public class PrinterOfflineException extends PrintingException {

    public PrinterOfflineException(String printerKey) {
        super("PRINTER_OFFLINE", "Printer is offline: " + printerKey);
    }

    public PrinterOfflineException(String printerKey, String reason) {
        super("PRINTER_OFFLINE", "Printer is offline: " + printerKey + " - " + reason);
    }
}
