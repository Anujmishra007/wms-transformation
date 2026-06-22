package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Base exception for all master data domain errors.
 */
public class MasterDataException extends RuntimeException {

    private final String errorCode;

    public MasterDataException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MasterDataException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
