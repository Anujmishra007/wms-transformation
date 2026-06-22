package com.maersk.wms.masterdata.shared.kernel.exceptions;

/**
 * Exception thrown when equipment is not found.
 */
public class EquipmentNotFoundException extends MasterDataException {

    public EquipmentNotFoundException(String equipmentKey) {
        super("EQUIPMENT_NOT_FOUND", "Equipment not found: " + equipmentKey);
    }
}
