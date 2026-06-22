package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends MasterDataException {

    public UserNotFoundException(String userKey) {
        super("USER_NOT_FOUND", "User not found: " + userKey);
    }
}
