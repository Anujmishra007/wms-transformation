package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Exception thrown when a label is not found.
 */
public class LabelNotFoundException extends PrintingException {

    public LabelNotFoundException(String labelKey) {
        super("LABEL_NOT_FOUND", "Label not found: " + labelKey);
    }
}
