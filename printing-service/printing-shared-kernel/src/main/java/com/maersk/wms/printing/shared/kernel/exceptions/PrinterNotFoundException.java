package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Exception thrown when a printer is not found.
 */
public class PrinterNotFoundException extends PrintingException {

    public PrinterNotFoundException(String printerKey) {
        super("PRINTER_NOT_FOUND", "Printer not found: " + printerKey);
    }
}
