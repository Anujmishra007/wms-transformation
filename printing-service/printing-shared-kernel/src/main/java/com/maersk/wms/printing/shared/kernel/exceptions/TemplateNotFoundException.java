package com.maersk.wms.printing.shared.kernel.exceptions;

/**
 * Exception thrown when a template is not found.
 */
public class TemplateNotFoundException extends PrintingException {

    public TemplateNotFoundException(String templateKey) {
        super("TEMPLATE_NOT_FOUND", "Template not found: " + templateKey);
    }
}
